/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.strategies;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.strategy.CartRestorationStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaloginaddon.controllers.ControllerConstants;
import de.hybris.platform.gigya.gigyaloginaddon.util.CookieUtils;
import de.hybris.platform.gigya.gigyaloginaddon.util.GigyaCookieValueGenerator;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.persistence.security.EJBPasswordEncoderNotFoundException;
import de.hybris.platform.servicelayer.security.spring.HybrisSessionFixationProtectionStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

public class DefaultGigyaAutoLoginStrategy implements AutoLoginStrategy {

	private static final Logger LOG = Logger.getLogger(DefaultGigyaAutoLoginStrategy.class);

	private CustomerFacade customerFacade;

	private GUIDCookieStrategy guidCookieStrategy;

	private RememberMeServices rememberMeServices;

	private BaseSiteService baseSiteService;

	private UserService userService;

	private CookieGenerator cookieGenerator;

	private UserDetailsService userDetailsService;

	private HybrisSessionFixationProtectionStrategy sessionFixationStrategy;

	private CartRestorationStrategy cartRestorationStrategy;

	@Override
	public void login(final String username, final String password, final HttpServletRequest request,
			final HttpServletResponse response) {
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null);
		final WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(request);
		token.setDetails(webAuthenticationDetails);
		try {
			UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
			final Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
					loadedUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin(username));
			getCustomerFacade().loginSuccess();

			// trigger cart restoration strategy
			getCartRestorationStrategy().restoreCart(request);

			getGuidCookieStrategy().setCookie(request, response);
			getRememberMeServices().loginSuccess(request, response, token);
			handleSessionManagement(username, request, response);
			sessionFixationStrategy.onAuthentication(authentication, request, response);
		} catch (final Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Failure during login", e);
		}
	}

	private void handleSessionManagement(final String username, final HttpServletRequest request,
			final HttpServletResponse response) throws EJBPasswordEncoderNotFoundException {

		final UserModel user = userService.getUserForUID(username);
		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null) {
			final GigyaConfigModel gigyaConfig = baseSite.getGigyaConfig();
			if (gigyaConfig != null) {
				handleSessionUsingGigyaConfig(username, request, response, user, baseSite, gigyaConfig);
			}
		}
	}

	private void handleSessionUsingGigyaConfig(final String username, final HttpServletRequest request,
			final HttpServletResponse response, UserModel user, BaseSiteModel baseSite, GigyaConfigModel gigyaConfig)
			throws EJBPasswordEncoderNotFoundException {
		final GigyaSessionConfigModel sessionConfig = gigyaConfig.getGigyaSessionConfig();
		if (sessionConfig != null && GigyaSessionLead.GIGYA == sessionConfig.getSessionLead()) {

			if (GigyaSessionType.SLIDING.equals(sessionConfig.getSessionType())) {
				// Create gltexp cookie
				final Cookie gltCookie = WebUtils.getCookie(request,
						ControllerConstants.GLT_COOKIE + gigyaConfig.getGigyaApiKey());
				final String cookieValue = GigyaCookieValueGenerator.generateCookieValue(gigyaConfig, gltCookie,
						sessionConfig.getSessionDuration());
				String cookieName = ControllerConstants.GLT_EXP_COOKIE + gigyaConfig.getGigyaApiKey();
				cookieGenerator.setCookieName(cookieName);
				cookieGenerator.setCookieMaxAge(sessionConfig.getSessionDuration());
				cookieGenerator.addCookie(response, cookieValue);
				CookieUtils.setSameSiteAttributeInCookie(response, cookieName,
						Config.getString(ControllerConstants.GLT_EXP_COOKIE_SAMESITE_ATTR_VAL,
								ControllerConstants.SAME_SITE_ATTRIBUTE_LAX));
			}

			final String cookieName = baseSite.getUid() + ControllerConstants.LOGION_TOKEN;
			final Cookie siteCookie = WebUtils.getCookie(request, baseSite.getUid() + ControllerConstants.LOGION_TOKEN);
			if (siteCookie == null) {
				// create site cookie
				UserManager.getInstance().storeLoginTokenCookie(cookieName, username,
						user.getSessionLanguage().getIsocode(), user.getEncodedPassword(), "/", null, true,
						sessionConfig.getSessionDuration(), response);
			}
			CookieUtils.setSameSiteAttributeInCookie(response, cookieName, ControllerConstants.SAME_SITE_ATTRIBUTE_LAX);
		}
	}

	public CustomerFacade getCustomerFacade() {
		return customerFacade;
	}

	@Required
	public void setCustomerFacade(final CustomerFacade customerFacade) {
		this.customerFacade = customerFacade;
	}

	public GUIDCookieStrategy getGuidCookieStrategy() {
		return guidCookieStrategy;
	}

	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
		this.guidCookieStrategy = guidCookieStrategy;
	}

	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	@Required
	public void setRememberMeServices(final RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CookieGenerator getCookieGenerator() {
		return cookieGenerator;
	}

	@Required
	public void setCookieGenerator(CookieGenerator cookieGenerator) {
		this.cookieGenerator = cookieGenerator;
	}

	public HybrisSessionFixationProtectionStrategy getSessionFixationStrategy() {
		return sessionFixationStrategy;
	}

	@Required
	public void setSessionFixationStrategy(HybrisSessionFixationProtectionStrategy sessionFixationStrategy) {
		this.sessionFixationStrategy = sessionFixationStrategy;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	@Required
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public CartRestorationStrategy getCartRestorationStrategy() {
		return cartRestorationStrategy;
	}

	@Required
	public void setCartRestorationStrategy(CartRestorationStrategy cartRestorationStrategy) {
		this.cartRestorationStrategy = cartRestorationStrategy;
	}

}

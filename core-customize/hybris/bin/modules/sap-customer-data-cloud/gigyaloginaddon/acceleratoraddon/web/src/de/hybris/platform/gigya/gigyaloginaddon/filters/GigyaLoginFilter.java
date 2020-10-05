/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaloginaddon.controllers.ControllerConstants;
import de.hybris.platform.gigya.gigyaloginaddon.util.CookieUtils;
import de.hybris.platform.gigya.gigyaloginaddon.util.GigyaCookieValueGenerator;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.persistence.security.EJBPasswordEncoderNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

/**
 * Filter to check if a valid gigya session exists and ensure that the commerce
 * session exists as well. If gigya session is completed then logout the user
 * from commerce as well
 */
public class GigyaLoginFilter extends OncePerRequestFilter {

	private BaseSiteService baseSiteService;

	private UserService userService;

	private RedirectStrategy redirectStrategy;

	private AutoLoginStrategy gigyaAutoLoginStrategy;

	private CookieGenerator cookieGenerator;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

		if (baseSite != null) {
			final GigyaConfigModel gigyaConfig = baseSite.getGigyaConfig();
			if (gigyaConfig != null) {
				final GigyaSessionConfigModel sessionConfig = gigyaConfig.getGigyaSessionConfig();
				if (sessionConfig != null && GigyaSessionLead.GIGYA == sessionConfig.getSessionLead()) {
					handleSessionWithGigyaConfig(request, response, baseSite, gigyaConfig, sessionConfig);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private void handleSessionWithGigyaConfig(HttpServletRequest request, HttpServletResponse response,
			BaseSiteModel baseSite, GigyaConfigModel gigyaConfig, GigyaSessionConfigModel sessionConfig)
			throws IOException {
		final UserModel user = userService.getCurrentUser();
		final GigyaSessionType sessionType = sessionConfig.getSessionType();
		boolean triggerRedirect = false;

		if (user != null && !userService.isAnonymousUser(user)) {
			triggerRedirect = handleActiveSession(request, response, baseSite, triggerRedirect, gigyaConfig,
					sessionConfig, sessionType);
		} else {
			// check glt and site cookies are valid and re-trigger login
			final Cookie gltCookie = WebUtils.getCookie(request,
					ControllerConstants.GLT_COOKIE + gigyaConfig.getGigyaApiKey());
			final Cookie siteCookie = WebUtils.getCookie(request, baseSite.getUid() + ControllerConstants.LOGION_TOKEN);
			if (gltCookie != null && siteCookie != null) {
				final CookieBasedLoginToken loginToken = getCookieBasedLoginToken(siteCookie);
				final User jaloUserFromToken = loginToken.getUser();
				final UserModel tokenUser = userService.getUserForUID(jaloUserFromToken.getUid());
				try {
					gigyaAutoLoginStrategy.login(jaloUserFromToken.getUid(), tokenUser.getEncodedPassword(), request,
							response);
					redirectStrategy.sendRedirect(request, response, "/");
				} catch (Exception e) {
					logger.error(e);
					triggerRedirect = true;
				}
			}
		}

		if (triggerRedirect) {
			redirectStrategy.sendRedirect(request, response, "/logout");
		}
	}

	protected CookieBasedLoginToken getCookieBasedLoginToken(Cookie siteCookie) {
		return new CookieBasedLoginToken(siteCookie);
	}

	private boolean handleActiveSession(HttpServletRequest request, HttpServletResponse response,
			BaseSiteModel baseSite, boolean triggerRedirect, GigyaConfigModel gigyaConfig,
			GigyaSessionConfigModel sessionConfig, GigyaSessionType sessionType) {
		if (GigyaSessionType.SLIDING == sessionType) {
			triggerRedirect = handleSlidingSession(request, response, baseSite, sessionConfig, gigyaConfig);
		} else {
			triggerRedirect = handleLogoutWhenGltCookieExpires(request, triggerRedirect, gigyaConfig);
		}
		return triggerRedirect;
	}

	private boolean handleLogoutWhenGltCookieExpires(HttpServletRequest request, boolean triggerRedirect,
			GigyaConfigModel gigyaConfig) {
		final Cookie gltCookie = WebUtils.getCookie(request, ControllerConstants.GLT_COOKIE + gigyaConfig.getGigyaApiKey());
		if (gltCookie == null) {
			// Trigger logout as gigya cookie doesn't exist
			triggerRedirect = true;
		}
		return triggerRedirect;
	}

	private boolean handleSlidingSession(HttpServletRequest request, HttpServletResponse response,
			BaseSiteModel baseSite, GigyaSessionConfigModel sessionConfig, GigyaConfigModel gigyaConfig) {

		final String gltExpCookieName = ControllerConstants.GLT_EXP_COOKIE + gigyaConfig.getGigyaApiKey();
		final Cookie gltExpCookie = WebUtils.getCookie(request, gltExpCookieName);
		if (gltExpCookie == null) {
			return true;
		}

		final String gltCookieName = ControllerConstants.GLT_COOKIE + gigyaConfig.getGigyaApiKey();
		final Cookie gltCookie = WebUtils.getCookie(request, gltCookieName);
		if (gltCookie != null) {
			cookieGenerator.setCookieName(gltExpCookieName);
			cookieGenerator.setCookieMaxAge(sessionConfig.getSessionDuration());
			cookieGenerator.addCookie(response, generateCookieValue(sessionConfig, gigyaConfig, gltCookie));
			CookieUtils.setSameSiteAttributeInCookie(response, gltExpCookieName,
					Config.getString(ControllerConstants.GLT_EXP_COOKIE_SAMESITE_ATTR_VAL,
							ControllerConstants.SAME_SITE_ATTRIBUTE_LAX));
		}
		final Cookie siteCookie = WebUtils.getCookie(request, baseSite.getUid() + ControllerConstants.LOGION_TOKEN);
		if (siteCookie != null) {
			final CookieBasedLoginToken loginToken = getCookieBasedLoginToken(siteCookie);
			try {
				UserManager.getInstance().storeLoginTokenCookie(loginToken.getName(), loginToken.getUser().getUid(),
						loginToken.getLanguage().getIsocode(), loginToken.getPassword(), "/", loginToken.getDomain(),
						true, sessionConfig.getSessionDuration(), response);
				CookieUtils.setSameSiteAttributeInCookie(response, siteCookie.getName(), ControllerConstants.SAME_SITE_ATTRIBUTE_LAX);
			} catch (EJBPasswordEncoderNotFoundException e) {
				logger.error(e);
			}
		}
		return false;
	}

	protected String generateCookieValue(GigyaSessionConfigModel sessionConfig, GigyaConfigModel gigyaConfig,
			Cookie gltCookie) {
		return GigyaCookieValueGenerator.generateCookieValue(gigyaConfig, gltCookie,
				sessionConfig.getSessionDuration());
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

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	@Required
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public AutoLoginStrategy getGigyaAutoLoginStrategy() {
		return gigyaAutoLoginStrategy;
	}

	@Required
	public void setGigyaAutoLoginStrategy(AutoLoginStrategy gigyaAutoLoginStrategy) {
		this.gigyaAutoLoginStrategy = gigyaAutoLoginStrategy;
	}

	public CookieGenerator getCookieGenerator() {
		return cookieGenerator;
	}

	@Required
	public void setCookieGenerator(CookieGenerator cookieGenerator) {
		this.cookieGenerator = cookieGenerator;
	}

}

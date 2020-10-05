/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.CookieGenerator;

import de.hybris.platform.acceleratorstorefrontcommons.security.StorefrontLogoutSuccessHandler;
import de.hybris.platform.gigya.gigyaloginaddon.controllers.ControllerConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Logout success handler to handle logout with gigya session
 */
public class DefaultGigyaLogoutSuccessHandler extends StorefrontLogoutSuccessHandler {

	private ConfigurationService configurationService;

	private CookieGenerator cookieGenerator;

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException {
		// delete cookies created for managing session with gigya if required
		final Cookie gigyaExpCookie = getCookieWithPrefix(request, ControllerConstants.GLT_EXP_COOKIE);
		if (gigyaExpCookie != null) {
			removeCookie(response, gigyaExpCookie);
		}

		final Cookie siteCookie = getCookieWithSuffix(request, ControllerConstants.LOGION_TOKEN);
		if (siteCookie != null) {
			removeCookie(response, siteCookie);
		}

		super.onLogoutSuccess(request, response, authentication);
	}

	private void removeCookie(final HttpServletResponse response, Cookie gigyaExpCookie) {
		cookieGenerator.setCookieName(gigyaExpCookie.getName());
		cookieGenerator.removeCookie(response);
	}

	private Cookie getCookieWithPrefix(HttpServletRequest request, String prefix) {
		return getCookieContainingCode(request, prefix, true);
	}

	private Cookie getCookieWithSuffix(HttpServletRequest request, String suffix) {
		return getCookieContainingCode(request, suffix, false);
	}

	private Cookie getCookieContainingCode(HttpServletRequest request, String code, boolean isPrefix) {
		if (ArrayUtils.isNotEmpty(request.getCookies())) {
			for (final Cookie cookie : request.getCookies()) {
				if ((isPrefix && StringUtils.startsWith(cookie.getName(), code))
						|| (!isPrefix && StringUtils.endsWith(cookie.getName(), code))) {
					return cookie;
				}
			}
		}
		return null;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public CookieGenerator getCookieGenerator() {
		return cookieGenerator;
	}

	@Required
	public void setCookieGenerator(CookieGenerator cookieGenerator) {
		this.cookieGenerator = cookieGenerator;
	}

}

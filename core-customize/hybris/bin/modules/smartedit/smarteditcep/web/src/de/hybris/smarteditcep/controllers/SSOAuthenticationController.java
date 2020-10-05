/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smarteditcep.controllers;

import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.LoginToken;
import de.hybris.platform.util.Config;
import de.hybris.platform.smarteditcep.dto.SSOCredentials;
import de.hybris.smarteditcep.facade.SSOAuthenticationFacade;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;


/**
 * Controller used to generate OAuth2 token basing on SSO authentication
 */
@RestController
@RequestMapping("/authenticate")
public class SSOAuthenticationController
{
	private final String SSO_COOKIE_NAME = "sso.cookie.name";

	@Resource(name = "smarteditSSOAuthenticationFacade")
	private SSOAuthenticationFacade authenticationFacade;

	@PostMapping
	public OAuth2AccessToken generateOAuthTokenBasingOnSSO(final HttpServletRequest request, @RequestBody SSOCredentials credentials) {
		final LoginToken token = new CookieBasedLoginToken(getSamlCookie(request));
		return authenticationFacade.generateOAuthTokenForUser(credentials.getClient_id(), token.getUser().getUid());
	}

	private Cookie getSamlCookie(HttpServletRequest request) {
		final String cookieName = Config.getString(SSO_COOKIE_NAME, null);
		return cookieName != null ? WebUtils.getCookie(request, cookieName) : null;
	}
}

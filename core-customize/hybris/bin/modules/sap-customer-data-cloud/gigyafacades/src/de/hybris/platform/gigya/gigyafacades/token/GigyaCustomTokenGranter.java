/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.token;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyafacades.constants.GigyafacadesConstants;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.site.BaseSiteService;

/**
 * A custom token granter class which gets invoked by the oauth server to
 * replicate customer from CDC to commerce and return a valid token for such
 * customers
 *
 */
public class GigyaCustomTokenGranter extends AbstractTokenGranter {

	private GigyaLoginService gigyaLoginService;

	private GigyaLoginFacade gigyaLoginFacade;

	private BaseSiteService baseSiteService;

	private UserDetailsService userDetailsService;

	protected GigyaCustomTokenGranter(final AuthorizationServerTokenServices tokenServices,
			final ClientDetailsService clientDetailsService, final OAuth2RequestFactory requestFactory,
			final GigyaLoginService gigyaLoginService, final GigyaLoginFacade gigyaLoginFacade,
			final BaseSiteService baseSiteService, final UserDetailsService userDetailsService) {
		super(tokenServices, clientDetailsService, requestFactory, GigyafacadesConstants.GRANT_TYPE);
		this.gigyaLoginService = gigyaLoginService;
		this.gigyaLoginFacade = gigyaLoginFacade;
		this.baseSiteService = baseSiteService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(final ClientDetails client,
			final TokenRequest tokenRequest) {

		final Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		final String uid = parameters.get("UID");
		final String baseSite = parameters.get("baseSite");

		final GigyaJsOnLoginInfo jsInfo = initializeGigyaJsInfoObject(parameters);
		final BaseSiteModel currentBaseSite = configureBaseSiteInSession(baseSite);

		if (currentBaseSite != null && currentBaseSite.getGigyaConfig() != null
				&& gigyaLoginFacade.processGigyaLogin(jsInfo, currentBaseSite.getGigyaConfig())) {
			final UserModel user = gigyaLoginService.findCustomerByGigyaUid(uid);
			final UserDetails loadedUser = userDetailsService.loadUserByUsername(user.getUid());

			final Authentication userAuth = new UsernamePasswordAuthenticationToken(user.getUid(), null,
					loadedUser.getAuthorities());
			final OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);

			return new OAuth2Authentication(storedOAuth2Request, userAuth);
		} else {
			throw new InvalidRequestException("Invalid request received");
		}
	}

	private BaseSiteModel configureBaseSiteInSession(final String baseSite) {
		final BaseSiteModel currentBaseSite = baseSiteService.getBaseSiteForUID(baseSite);
		baseSiteService.setCurrentBaseSite(currentBaseSite, false);
		return currentBaseSite;
	}

	private GigyaJsOnLoginInfo initializeGigyaJsInfoObject(final Map<String, String> parameters) {
		final String uid = parameters.get(GigyafacadesConstants.UID_PARAM);
		final String uidSignature = parameters.get(GigyafacadesConstants.UIDSIGNATURE_PARAM);
		final String signatureTimeStamp = parameters.get(GigyafacadesConstants.TIMESTAMP_PARAM);
		final String idToken = parameters.get(GigyafacadesConstants.IDTOKEN_PARAM);

		final GigyaJsOnLoginInfo jsInfo = new GigyaJsOnLoginInfo();
		jsInfo.setUID(uid);
		jsInfo.setUIDSignature(uidSignature);
		jsInfo.setSignatureTimestamp(signatureTimeStamp);
		jsInfo.setIdToken(idToken);
		return jsInfo;
	}
	
}
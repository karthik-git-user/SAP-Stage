/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.token;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyafacades.constants.GigyafacadesConstants;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class to test the functionality of 'GigyaCustomTokenGranter'
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaCustomTokenGranterTest {

	private static final String SAMPLE_BASE_SITE = "sample-site";
	private static final String SAMPLE_UID = "uid";
	private static final String SAMPLE_UID_SIGNATURE = "UIDSignature";
	private static final String SAMPLE_TIMESTAMP = "timeStamp";

	private static final String CLIENT_ID = "clientId";
	private static final String SCOPE = "scope";

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private GigyaLoginFacade gigyaLoginFacade;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private AuthorizationServerTokenServices tokenServices;

	@Mock
	private ClientDetailsService clientDetailsService;

	@Mock
	private OAuth2RequestFactory requestFactory;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private ClientDetails client;

	private TokenRequest tokenRequest;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private CustomerModel customer;

	private Map<String, String> params = new LinkedHashMap<>();

	@Mock
	private UserDetails userDetails;

	@Mock
	private OAuth2Request oauth2Request;

	@InjectMocks
	private GigyaCustomTokenGranter gigyaCustomTokenGranter = new GigyaCustomTokenGranter(tokenServices,
			clientDetailsService, requestFactory, gigyaLoginService, gigyaLoginFacade, baseSiteService,
			userDetailsService) {

		@Override
		protected OAuth2RequestFactory getRequestFactory() {
			return requestFactory;
		}
		
	};

	@Before
	public void setUp() {
		params.put(GigyafacadesConstants.UID_PARAM, SAMPLE_UID);
		params.put(GigyafacadesConstants.UIDSIGNATURE_PARAM, SAMPLE_UID_SIGNATURE);
		params.put(GigyafacadesConstants.BASE_SITE_PARAM, SAMPLE_BASE_SITE);
		params.put(GigyafacadesConstants.TIMESTAMP_PARAM, SAMPLE_TIMESTAMP);

		tokenRequest = new TokenRequest(params, CLIENT_ID, Collections.singleton(SCOPE),
				GigyafacadesConstants.GRANT_TYPE);
	}

	@Test(expected = InvalidRequestException.class)
	public void testGetOAuth2AuthenticationWhenBaseSiteIsMissing() {
		Mockito.when(baseSiteService.getBaseSiteForUID(SAMPLE_BASE_SITE)).thenReturn(null);

		gigyaCustomTokenGranter.getOAuth2Authentication(client, tokenRequest);
	}

	@Test(expected = InvalidRequestException.class)
	public void testGetOAuth2AuthenticationWhenGigyaConfigIsMissingForSite() {
		Mockito.when(baseSiteService.getBaseSiteForUID(SAMPLE_BASE_SITE)).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);

		gigyaCustomTokenGranter.getOAuth2Authentication(client, tokenRequest);
	}

	@Test(expected = InvalidRequestException.class)
	public void testGetOAuth2AuthenticationWhenCDCUserProcessingIsUnSuccessful() {
		Mockito.when(baseSiteService.getBaseSiteForUID(SAMPLE_BASE_SITE)).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaLoginFacade.processGigyaLogin(Mockito.any(), Mockito.any())).thenReturn(Boolean.FALSE);

		gigyaCustomTokenGranter.getOAuth2Authentication(client, tokenRequest);
	}

	@Test
	public void testGetOAuth2AuthenticationWhenCDCUserProcessingIsSuccessful() {
		Mockito.when(baseSiteService.getBaseSiteForUID(SAMPLE_BASE_SITE)).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaLoginFacade.processGigyaLogin(Mockito.any(), Mockito.any())).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid(Mockito.anyString())).thenReturn(customer);
		Mockito.when(userDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(userDetails);
		Mockito.when(requestFactory.createOAuth2Request(client, tokenRequest)).thenReturn(oauth2Request);

		OAuth2Authentication auth = gigyaCustomTokenGranter.getOAuth2Authentication(client, tokenRequest);

		Assert.assertNotNull(auth);
		Mockito.verify(requestFactory).createOAuth2Request(client, tokenRequest);
	}

}

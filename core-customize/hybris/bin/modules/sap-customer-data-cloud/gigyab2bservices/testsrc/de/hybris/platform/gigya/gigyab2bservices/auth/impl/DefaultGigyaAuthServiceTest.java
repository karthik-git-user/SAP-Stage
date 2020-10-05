/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaActionsAccessData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaActionsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAssetTemplatesData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAssetsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthRequestData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaFunctionalRolesData;
import de.hybris.platform.gigya.gigyab2bservices.token.GigyaTokenGenerator;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


/**
 * Test class for DefaultGigyaAuthService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaAuthServiceTest
{

	private static final String TOKEN = "token";
	private static final String SAMPLE_GROUP = "sample-group";

	@InjectMocks
	private final DefaultGigyaAuthService gigyaAuthService = new DefaultGigyaAuthService()
	{

		@Override
		protected HttpEntity createHttpEntity(final CustomerModel customer, final HttpHeaders headers)
		{
			return httpEntity;
		}

	};

	@Mock
	private HttpEntity httpEntity;

	@Mock
	private B2BCommerceUserService b2bCommerceUserService;

	@Mock
	private Converter<CustomerModel, GigyaAuthRequestData> gigyaAuthRequestConverter;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private GigyaTokenGenerator gigyaTokenGenerator;

	@Mock
	private UserService userService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaAuthRequestData requestData;

	@Mock
	private CustomerModel customer;

	@Mock
	private ResponseEntity response;

	@Mock
	private GigyaAuthData gigyaAuthData;

	@Mock
	private GigyaAssetsData assets;

	@Mock
	private GigyaAssetTemplatesData assetTemplates;

	@Mock
	private GigyaFunctionalRolesData functionalRoles;

	@Mock
	private GigyaActionsData actionsData;

	@Mock
	private GigyaActionsAccessData accessData;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testAssignAuthorisationsToCustomerWhenAuthorizationExists()
	{
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaTokenGenerator.generate(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(TOKEN);
		Mockito.when(gigyaAuthRequestConverter.convert(customer)).thenReturn(requestData);

		Mockito.when(restTemplate.exchange(gigyaConfig.getAuthorizationUrl(), HttpMethod.POST, httpEntity, GigyaAuthData.class))
				.thenReturn(response);
		Mockito.when(response.getBody()).thenReturn(gigyaAuthData);
		Mockito.when(gigyaAuthData.getAssets()).thenReturn(assets);
		Mockito.when(assets.getAssetTemplates()).thenReturn(assetTemplates);
		Mockito.when(assetTemplates.getCommerceFunctionalRoles()).thenReturn(functionalRoles);
		Mockito.when(functionalRoles.getActions()).thenReturn(actionsData);
		Mockito.when(actionsData.getAccessList()).thenReturn(Collections.singletonList(accessData));
		Mockito.when(userService.getUserGroupForUID(SAMPLE_GROUP)).thenReturn(userGroup);

		Mockito.when(accessData.getAttributes())
				.thenReturn(Collections.singletonMap("Name", Collections.singletonList(SAMPLE_GROUP)));

		gigyaAuthService.assignAuthorisationsToCustomer(customer);

		Mockito.verify(customer).setGroups(Mockito.anySet());
	}

	@Test(expected = IOException.class)
	public void testAssignAuthorisationsToCustomerWhenFetchingAuthorizationFails()
	{
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaTokenGenerator.generate(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenReturn(TOKEN);
		Mockito.when(gigyaAuthRequestConverter.convert(customer)).thenReturn(requestData);
		Mockito.when(restTemplate.exchange(gigyaConfig.getAuthorizationUrl(), HttpMethod.POST, httpEntity, GigyaAuthData.class))
				.thenThrow(IOException.class);

		Mockito.when(accessData.getAttributes())
				.thenReturn(Collections.singletonMap("Name", Collections.singletonList(SAMPLE_GROUP)));

		gigyaAuthService.assignAuthorisationsToCustomer(customer);
	}

	@Test
	public void testRemoveAuthorisationsOfCustomerWhenCustomerExists()
	{
		Mockito.when(customer.getGroups()).thenReturn(Collections.singleton(userGroup));
		Mockito.when(userGroup.getUid()).thenReturn("b2badmingroup");

		gigyaAuthService.removeAuthorisationsOfCustomer(customer);

		Mockito.verify(customer).setGroups(Collections.emptySet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAuthorisationsOfCustomerWhenCustomerDoesntExists()
	{
		gigyaAuthService.removeAuthorisationsOfCustomer(null);
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.facade.impl;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsfacades.facade.SapEntitlementFacade;
import com.sap.hybris.sapentitlementsintegration.factory.impl.DefaultSapEntitlementRestTemplateFactory;
import com.sap.hybris.sapentitlementsintegration.pojo.BusinessCategory;
import com.sap.hybris.sapentitlementsintegration.pojo.Data;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.TheRight;
import com.sap.hybris.sapentitlementsintegration.pojo.Uom;
import com.sap.hybris.sapentitlementsintegration.service.impl.DefaultSapEntitlementOutboundService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

@IntegrationTest
public class DefaultSapB2BEntitlementServiceIntegrationTest extends BaseCommerceBaseTest{
	
	@Resource(name = "sapEntitlementFacade")
	private SapEntitlementFacade sapEntitlementFacade;
	@Resource
	private UserService userService;
	
	@InjectMocks
	@Resource
	private DefaultSapEntitlementOutboundService defaultSapEntitlementOutboundService;
	
	@Mock
	DefaultSapEntitlementRestTemplateFactory defaultSapEntitlementRestTemplateFactory; 
	
	@Mock
	private RestTemplate restTemplate;
	
	private UserModel user;
	
	private Entitlements entitlements = new Entitlements();
	private Entitlement entitlement = new Entitlement();
	private Data data = new Data();
	private String dummyText = "dummyText";
	private ArrayList<Entitlement> dummyResponse = new ArrayList<Entitlement>();
	

	private BusinessCategory dummyBusinessCategory;

	private TheRight theRight;

	private Uom uom;
	
	private static final int PAGE_SIZE = 100;
	
	@Before
	public void setUp() throws Exception {
		
		createCoreData();
		
		importCsv("/sapentitlementsfacades/test/TestUserData.impex", "utf-8");
		user = userService.getUserForUID("johnsmith");
		userService.setCurrentUser(user);
		
		data.setStatus(dummyText);
		entitlement.setEntitlementNo(Integer.valueOf(1));
		entitlement.setEntitlementGuid(dummyText);
		entitlement.setValidFrom(new Date());
		entitlement.setValidTo(new Date());
		entitlement.setStatusName(dummyText);
		entitlement.setRefDocNo(dummyText);
		entitlement.setTheRight(theRight);
		entitlement.setGeolocation(dummyText);
		entitlement.setBusinessCategory(dummyBusinessCategory);
		entitlement.setOfferingID(dummyText);
		entitlement.setEntitlementModelName(dummyText);
		entitlement.setQuantity(Integer.valueOf(5));
		entitlement.setUom(uom);
		entitlement.setEntitlementTypeName(dummyText);
		dummyResponse.add(entitlement);
		data.setResponse(dummyResponse);
		entitlements.setData(data);
		
		when(defaultSapEntitlementRestTemplateFactory.create()).thenReturn(restTemplate);
	}
	
	@Test
	public void testGetEntitlementsforCurrentCustomers () {

		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(mockTokenResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());
		doReturn(mockTokenResponse).when(restTemplate).getForEntity(anyString(), anyObject());

		final ResponseEntity<Entitlements> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockPostResponseResult.getBody()).thenReturn(entitlements);
		when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());
		
		doReturn(mockPostResponseResult).when(restTemplate).postForEntity(anyString(), anyObject(), anyObject());
		
		List<EntitlementData> entitlementData = sapEntitlementFacade.getEntitlementsForCurrentCustomer(1, PAGE_SIZE);
		
		Assert.assertEquals(entitlementData.get(0).getId(), dummyText);
		
	}
	
	@Test
	public void testgetEntitlementForNumber () {

		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(mockTokenResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());
		doReturn(mockTokenResponse).when(restTemplate).getForEntity(anyString(), anyObject());

		final ResponseEntity<Entitlements> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockPostResponseResult.getBody()).thenReturn(entitlements);
		when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());
		
		doReturn(mockPostResponseResult).when(restTemplate).postForEntity(anyString(), anyObject(), anyObject());
		
		EntitlementData entitlementData = sapEntitlementFacade.getEntitlementForNumber(dummyText);
		
		Assert.assertEquals(entitlementData.getId(), dummyText);
		
	}

}

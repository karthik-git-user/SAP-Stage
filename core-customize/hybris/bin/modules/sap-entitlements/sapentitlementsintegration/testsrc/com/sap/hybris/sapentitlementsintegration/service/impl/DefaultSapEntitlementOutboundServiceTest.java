/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;
import com.sap.hybris.sapentitlementsintegration.factory.impl.DefaultSapEntitlementRestTemplateFactory;
import com.sap.hybris.sapentitlementsintegration.pojo.BusinessCategory;
import com.sap.hybris.sapentitlementsintegration.pojo.Data;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.GetEntitlementRequest;
import com.sap.hybris.sapentitlementsintegration.pojo.TheRight;
import com.sap.hybris.sapentitlementsintegration.pojo.Uom;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.impl.DefaultDestinationService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;


@UnitTest
public class DefaultSapEntitlementOutboundServiceTest {
	
	@InjectMocks
	private DefaultSapEntitlementOutboundService defaultSapOutboundService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private GetEntitlementRequest request;
	@Mock
	private ConsumedOAuthCredentialModel credential;
	@Mock
	private ConsumedDestinationModel destinationModel;
	@Mock
	private OAuthClientDetailsModel oAuthClientDetailsModel;
	@Mock
	private DefaultDestinationService<ConsumedDestinationModel> destinationService;
	@Mock
	private BusinessCategory dummyBusinessCategory;
	@Mock
	private TheRight theRight;
	@Mock
	private Uom uom;
	@Mock
	private DefaultSapEntitlementRestTemplateFactory defaultSapEntitlementRestTemplateFactory;

	
	private static final String username = "username";
	private static final String tokenUrl = "Token_URL";
	
	
	private Entitlements entitlements = new Entitlements();
	private Entitlement entitlement = new Entitlement();
	private Data data = new Data();
	private String dummyText = "dummyText";
	private ArrayList<Entitlement> dummyResponse = new ArrayList<Entitlement>();
	
	final String ENTITLEMENT_API_DESTINATION = "sapEntitlementsApi";
	
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicData();
    }
	
	@Test
	public void testSendRequest() {
		
		when(destinationService.getDestinationById(any(String.class))).thenReturn(destinationModel);
		when(destinationModel.getCredential()).thenReturn(credential);
		when(credential.getClientId()).thenReturn(dummyText);
		when(oAuthClientDetailsModel.getClientId()).thenReturn(username);
		when(credential.getClientSecret()).thenReturn(dummyText);
		when(credential.getOAuthUrl()).thenReturn(dummyText);
		when(oAuthClientDetailsModel.getOAuthUrl()).thenReturn(tokenUrl);
		when(defaultSapEntitlementRestTemplateFactory.create()).thenReturn(restTemplate);
		
		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(restTemplate.getForEntity(any(String.class),eq(String.class))).thenReturn(mockTokenResponse);
		when(mockTokenResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());
		
		when(restTemplate.getForEntity(tokenUrl, String.class)).thenReturn(mockTokenResponse);
		
		final ResponseEntity<Entitlements> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		when(mockPostResponseResult.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockPostResponseResult.getBody()).thenReturn(entitlements);
		when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());
		
		when(restTemplate.postForEntity(Mockito.eq(destinationModel.getUrl()), any(), eq(Entitlements.class)))
				.thenReturn(mockPostResponseResult);
		
		Entitlements result = defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
		Assert.assertEquals(result, entitlements);
		
	}
	
	@Test(expected = SapEntitlementException.class)
	public void testNullDestinationModelException () {
		when(destinationService.getDestinationById("dummyText")).thenReturn(destinationModel);
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	@Test(expected = SapEntitlementException.class)
	public void testNullCredentialException () {
		credential = null; 
		when(destinationService.getDestinationById("dummyText")).thenReturn(destinationModel);
		when(destinationModel.getCredential()).thenReturn(credential);
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	
	@Test(expected = SapEntitlementException.class)
	public void testTokenResponseException () {
		
		when(destinationService.getDestinationById(ENTITLEMENT_API_DESTINATION)).thenReturn(destinationModel);

		when(restTemplate.getForEntity(tokenUrl, String.class)).thenReturn(null);		
		
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	@Test(expected = SapEntitlementException.class)
	public void testHttpStatusCodeForTokenResponseException () {
		
		when(destinationService.getDestinationById(ENTITLEMENT_API_DESTINATION)).thenReturn(destinationModel);

		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(mockTokenResponse.getStatusCode()).thenReturn(null);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());	
		
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	@Test(expected = SapEntitlementException.class)
	public void testResponseException () {
		
		when(destinationService.getDestinationById(ENTITLEMENT_API_DESTINATION)).thenReturn(destinationModel);

		when(restTemplate.getForEntity(tokenUrl, String.class)).thenReturn(null);	
		
		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(mockTokenResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());
		
		when(restTemplate.postForEntity(Mockito.eq(destinationModel.getUrl()), Mockito.any(), Mockito.eq(Entitlements.class)))
		.thenReturn(null);
		
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	@Test(expected = SapEntitlementException.class)
	public void testHttpStatusCodeForResponseException () {
		final String ENTITLEMENT_API_DESTINATION = "sapEntitlementsApi";
		when(destinationService.getDestinationById(ENTITLEMENT_API_DESTINATION)).thenReturn(destinationModel);

		final ResponseEntity<String> mockTokenResponse = Mockito.mock(ResponseEntity.class);
		when(mockTokenResponse.getStatusCode()).thenReturn(null);
		when(mockTokenResponse.getBody()).thenReturn("Token Body");
		when(mockTokenResponse.getHeaders()).thenReturn(new HttpHeaders());	
		
		when(restTemplate.getForEntity(tokenUrl, String.class)).thenReturn(mockTokenResponse);
		
		final ResponseEntity<Entitlements> mockPostResponseResult = Mockito.mock(ResponseEntity.class);
		when(mockPostResponseResult.getStatusCode()).thenReturn(null);
		when(mockPostResponseResult.getBody()).thenReturn(entitlements);
		when(mockPostResponseResult.getHeaders()).thenReturn(new HttpHeaders());
		
		when(restTemplate.postForEntity(Mockito.eq(destinationModel.getUrl()), Mockito.any(), Mockito.eq(Entitlements.class)))
				.thenReturn(mockPostResponseResult);
		
		defaultSapOutboundService.sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}
	
	void mockBasicData() {
		
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
	}

}

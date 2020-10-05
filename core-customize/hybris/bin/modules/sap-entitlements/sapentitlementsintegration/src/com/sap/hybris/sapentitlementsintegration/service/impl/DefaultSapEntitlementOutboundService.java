/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;
import com.sap.hybris.sapentitlementsintegration.factory.SapEntitlementRestTemplateFactory;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.GetEntitlementRequest;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementOutboundService;


/**
 * Default implementation of SapEntitlementOutboundService
 */
public class DefaultSapEntitlementOutboundService implements SapEntitlementOutboundService
{
	private static final String ENTITLEMENT_API_DESTINATION = "sapEntitlementsApi";
	private DestinationService<ConsumedDestinationModel> destinationService;
	private SapEntitlementRestTemplateFactory sapEntitlementRestTemplateFactory;

	/**
	 * Sends request to SAP Entitlements.
	 * 
	 * @param <T>
	 *           Type of the response object
	 * @param request
	 *           Request object
	 * @param responseClass
	 *           Type to map with API response
	 * @param destinationID
	 *           Destination ID for target destination
	 * @return The response of the request
	 */
	public <T> T sendRequest(final Object request, final Class<T> responseClass, final String destinationID)
	{

		final ConsumedDestinationModel destinationModel = getDestinationService().getDestinationById(destinationID);
		if (destinationModel == null)
		{
			throw new SapEntitlementException("Destination with ID " + destinationID + " not found.");
		}
		//fetch token
		final ConsumedOAuthCredentialModel credential = (ConsumedOAuthCredentialModel) destinationModel.getCredential();
		if (credential == null)
		{
			throw new SapEntitlementException("No credentials found for Destination " + destinationID);
		}
		final String username = credential.getClientId();
		final String password = credential.getClientSecret();
		final String tokenUrl = credential.getOAuthUrl();
		final RestTemplate restTemplate = sapEntitlementRestTemplateFactory.create();

		final BasicAuthorizationInterceptor basicAuthorizationInterceptor = new BasicAuthorizationInterceptor(username, password);
		final List<ClientHttpRequestInterceptor> interceptorList = Lists.newArrayList(basicAuthorizationInterceptor);
		restTemplate.setInterceptors(interceptorList);
		final ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenUrl, String.class);

		if (!HttpStatus.OK.equals(tokenResponse.getStatusCode()))
		{
			throw new SapEntitlementException(
					"Could not fetch token from SAP Entitlements: " + tokenResponse.getStatusCode() + " " + tokenResponse.getBody());
		}

		final String tokenHeader = "Bearer " + tokenResponse.getBody();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", tokenHeader);
		httpHeaders.add("Content-Type", "application/json");
		final HttpEntity<Object> entity = new HttpEntity<Object>(request, httpHeaders);
		final ResponseEntity<T> response = restTemplate.postForEntity(destinationModel.getUrl(), entity, responseClass);
		if (!HttpStatus.OK.equals(response.getStatusCode()))
		{
			throw new SapEntitlementException(
					"Error connecting to SAP Entitlements:  " + response.getStatusCode() + " " + tokenResponse.getBody());
		}
		return response.getBody();
	}

	/**
	 * @deprecated not used anymore.
	 */
	@Deprecated(since = "1905.2003-CEP", forRemoval = true)
	public Entitlements sendRequest(final GetEntitlementRequest request)
	{
		final ConsumedDestinationModel destinationModel = getDestinationService().getDestinationById(ENTITLEMENT_API_DESTINATION);
		if (destinationModel == null)
		{
			throw new SapEntitlementException("Destination with ID " + ENTITLEMENT_API_DESTINATION + " not found.");
		}

		//fetch token
		final ConsumedOAuthCredentialModel credential = (ConsumedOAuthCredentialModel) destinationModel.getCredential();
		if (credential == null)
		{
			throw new SapEntitlementException("No credentials found for Destination " + ENTITLEMENT_API_DESTINATION);
		}
		final String username = credential.getClientId();
		final String password = credential.getClientSecret();
		final String tokenUrl = credential.getOAuthUrl();
		final RestTemplate restTemplate = sapEntitlementRestTemplateFactory.create();

		final BasicAuthorizationInterceptor basicAuthorizationInterceptor = new BasicAuthorizationInterceptor(username, password);
		final List<ClientHttpRequestInterceptor> interceptorList = Lists.newArrayList(basicAuthorizationInterceptor);
		restTemplate.setInterceptors(interceptorList);
		final ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenUrl, String.class);

		if (!HttpStatus.OK.equals(tokenResponse.getStatusCode()))
		{
			throw new SapEntitlementException(
					"Could not fetch token from SAP Entitlements: " + tokenResponse.getStatusCode() + " " + tokenResponse.getBody());
		}

		final String tokenHeader = "Bearer " + tokenResponse.getBody();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", tokenHeader);
		httpHeaders.add("Content-Type", "application/json");
		final HttpEntity<GetEntitlementRequest> entity = new HttpEntity<GetEntitlementRequest>(request, httpHeaders);
		final ResponseEntity<Entitlements> response = restTemplate.postForEntity(destinationModel.getUrl(), entity,
				Entitlements.class);
		if (!HttpStatus.OK.equals(response.getStatusCode()))
		{
			throw new SapEntitlementException(
					"Error connecting to SAP Entitlements:  " + response.getStatusCode() + " " + tokenResponse.getBody());
		}
		return response.getBody();

	}

	/**
	 * @return the destinationService
	 */
	public DestinationService<ConsumedDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	/**
	 * @param destinationService
	 *           the destinationService to set
	 */
	@Required
	public void setDestinationService(final DestinationService<ConsumedDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}


	/**
	 * @param sapEntitlementRestTemplateFactory
	 *           the sapEntitlementRestTemplateFactory to set
	 */
	@Required
	public void setSapEntitlementRestTemplateFactory(final SapEntitlementRestTemplateFactory sapEntitlementRestTemplateFactory)
	{
		this.sapEntitlementRestTemplateFactory = sapEntitlementRestTemplateFactory;
	}



}

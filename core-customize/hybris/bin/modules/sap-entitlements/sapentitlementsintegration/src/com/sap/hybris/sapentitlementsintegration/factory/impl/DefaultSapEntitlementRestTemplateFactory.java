/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.factory.impl;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.sap.hybris.sapentitlementsintegration.factory.SapEntitlementRestTemplateFactory;


/**
 * Default implementation of SapEntitlementRestTemplateFactory
 */
public class DefaultSapEntitlementRestTemplateFactory implements SapEntitlementRestTemplateFactory
{
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@Override
	public RestTemplate create()
	{
		return new RestTemplate(getClientHttpRequestFactory());
	}

	/**
	 * @return the clientHttpRequestFactory
	 */
	public ClientHttpRequestFactory getClientHttpRequestFactory()
	{
		return clientHttpRequestFactory;
	}

	/**
	 * @param clientHttpRequestFactory
	 *           the clientHttpRequestFactory to set
	 */
	public void setClientHttpRequestFactory(final ClientHttpRequestFactory clientHttpRequestFactory)
	{
		this.clientHttpRequestFactory = clientHttpRequestFactory;
	}

}

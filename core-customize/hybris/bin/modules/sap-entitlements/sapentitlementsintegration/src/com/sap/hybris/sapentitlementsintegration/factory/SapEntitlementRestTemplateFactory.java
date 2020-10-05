/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.factory;

import org.springframework.web.client.RestTemplate;


/**
 * Factory to create RestTemplate for SAP Entitlements
 */

public interface SapEntitlementRestTemplateFactory
{
	/**
	 * Creates RestTemplate
	 *
	 * @return RestTemplate for SAP Entitlements
	 */
	public RestTemplate create();
}

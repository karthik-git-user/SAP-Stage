/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;


import de.hybris.platform.sap.saprevenuecloudorder.clients.SapRevenueCloudSubscriptionClient;

/**
 * Configuration Service for Subscription Client. 
 * @deprecated charon is no longer supported, instead use {@link org.springframework.web.client.RestOperations}
 */
@Deprecated(since = "1905.12", forRemoval = true)
public interface SapRevenueCloudSubscriptionConfigurationService {

	/**
     * Get SAP subscription REST client
     * @return REST client SapSubscriptionClient
     */
    SapRevenueCloudSubscriptionClient getSapSubscriptionClient();

}

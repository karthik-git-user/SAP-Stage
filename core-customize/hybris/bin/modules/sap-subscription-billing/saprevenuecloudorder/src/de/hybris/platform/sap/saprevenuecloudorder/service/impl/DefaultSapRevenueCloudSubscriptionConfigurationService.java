/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;


import org.apache.log4j.Logger;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SapRevenueCloudSubscriptionClient;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionConfigurationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Configuration Service for SubscriptionClient Object.
 * @deprecated charon is no longer supported, instead use {@link org.springframework.web.client.RestOperations}
 */
@Deprecated(since = "1905.12", forRemoval = true)
public class DefaultSapRevenueCloudSubscriptionConfigurationService implements SapRevenueCloudSubscriptionConfigurationService
{
	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionConfigurationService.class);
    private ApiRegistryClientService apiRegistryClientService;

	@Override
	public SapRevenueCloudSubscriptionClient getSapSubscriptionClient() {

		try
		{
			return getApiRegistryClientService().lookupClient(SapRevenueCloudSubscriptionClient.class);
		}
		catch (final CredentialException e)
		{
			LOG.error("Error occured while fetching SapRevenueCloudSubscriptionClient Configuration");
			throw new SystemException(e);
		}
    }

    public ApiRegistryClientService getApiRegistryClientService() {
		return apiRegistryClientService;
	}

	public void setApiRegistryClientService(ApiRegistryClientService apiRegistryClientService) {
		this.apiRegistryClientService = apiRegistryClientService;
	}

}

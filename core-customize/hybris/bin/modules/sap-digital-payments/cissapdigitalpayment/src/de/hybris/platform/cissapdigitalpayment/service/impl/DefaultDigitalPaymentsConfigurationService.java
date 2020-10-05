/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service.impl;

import de.hybris.platform.cissapdigitalpayment.service.DigitalPaymentsConfigurationService;
import org.apache.log4j.Logger;

import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;

/**
 * Default implementation of {@link DigitalPaymentsConfigurationService}
 */
public class DefaultDigitalPaymentsConfigurationService implements DigitalPaymentsConfigurationService {

    private static final Logger LOG = Logger.getLogger(DefaultDigitalPaymentsConfigurationService.class);
    private ApiRegistryClientService apiRegistryClientService;

    @Override
    public SapDigitalPaymentClient getSapDigitalPaymentsClient() {
        try
        {
            return getApiRegistryClientService().lookupClient(SapDigitalPaymentClient.class);
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

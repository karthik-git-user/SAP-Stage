/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;

/**
 * Digital Payment Configuration Service which helps in generating client for digital payments
 */
public interface DigitalPaymentsConfigurationService {

    /**
     * Get Digital Payments Client
     * @return Rest Client for digital payments
     */
    SapDigitalPaymentClient getSapDigitalPaymentsClient();
}

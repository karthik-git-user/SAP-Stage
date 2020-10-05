/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.service;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

/**
 * Sap Revenue Cloud Digital Payment Service Layer Component
 */
public interface SapRevenueCloudDigitalPaymentService {

    /**
     * Checks payment card registration if successful then saves model in commerce
     * @param sessionId Session ID of digital payment registration request
     * @return {@link CreditCardPaymentInfoModel} if successful else null
     */
    CreditCardPaymentInfoModel checkPaymentCardRegistration(String sessionId);

}

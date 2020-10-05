/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.facade;

import com.sap.hybris.saprevenueclouddpaddon.data.SapRcDigitalPaymentPollResultData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;

/**
 * Sap Revenue Cloud Digital Payment Facade
 */
public interface SapRevenueCloudDigitalPaymentFacade {
    /**
     * Checks payment card registration
     * @param sessionId Session ID of digital payment registration request
     * @return {@link SapRcDigitalPaymentPollResultData}
     */
    CCPaymentInfoData checkPaymentCardRegistration(String sessionId);

    /**
     * Fetch the registration URL from the SAP Digital payment. Application is redirected to this URL to register the
     * card with SAP Digital payment
     *
     * @return String
     */
    String getCardRegistrationUrl();

    /**
     * @return Session Id of digital payment register card session
     */
    String getSapDigitalPaymentRegisterCardSessionId();
}

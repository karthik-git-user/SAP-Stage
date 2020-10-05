/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigitalPaymentsRegistrationModel {

    @JsonProperty("PaymentCardRegistrationURL")
    private String paymentCardRegistrationURL;

    @JsonProperty("PaymentCardRegistrationSession")
    private String paymentCardRegistrationSession;

    public String getPaymentCardRegistrationURL() {
        return paymentCardRegistrationURL;
    }

    public void setPaymentCardRegistrationURL(String paymentCardRegistrationURL) {
        this.paymentCardRegistrationURL = paymentCardRegistrationURL;
    }

    public String getPaymentCardRegistrationSession() {
        return paymentCardRegistrationSession;
    }

    public void setPaymentCardRegistrationSession(String paymentCardRegistrationSession) {
        this.paymentCardRegistrationSession = paymentCardRegistrationSession;
    }
}

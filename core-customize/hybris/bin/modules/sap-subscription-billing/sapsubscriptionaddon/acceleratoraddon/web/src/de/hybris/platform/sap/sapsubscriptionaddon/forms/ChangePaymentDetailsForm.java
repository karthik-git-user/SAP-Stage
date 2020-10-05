/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

public class ChangePaymentDetailsForm {
    private String version;
    private String paymentCardId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPaymentCardId() {
        return paymentCardId;
    }

    public void setPaymentCardId(String paymentCardId) {
        this.paymentCardId = paymentCardId;
    }
}

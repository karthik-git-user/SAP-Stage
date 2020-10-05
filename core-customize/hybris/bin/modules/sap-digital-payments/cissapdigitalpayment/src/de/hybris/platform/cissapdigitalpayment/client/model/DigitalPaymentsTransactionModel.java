/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DigitalPaymentsTransactionModel {
    @JsonProperty("DigitalPaymentTransaction")
    private String digitalPaymentTransaction;

    @JsonProperty("DigitalPaymentDateTime")
    private Date digitalPaymentDateTime;

    @JsonProperty("DigitalPaytTransResult")
    private String digitalPaytTransResult;

    @JsonProperty("DigitalPaytTransRsltDesc")
    private String digitalPaytTransRsltDesc;

    public String getDigitalPaymentTransaction() {
        return digitalPaymentTransaction;
    }

    public void setDigitalPaymentTransaction(String digitalPaymentTransaction) {
        this.digitalPaymentTransaction = digitalPaymentTransaction;
    }

    public Date getDigitalPaymentDateTime() {
        return digitalPaymentDateTime;
    }

    public void setDigitalPaymentDateTime(Date digitalPaymentDateTime) {
        this.digitalPaymentDateTime = digitalPaymentDateTime;
    }

    public String getDigitalPaytTransResult() {
        return digitalPaytTransResult;
    }

    public void setDigitalPaytTransResult(String digitalPaytTransResult) {
        this.digitalPaytTransResult = digitalPaytTransResult;
    }

    public String getDigitalPaytTransRsltDesc() {
        return digitalPaytTransRsltDesc;
    }

    public void setDigitalPaytTransRsltDesc(String digitalPaytTransRsltDesc) {
        this.digitalPaytTransRsltDesc = digitalPaytTransRsltDesc;
    }
}

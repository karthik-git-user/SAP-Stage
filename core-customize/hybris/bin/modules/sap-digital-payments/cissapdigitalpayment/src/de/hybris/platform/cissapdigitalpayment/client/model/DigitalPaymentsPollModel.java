/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DigitalPaymentsPollModel {

    @JsonProperty("DigitalPaymentTransaction")
    private DigitalPaymentsTransactionModel digitalPaymentTransaction;

    @JsonProperty("PaytCardByDigitalPaymentSrvc")
    private String paytCardByDigitalPaymentSrvc;

    @JsonProperty("PaymentCardType")
    private String paymentCardType;

    @JsonProperty("PaymentCardExpirationMonth")
    private String paymentCardExpirationMonth;

    @JsonProperty("PaymentCardExpirationYear")
    private String paymentCardExpirationYear;

    @JsonProperty("PaymentCardMaskedNumber")
    private String paymentCardMaskedNumber;

    @JsonProperty("PaymentCardHolderName")
    private String paymentCardHolderName;

    public DigitalPaymentsTransactionModel getDigitalPaymentTransaction() {
        return digitalPaymentTransaction;
    }

    public void setDigitalPaymentTransaction(DigitalPaymentsTransactionModel digitalPaymentTransaction) {
        this.digitalPaymentTransaction = digitalPaymentTransaction;
    }

    public String getPaytCardByDigitalPaymentSrvc() {
        return paytCardByDigitalPaymentSrvc;
    }

    public void setPaytCardByDigitalPaymentSrvc(String paytCardByDigitalPaymentSrvc) {
        this.paytCardByDigitalPaymentSrvc = paytCardByDigitalPaymentSrvc;
    }

    public String getPaymentCardType() {
        return paymentCardType;
    }

    public void setPaymentCardType(String paymentCardType) {
        this.paymentCardType = paymentCardType;
    }

    public String getPaymentCardExpirationMonth() {
        return paymentCardExpirationMonth;
    }

    public void setPaymentCardExpirationMonth(String paymentCardExpirationMonth) {
        this.paymentCardExpirationMonth = paymentCardExpirationMonth;
    }

    public String getPaymentCardExpirationYear() {
        return paymentCardExpirationYear;
    }

    public void setPaymentCardExpirationYear(String paymentCardExpirationYear) {
        this.paymentCardExpirationYear = paymentCardExpirationYear;
    }

    public String getPaymentCardMaskedNumber() {
        return paymentCardMaskedNumber;
    }

    public void setPaymentCardMaskedNumber(String paymentCardMaskedNumber) {
        this.paymentCardMaskedNumber = paymentCardMaskedNumber;
    }

    public String getPaymentCardHolderName() {
        return paymentCardHolderName;
    }

    public void setPaymentCardHolderName(String paymentCardHolderName) {
        this.paymentCardHolderName = paymentCardHolderName;
    }
}

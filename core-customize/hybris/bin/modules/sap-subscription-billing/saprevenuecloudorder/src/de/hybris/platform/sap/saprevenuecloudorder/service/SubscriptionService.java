/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;

import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;

import java.util.List;

/**
 * This service connects to Sap Subscription Billing System and performs Subscription related actions
 */
public interface SubscriptionService {

    /**
     * fetch subscription details using customerId
     *
     * @param clientId customerId
     * @return {@link List} list of {@link Subscription}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    List<Subscription> getSubscriptionsByClientId(String clientId) throws SubscriptionServiceException;

    /**
     * fetch subscription details with Pagination
     *
     * @param clientId   customerId
     * @param pageNumber pageNumber
     * @param pageSize   pageSize
     * @param sortBy    Sorting Order
     * @return single page of {@link Subscription}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    PaginationResult<List<Subscription>> getSubscriptionsByClientIdPage(final String clientId, final Integer pageNumber, final Integer pageSize, final String sortBy) throws SubscriptionServiceException;

    /**
     * fetch subscription details using subscriptionId
     *
     * @param subscriptionsId subscription code
     * @return {@link Subscription }
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    Subscription getSubscription(String subscriptionsId) throws SubscriptionServiceException;

    /**
     * cancel a subscription based on subscription code
     *
     * @param code         subscription code
     * @param subscription subscription data required for cancellation
     *
     * @return Response of cancelled subscription
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    CancellationResponse cancelSubscription(String code, CancellationRequest subscription) throws SubscriptionServiceException;

    /**
     * withdraw  a subscription based on subscription code
     *
     * @param code         subscription code
     * @param subscription subscription data required for Withdrawal
     *
     * @return Response of withdrawn subscription
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    WithdrawalResponse withdrawSubscription(String code, WithdrawalRequest subscription) throws SubscriptionServiceException;

    /**
     * extend a subscription based on subscription code
     *
     * @param subscriptionCode subscriptionId
     * @param subscription     subscription data required for extending subscription
     * @param simulate         simulation flag (Data will simulate but won't persist)
     * @return extension status
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    ExtensionResponse extendSubscription(String subscriptionCode, ExtensionRequest subscription, boolean simulate) throws SubscriptionServiceException;

    /**
     * get billingFrequency for specific product
     *
     * @param productModel - product model
     *
     * @return {@link BillingFrequencyModel}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    BillingFrequencyModel getBillingFrequency(final ProductModel productModel) throws SubscriptionServiceException;

    /**
     * get effective end date for subscription
     *
     * @param subscriptionsId     subscription code
     * @param reqCancellationDate requested cancellation date
     *
     * @return effective end date for subscription
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    EffectiveExpirationDate computeCancellationDate(String subscriptionsId, String reqCancellationDate) throws SubscriptionServiceException;

    /**
     * Makes service call to change payment details of provided subscription code
     *
     * @param code              Subscription Id for which payment details needs to be changed
     * @param changePaymentData Payment details that is to be updated
     *
     * @return result of Payment update
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    PaymentResponse updatePayment(final String code, final PaymentRequest changePaymentData) throws SubscriptionServiceException;

    /**
     * Reverses the cancellation of subscription
     *
     * @param code                 subscription id
     * @param cancellationReversal Cancellation Reversal
     *
     * @return Cancellation Reversal Response
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    CancellationReversalResponse reverseCancellation(String code, CancellationReversalRequest cancellationReversal) throws SubscriptionServiceException;

}

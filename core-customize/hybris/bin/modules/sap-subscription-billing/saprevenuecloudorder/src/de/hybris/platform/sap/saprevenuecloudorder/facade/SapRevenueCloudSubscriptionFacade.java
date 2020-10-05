/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade;

import java.util.List;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionFormData;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;


/**
 * Facade which provides functionality to manage subscriptions.
 */
public interface SapRevenueCloudSubscriptionFacade extends SubscriptionFacade

{
    /**
     * Returns all subscriptions for the current user.
     *
     * @param currentPage current page
     * @param pageSize size of page
     * @param sort sorting order
     * @return the current user's subscription
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    SearchPageData<SubscriptionData> getSubscriptions(final int currentPage, final int pageSize, final String sort)  throws SubscriptionFacadeException;

    /**
     * cancel the subscription
     *
     * @param subscriptionData Subscription Data
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void cancelSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException;

    /**
     * withdraw the subscription
     *
     * @param subscriptionData Subscription Data
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void withdrawSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException;

    /**
     * extends the subscription
     *
     * @param subscriptionData
     * 				Subscription Data
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     * @deprecated extendSubscription(String , SubscriptionExtensionFormData , boolean ) with simulate=false
     */
    @Deprecated(since = "1905.09", forRemoval = true)
    void extendSubscription(SubscriptionData subscriptionData) throws SubscriptionFacadeException;

    /**
     * Extends the subscription
     * @param subscriptionCode subscription code
     * @param formData subscription extension form
     * @param simulation simulation flag
     * @return subscription extension response
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    SubscriptionExtensionData extendSubscription(String subscriptionCode,
                                                 SubscriptionExtensionFormData formData,
                                                 boolean simulation) throws SubscriptionFacadeException;

    /**
     * gets the effective end date upon cancellation
     *
     * @param subscriptionId subscriptionId
     *
     * @return subscriptionData
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    SubscriptionData computeCancellationDate(String subscriptionId) throws SubscriptionFacadeException;

    /**
     * gets the effective end date upon extension
     *
     * @param subscriptionData subscription data
     *
     * @return subscriptionData
     *
     * @deprecated extendSubscription(String , SubscriptionExtensionFormData , boolean ) with simulate=true
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    @Deprecated(since = "1905.09", forRemoval = true)
    SubscriptionData computeExtensionDate(SubscriptionData subscriptionData ) throws SubscriptionFacadeException;

    /**
     * gets subscription bills based on customerId and dates
     *
     * @param fromDate
     * 			starting Date for filtering bills
     * @param toDate
     * 			end Date for filtering bills
     *
     * @return  {@link List}<{@link SubscriptionBillingData}> of the current user
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    List<SubscriptionBillingData> getSubscriptionBills(String fromDate,String toDate) throws SubscriptionFacadeException;

    /**
     * gets subscription bills based on customerId , dates , currentPage , pageSize and sort
     * @param fromDate
     * 			starting Date for filtering bills
     * @param toDate
     * 			end Date for filtering bills
     * @param currentPage
     *          currentPage
     * @param pageSize
     *          pageSize
     * @param sort
     *          sort
     * @return bills of the current user's subscription
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    SearchPageData<SubscriptionBillingData> getSubscriptionBillsHistory(
            String fromDate,
            String toDate,
            final int currentPage,
            final int pageSize,
            String sort) throws SubscriptionFacadeException;

    /**
     * gets subscription bill using billId
     *
     * @param billId
     * 			billId
     * @return
     * 		bill for the billId
     * @deprecated instead use {@link #getBillDetails(String)}
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    @Deprecated(forRemoval = true, since = "1905.12")
    List<SubscriptionBillingData> getSubscriptionBillsById(String billId) throws SubscriptionFacadeException;

    /**
     * get subscription bill using billId
     *
     * @param billId
     * 			billId
     * @return
     * 		bill for the billId
     *
     *  @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    List<SubscriptionBillingData> getBillDetails(String billId) throws SubscriptionFacadeException;

    /**
     * gets subscription bill using billId
     *
     * @param billId
     * 			billId
     * @return
     * 		bill for the billId
     *
     * @deprecated instead use {@link #getBillDetails(String)}
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    @Deprecated(since = "1905.12", forRemoval = true)
    List<SubscriptionBillingData> getSubscriptionBillsByBillId(String billId) throws SubscriptionFacadeException;

    /**
     * Changes payment details of subscription in Revenue Cloud using token received from digital payment (in case of
     * payment card) and external system (in case of external card)
     * @param subscriptionData subscription data
     * @param paymentCardToken payment card token
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void changePaymentDetailsAsCard(SubscriptionData subscriptionData, String paymentCardToken) throws SubscriptionFacadeException;

    /**
     * Changes payment details of subscription to invoice in revenue cloud
     * @param subscriptionData subscription data
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void changePaymentDetailsAsInvoice(SubscriptionData subscriptionData) throws SubscriptionFacadeException;

    /**
     * Fetches current payment card details of subscription
     * @deprecated use payment details facade method instead
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    @Deprecated(since = "1905.09", forRemoval = true)
    List<CCPaymentInfoData> getCCPaymentDetails(boolean saved) throws SubscriptionFacadeException;

    /**
     * Formats digital payments specific values for UI
     * @param ccPaymentInfoData payment card details
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void populateCardTypeName(CCPaymentInfoData ccPaymentInfoData) throws SubscriptionFacadeException;

    /**
     * Formats digital payments specific values for UI
     * @param ccPaymentInfoDataList list of payment card details
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void populateCardTypeName(List<CCPaymentInfoData> ccPaymentInfoDataList) throws SubscriptionFacadeException;

    /**
     * Reverses the Cancellation of subscription
     * @param subscriptionData subscription details
     *
     * @throws SubscriptionFacadeException if customer enters wrong inputs
     */
    void reverseCancellation(final SubscriptionData subscriptionData) throws SubscriptionFacadeException;

    /**
     * Updates a Subscription
     * @param subscriptionData subscription data
     * @throws SubscriptionFacadeException if customer passes wrong inputs
     */
    void updateSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException;
}



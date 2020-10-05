/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import java.util.List;

import com.hybris.charon.RawResponse;


/**
 * Service API that provides methods for SAP RevenueCloud Subscription Orders
 * @deprecated instead use {@link BillService} and {@link SubscriptionService}
 */
@Deprecated(since = "1905.10", forRemoval = true)
public interface SapRevenueCloudSubscriptionService {

	/**
	 * fetch subscription details using customerId
	 *
	 * @param clientId
	 *           customerId
	 *
	 * @return {@link List} list of subscriptions
	 *
	 */
	List<Subscription> getSubscriptionsByClientId(String clientId);
	
	/**
	 * fetch subscription details with Pagination
	 *
	 * @param clientId
	 *           customerId
	 * @param pageNumber
	 * 			 pageNumber
	 * @param pageSize
	 * 		     pageSize          
	 *
	 * @return  Response
	 *
	 */
	RawResponse<List<Subscription>> getSubscriptionsWithPagination(String clientId,final int pageNumber,final int pageSize);

	/**
	 * fetch subscription details using subscriptionId
	 *
	 * @param subscriptionsId
	 *           subscription code
	 *
	 * @return {@link Subscription } Subscription details
	 */
	Subscription getSubscriptionById(String subscriptionsId);

	/**
	 * cancel a subscription based on subscription code
	 * 
	 * @param code
	 * 			subscription code
	 * @param subscription
	 * 			subscription data required for cancellation
	 */
	void cancelSubscription(String code, CancelSubscription subscription);

	/**
	 *withdraw  a subscription based on subscription code
	 *
	 * @param code
	 * 			subscription code
	 * @param subscription
	 * 			subscription data required for Withdrawal
	 */
	void withdrawSubscription(String code, WithdrawSubscription subscription);

	/**
	 * extend a subscription based on subscription code
	 *
	 *@param subscriptionCode
	 *				subscriptionId
	 *@param subscription
	 *			subscription data required for extending subscription
	 *
	 * @deprecated Instead use extendSubscription( String, SubscriptionExtensionForm, boolean )
	 */
	@Deprecated(since = "1905.09", forRemoval = true)
	void extendSubscription(String subscriptionCode, ExtendSubscription subscription);

	/**
	 * extend a subscription based on subscription code
	 *
	 *@param subscriptionCode
	 *				subscriptionId
	 *@param subscription
	 *			subscription data required for extending subscription
	 *@param simulate
	 * 	 		simulation flag
	 * @return extension status
	 *
	 * @deprecated Instead use extendSubscription( String, SubscriptionExtensionForm, boolean )
	 */
	@Deprecated(since = "1905.09", forRemoval = true)
	Subscription extendSubscription(String subscriptionCode, ExtendSubscription subscription, boolean simulate);

	/**
	 * extend a subscription based on subscription code
	 *
	 *@param subscriptionCode
	 *				subscriptionId
	 *@param subscription
	 *			subscription data required for extending subscription
	 *@param simulate
	 * 	 		simulation flag
	 * @return extension status
	 */
	SubscriptionExtensionResponse extendSubscription(String subscriptionCode, SubscriptionExtensionForm subscription, boolean simulate);

	/**
	 * get billingFrequency for specific product
	 *
	 * @param productModel
	 *           - product model 
	 *
	 * @return {@link BillingFrequencyModel}
	 */
	BillingFrequencyModel getBillingFrequency(final ProductModel productModel);
	
	/**
	 * get effective end date for subscription
	 *
	 * @param subscriptionsId
	 *           - subscription code
	 * @param reqCancellationDate
	 * 			 - requested cancellation date
	 *
	 * @return effective end date for subscription
	 */
	String computeCancellationDate(String subscriptionsId, String reqCancellationDate);

	/**
	 * fetch bills using subscriptionId and dates
	 *
	 * @param subscriptionsId
	 * 				subscriptionId
	 * @param fromDate
	 * 				the date from which bills should be displayed
	 * @param toDate
	 * 				the date till which bills should be displayed
	 * @return {@link List<Bills>}
	 * 			list of bills based on the filter applied
	 * @deprecated instead use {@link #getBillsBySubscriptionsId(String, String, String, int, int, String)}
	 */
	@Deprecated(since = "1905.10", forRemoval = true)

	List<Bills> getBillsBySubscriptionsId(final String subscriptionsId,final String fromDate,final String toDate);
	/**
	 * fetch bill using billId
	 * 
	 * @param billId 
	 * 			subscription billId
	 * @return
	 * 		{@link Bills} bill for the given id
	 */
	Bills getSubscriptionBillsById(String billId);

    /**
     * fetch bills using subscriptionId and dates
     *
     * @param subscriptionsId
     * 				subscriptionId
     * @param fromDate
     * 				the date from which bills should be displayed
     * @param toDate
     * 				the date till which bills should be displayed
     * @param pageNumber
     *              pageNumber
     * @param pageSize
     *              pageSize
     * @param sort
     *              sort
     *
     * @return Response
     *
     */
    RawResponse<List<BillsList>> getBillsBySubscriptionsId(final String subscriptionsId,
                                                           final String fromDate,
                                                           final String toDate,
                                                           final int pageNumber,
                                                           final int pageSize,
                                                           final String sort);

	/**
	 * fetch bill using billId
	 *
	 * @param billId
	 * 			subscription billId
	 * @return
	 * 		{@link Bills} bill for the given id
	 */
	BillsList getSubscriptionBillsByBillId(String billId);

	/**
	 * fetch usage of subscription in current billing cycle
	 * 
	 * @param subscriptionId
	 * 				- id of subscription for which current usage is needed
	 * @param currentDate
	 * 				- current date to fetch usage till current date in present billing cycle
	 * @return {@link List<Bills>}
	 * 				returns bills which contains current usage for given subscription
	 */
	List<Bills> getSubscriptionCurrentUsage(String subscriptionId, String currentDate);

    /**
     * Makes service call to change payment details of provided subscription code
     * @param code - Subscription Id for which payment details needs to be changed
     * @param changePaymentData Payment details that is to be updated
     */
    void changePaymentDetails(final String code, final ChangePaymentData changePaymentData);

    /**
     * Reverses the cancellation of subscription
     *
     * @param code subscription id
     * @param cancellationReversal Cancellation Reversal
	 * @return Cancellation Reversal Response
     */
	CancellationReversalResponse reverseCancellation(String code, CancellationReversal cancellationReversal);
}

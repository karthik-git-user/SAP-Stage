/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.clients;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import com.hybris.charon.RawResponse;

/**
 * @deprecated charon is no longer supported, migrated to API registry
 */

@Deprecated(since = "1905.12", forRemoval = true)
@OAuth
@Http
@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:40000}")
public interface SapRevenueCloudSubscriptionClient
{

	@GET
	@Path("/subscription/v1/subscriptions?customer.id={clientId}&sort=documentNumber,desc")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	List<Subscription> getSubscriptionsByClientId(@PathParam("clientId") String clientId);
	
	@GET
	@Path("/subscription/v1/subscriptions")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RawResponse<List<Subscription>> getSubscriptionsWithPagination(
			@QueryParam("customer.id") String clientId,
			@QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("sort") String sort);

	@GET
	@Path("/subscription/v1/subscriptions/{subscriptionsId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Subscription getSubscriptionById(@PathParam("subscriptionsId") String subscriptionsId);

	@POST
	@Path("/subscription/v1/subscriptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Subscription createSubscription(Subscription subscription);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/cancellation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void cancelSubscription(@PathParam("id") String id, CancelSubscription subscription);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/withdrawal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void withdrawSubscription(@PathParam("id") String id, WithdrawSubscription subscription);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/extension")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void extendSubscription(@PathParam("id") String id, ExtendSubscription subscription);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/payment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void changePaymentDetails(@PathParam("id") String id, ChangePaymentData changePaymentData);

	@GET
	@Path("/subscription/v1/subscriptions/{id}/computedcancellationdate?requestedCancellationDate={reqCancellationDate}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Subscription getCancellationDate(@PathParam("id") String subscriptionsId,@PathParam("reqCancellationDate") String reqCancellationDate);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/extension")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Subscription extendSubscription(@PathParam("id") String id, ExtendSubscription subscription, @QueryParam("simulation") boolean simulation);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/extension")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	SubscriptionExtensionResponse extendSubscription(@PathParam("id") String id, SubscriptionExtensionForm subscription, @QueryParam("simulation") boolean simulation);

	/**
	 *
	 * @param customerId subscription billing customer id
	 * @param fromDate from date of bills
	 * @param toDate to date of bill
	 * @return list of {@link Bills}
	 * @deprecated instead use {@link #getSubscriptionBills(String, String, String, Integer, Integer)}
	 */
	@GET
	@Path("/bill/v1/bills?customerId={customerId}&from={fromDate}&to={toDate}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON )
	@Deprecated(since = "1905.10" ,forRemoval = true)
	List<Bills> getSubscriptionBills(@PathParam("customerId") String customerId,@PathParam("fromDate") String fromDate,@PathParam("toDate") String toDate);
	
	@GET
	@Path("/bill/v1/bills?subscriptionId={subscriptionId}&from={fromDate}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON )
	List<Bills> getSubscriptionCurrentUsage(@PathParam("subscriptionId") String subscriptionId,@PathParam("fromDate") String fromDate);

	@GET
	@Path("/bill/v1/bills/{billId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON )
	Bills getSubscriptionBillById(@PathParam("billId") String billId);

	@POST
	@Path("/subscription/v1/subscriptions/{id}/cancellationreversal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	CancellationReversalResponse cancellationReversal(@PathParam("id") String id, CancellationReversal cancellationReversal);

	@GET
	@Path("/bill/v2/bills")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	RawResponse<List<BillsList>> getSubscriptionBills(
			@QueryParam("customer.id") String customerId,
			@QueryParam("from") String fromDate,
			@QueryParam("to") String toDate,
			@QueryParam("pageNumber") Integer pageNumber,
			@QueryParam("pageSize") Integer pageSize);

	@GET
	@Path("/bill/v2/bills/{billId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON )
	BillsList getSubscriptionBillByBillId(@PathParam("billId") String billId);
}
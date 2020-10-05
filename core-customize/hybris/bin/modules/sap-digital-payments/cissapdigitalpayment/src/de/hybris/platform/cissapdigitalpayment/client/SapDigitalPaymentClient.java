/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client;

import de.hybris.platform.cissapdigitalpayment.client.model.*;


import java.util.concurrent.TimeoutException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;


/**
 *
 *
 * Client to connect to SAP Digital payment using Charon API.
 */
@OAuth
public interface SapDigitalPaymentClient
{
	/**
	 * Fetch the Registration URL and the session ID from SAP Digital payment
	 *
	 * @return CisSapDigitalPaymentRegistrationUrlResult
	 * @deprecated instead use {@link #getRegistrationUrl(DigitalPaymentsRegistrationRequest)}
	 */
	@Deprecated(since = "1905.2004")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/getregistrationurl?CompanyCode=${CompanyCode}&CustomerCountry=${CustomerCountry}&PaymentMethod=${PaymentMethod}&RoutingCustomParameterValue=${RoutingCustomParameterValue}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentRegistrationUrlResult> getRegistrationUrl() throws TimeoutException;


	/**
	 * Fetch the Registration URL and the session ID from SAP Digital payment
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/getregistrationurl")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	CisSapDigitalPaymentRegistrationUrlResult getRegistrationUrl(DigitalPaymentsRegistrationRequest registrationRequestModel) throws TimeoutException;

	/**
	 * Poll the registered card using the session ID
	 *
	 * @param sessionId session id
	 * @return CisSapDigitalPaymentPollRegisteredCardResult
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/poll/{sessionId}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentPollRegisteredCardResult> pollRegisteredCard(@PathParam("sessionId") final String sessionId);


	/**
	 * Authorize the payment
	 *
	 * @param authorizationRequests authorization request
	 * @return CisSapDigitalPaymentAuthorizationResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/authorizations")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentAuthorizationResultList> authorizatePayment(
			final CisSapDigitalPaymentAuthorizationRequestList authorizationRequests);

	/**
	 * Delete the card
	 *
	 * @param deletCardRequests delete card Request
	 * @return CisSapDigitalPaymentCardDeletionResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/cards/delete")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentCardDeletionResultList> deleteCard(
			final CisSapDigitalPaymentCardDeletionRequestList deletCardRequests);


	/**
	 * Charge the payment
	 *
	 * @param chargeRequests charge requests
	 * @return CisSapDigitalPaymentChargeResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/charges")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentChargeResultList> chargePayment(final CisSapDigitalPaymentChargeRequestList chargeRequests);


	/**
	 * Refund the payment
	 *
	 * @param refundRequests refund requests
	 * @return CisSapDigitalPaymentRefundResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/refunds")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentRefundResultList> refundPayment(final CisSapDigitalPaymentRefundRequestList refundRequests);


	/**
	 * Poll the registered Card
	 * @param sessionId sessionId of digital payment
	 * @return poll model
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/poll/{sessionId}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	DigitalPaymentsPollModel poll(@PathParam("sessionId") final String sessionId);

	/**
	 * Fetches the new card registration details for payment card
	 * @return model for registration url
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/getregistrationurl?CompanyCode=${CompanyCode}&CustomerCountry=${CustomerCountry}&PaymentMethod=${PaymentMethod}&RoutingCustomParameterValue=${RoutingCustomParameterValue}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	DigitalPaymentsRegistrationModel fetchRegistrationUrl();
}

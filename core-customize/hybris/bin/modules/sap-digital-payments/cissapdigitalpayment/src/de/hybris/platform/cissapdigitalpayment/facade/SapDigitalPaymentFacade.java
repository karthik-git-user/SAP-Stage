/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.facade;

import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsPollData;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsRegistrationData;

/**
 *
 * Generic facade interface for SAP DIgital payment. Facade responsible for interacting with the Service to get the
 * registration URL from SAP Digital payment and start the registered card poll process
 *
 */
public interface SapDigitalPaymentFacade
{

	/**
	 * Fetch the registration URL from the SAP Digital payment. Application is redirected to this URL to register the
	 * card with SAP Digital payment
	 *
	 * @return String
	 */
	String getCardRegistrationUrl();

	/**
	 * Creates the registration card polling process
	 *
	 * @param sessionId
	 *           - session-id is passed to digital-payments to get the card details.
	 */

	void createPollRegisteredCardProcess(final String sessionId);


	/**
	 * Polls the registered card
	 * @param sessionId session id of digital payment
	 * @return Card Details
	 */
	DigitalPaymentsPollData pollAndSave(final String sessionId);

	/**
	 * Get the registration details for digital payments
	 * @return registrations details of digital payment
	 */
	DigitalPaymentsRegistrationData getRegistrationUrl();

}

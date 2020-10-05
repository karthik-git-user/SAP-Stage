/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 *
 * Exception class with handle all the poll card request errors to SAP Digital payment
 */
public class SapDigitalPaymentPollRegisteredCardException extends SapDigitalPaymentServiceUnavaliableException
{

	/**
	 * Constructor method
	 */
	public SapDigitalPaymentPollRegisteredCardException(final String message)
	{
		super(message);
	}

}

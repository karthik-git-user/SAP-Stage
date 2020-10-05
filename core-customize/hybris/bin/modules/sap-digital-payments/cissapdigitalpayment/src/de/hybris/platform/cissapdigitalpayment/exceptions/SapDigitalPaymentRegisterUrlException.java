/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 * 
 * Exception class with handle all the registration URL request errors to SAP Digital payment
 */
public class SapDigitalPaymentRegisterUrlException extends SapDigitalPaymentServiceUnavaliableException
{

	/**
	 * Constructor
	 */
	public SapDigitalPaymentRegisterUrlException(final String message)
	{
		super(message);
	}

}

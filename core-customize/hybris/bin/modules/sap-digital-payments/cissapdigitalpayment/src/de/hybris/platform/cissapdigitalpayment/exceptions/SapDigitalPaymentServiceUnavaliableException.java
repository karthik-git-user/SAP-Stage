/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 * Generic exception class to handle all the unspecified exceptions with SAP Digital payment
 */
public class SapDigitalPaymentServiceUnavaliableException extends RuntimeException
{
	private final String message;

	/**
	 * Constructor
	 */
	public SapDigitalPaymentServiceUnavaliableException(final String message)
	{
		super();
		this.message = message;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage()
	{
		return message;
	}





}

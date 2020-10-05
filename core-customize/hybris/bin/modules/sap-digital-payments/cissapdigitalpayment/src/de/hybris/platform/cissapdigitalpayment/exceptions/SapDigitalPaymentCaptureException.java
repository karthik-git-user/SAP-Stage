/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 *
 */
public class SapDigitalPaymentCaptureException extends Exception
{

	private final String message;


	public SapDigitalPaymentCaptureException(final String message)
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

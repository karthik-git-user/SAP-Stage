/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 *
 */
public class SapDigitalPaymentDeleteCardException extends RuntimeException
{
	private final String message;


	/**
	 *
	 */
	public SapDigitalPaymentDeleteCardException(final String message)
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

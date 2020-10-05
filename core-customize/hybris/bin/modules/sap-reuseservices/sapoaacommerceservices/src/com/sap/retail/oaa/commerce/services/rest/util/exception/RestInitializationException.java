/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.rest.util.exception;

/**
 * Exception Class regarding the RestInitalization functionality
 *
 */
public class RestInitializationException extends RuntimeException
{
	public RestInitializationException()
	{
		super();
	}

	public RestInitializationException(final String message)
	{
		super(message);
	}

	public RestInitializationException(final Throwable e)
	{
		super(e);
	}
}
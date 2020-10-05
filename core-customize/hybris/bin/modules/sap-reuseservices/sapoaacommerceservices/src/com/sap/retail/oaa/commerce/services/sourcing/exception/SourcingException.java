/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.exception;

/**
 * Exception Class regarding the Sourcing functionality
 */
public class SourcingException extends RuntimeException
{
	public SourcingException()
	{
		super();
	}

	public SourcingException(final String message)
	{
		super(message);
	}

	public SourcingException(final Throwable e)
	{
		super(e);
	}
}

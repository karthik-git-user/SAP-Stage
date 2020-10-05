/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.exception;

/**
 * Exception Class regarding the aggregated availability functionality
 */
public class ATPException extends RuntimeException
{
	public ATPException()
	{
		super();
	}

	public ATPException(final String message)
	{
		super(message);
	}

	public ATPException(final Throwable e)
	{
		super(e);
	}
}

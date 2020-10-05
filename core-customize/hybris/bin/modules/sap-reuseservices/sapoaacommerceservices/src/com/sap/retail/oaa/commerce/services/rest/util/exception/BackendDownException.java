/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.rest.util.exception;

/**
 * Exception class regarding the availability of the CAR back end
 */
public class BackendDownException extends RuntimeException
{
	public BackendDownException()
	{
		super();
	}

	public BackendDownException(final String message)
	{
		super(message);
	}

	public BackendDownException(final Throwable e)
	{
		super(e);
	}

	public BackendDownException(final String message, final Throwable e)
	{
		super(message, e);
	}


}

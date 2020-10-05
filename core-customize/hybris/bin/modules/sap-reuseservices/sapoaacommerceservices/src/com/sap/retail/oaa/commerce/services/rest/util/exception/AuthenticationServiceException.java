/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.rest.util.exception;

/**
 * Exception class regarding the authentication service functionality
 *
 */
public class AuthenticationServiceException extends RuntimeException
{
	public AuthenticationServiceException()
	{
		super();
	}

	public AuthenticationServiceException(final String message)
	{
		super(message);
	}

	public AuthenticationServiceException(final Throwable e)
	{
		super(e);
	}
}
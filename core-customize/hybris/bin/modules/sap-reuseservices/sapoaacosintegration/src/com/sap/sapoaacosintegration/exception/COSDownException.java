/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.exception;

/**
 * Exception class regarding the availability of the CAR back end
 */
public class COSDownException extends RuntimeException
{
	public COSDownException()
	{
		super();
	}

	public COSDownException(final String message)
	{
		super(message);
	}

	public COSDownException(final Throwable e)
	{
		super(e);
	}

	public COSDownException(final String message, final Throwable e)
	{
		super(message, e);
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.api.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Gigya API exception class
 */
public class GigyaApiException extends SystemException
{

	private final int gigyaErrorCode;

	public GigyaApiException(final String message)
	{
		super(message);
		this.gigyaErrorCode = -1;
	}

	public GigyaApiException(final String message, final Throwable cause)
	{
		super(message, cause);
		this.gigyaErrorCode = -1;
	}

	public GigyaApiException(final String message, final int gigyaErrorCode)
	{
		super(message);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public GigyaApiException(final Throwable cause, final int gigyaErrorCode)
	{
		super(cause);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public GigyaApiException(final String message, final int gigyaErrorCode, final Throwable cause)
	{
		super(message, cause);
		this.gigyaErrorCode = gigyaErrorCode;
	}

	public int getGigyaErrorCode()
	{
		return gigyaErrorCode;
	}

}

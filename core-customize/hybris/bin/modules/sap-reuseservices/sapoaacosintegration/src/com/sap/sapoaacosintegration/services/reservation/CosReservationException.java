/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation;

/**
 *
 */
public class CosReservationException extends RuntimeException
{

	public CosReservationException()
	{
		super();
	}

	public CosReservationException(final String message)
	{
		super(message);
	}

	public CosReservationException(final Throwable e)
	{
		super(e);
	}
}

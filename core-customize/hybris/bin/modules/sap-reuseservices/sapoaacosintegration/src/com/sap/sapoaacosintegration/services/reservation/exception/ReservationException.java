/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.exception;

/**
 *
 */
public class ReservationException extends RuntimeException
{
	public ReservationException()
	{
		super();
	}

	public ReservationException(final String message)
	{
		super(message);
	}

	public ReservationException(final Throwable e)
	{
		super(e);
	}
}

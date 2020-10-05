/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.exception;


/**
 * Exception class regarding the REST service for updating the temporary reservation.
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

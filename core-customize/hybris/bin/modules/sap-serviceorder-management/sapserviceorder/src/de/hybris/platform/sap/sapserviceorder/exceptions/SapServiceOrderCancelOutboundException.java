/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.exceptions;

/**
 * Exception for Service Order Cancel Outbound
 *	
 */
public class SapServiceOrderCancelOutboundException extends RuntimeException {

	private static final long serialVersionUID = -6529416934930670212L;


	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 */
	public SapServiceOrderCancelOutboundException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public SapServiceOrderCancelOutboundException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}
}

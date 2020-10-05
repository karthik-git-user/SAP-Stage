/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.exception;

public class SapBillingInvoiceUserException extends Exception {

	private static final long serialVersionUID = -6529406933930670211L;

	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 */
	public SapBillingInvoiceUserException(final String msg)
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
	public SapBillingInvoiceUserException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}
}

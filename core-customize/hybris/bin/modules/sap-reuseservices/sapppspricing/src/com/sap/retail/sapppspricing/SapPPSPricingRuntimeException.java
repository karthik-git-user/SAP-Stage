/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

/**
 * Exception indicating that the price calculation via PPS was not successful
 */
public class SapPPSPricingRuntimeException extends RuntimeException
{

	private static final long serialVersionUID = -2276284059733818279L;

	@SuppressWarnings("javadoc")
	public SapPPSPricingRuntimeException(final Exception e)
	{
		super(e);
	}

	@SuppressWarnings("javadoc")
	public SapPPSPricingRuntimeException(final String arg)
	{
		super(arg);
	}

	@SuppressWarnings("javadoc")
	public SapPPSPricingRuntimeException(final String arg, final Throwable e)
	{
		super(arg, e);
	}
}

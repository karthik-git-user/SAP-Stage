/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.constants;

/**
 * Global class for all Sapoaacommerceservices constants. You can add global constants for your extension into this
 * class.
 */
public final class SapoaacommerceservicesConstants extends GeneratedSapoaacommerceservicesConstants //NOSONAR
{
	public static final String EXTENSIONNAME = "sapoaacommerceservices";

	/** Indicates the reservation status in the mode order */
	public static final String RESERVATION_STATUS_ORDER = "O";
	/** Indicates the reservation status in the mode cart */
	public static final String RESERVATION_STATUS_CART = "C";
	/** Error message for offline scenario */
	public static final String BACKEND_DOWN_MESSAGE = "Backend is not responding";

	private SapoaacommerceservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}
}

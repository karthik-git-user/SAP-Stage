/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;


/**
 * OAA specific status values for the CommerceCartModification statusCode. As the statusCode is a string it is not
 * limited to any specific set of values. These are just the values exposed by the commerceservices.
 *
 *
 */
public final class SapOaaCommerceCartModificationStatus implements CommerceCartModificationStatus
{
	/**
	 * Indicates a failure during ATP validation
	 */
	public static final String ATP_VALIDATION_FAILURE = "atpValidationFailure";

	private SapOaaCommerceCartModificationStatus()
	{
		// private constructor to prevent instantiation
	}
}

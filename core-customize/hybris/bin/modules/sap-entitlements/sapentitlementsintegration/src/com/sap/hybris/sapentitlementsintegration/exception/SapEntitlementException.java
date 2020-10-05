/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Exception for SAP Entitlement scenarios
 */
public class SapEntitlementException extends SystemException
{
	public SapEntitlementException(final String message)
	{
		super(message);
	}

}

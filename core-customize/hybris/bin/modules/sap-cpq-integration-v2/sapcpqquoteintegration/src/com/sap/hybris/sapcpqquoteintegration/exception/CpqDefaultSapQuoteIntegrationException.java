/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.exception;

import java.lang.RuntimeException;
import java.lang.String;
import java.lang.Throwable;

/**
 * The generic exception class for the com.hybris package.
 */
public class CpqDefaultSapQuoteIntegrationException extends RuntimeException {
	private static final long serialVersionUID = -7403307082453533142L;

	/**
	 * Creates a new instance with the given message.
	 *
	 * @param message the reason for this HybrisSystemException
	 */
	public CpqDefaultSapQuoteIntegrationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new instance using the given message and cause exception.
	 *
	 * @param message The reason for this HybrisSystemException.
	 * @param cause   the Throwable that caused this HybrisSystemException.
	 */
	public CpqDefaultSapQuoteIntegrationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}

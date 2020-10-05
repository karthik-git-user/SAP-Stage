/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.exception;

public class DefaultSapCpqQuoteIntegrationException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with the given message.
	 *
	 * @param message the reason for this HybrisSystemException
	 */
	public DefaultSapCpqQuoteIntegrationException(final String message) {
		super(message);
	}

	/**
	 * Creates a new instance using the given message and cause exception.
	 *
	 * @param message The reason for this HybrisSystemException.
	 * @param cause   the Throwable that caused this HybrisSystemException.
	 */
	public DefaultSapCpqQuoteIntegrationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}

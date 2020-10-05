/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;

/**
 * Exception is thrown when subscription service unable to process request because of external factors
 * like Database Unavailable, API Server not available etc.
 *
 */
public class SapSubscriptionSystemException extends SystemException {
    public SapSubscriptionSystemException(String message) {
        super(message);
    }

    public SapSubscriptionSystemException(Throwable cause) {
        super(cause);
    }

    public SapSubscriptionSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.ConfigurationException;

/**
 * Exception is thrown when Configurations are wrong/missing for Subscription.
 *
 */
public class SapSubscriptionConfigurationException extends ConfigurationException {
    public SapSubscriptionConfigurationException(String message) {
        super(message);
    }

    public SapSubscriptionConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;

public class RevenueCloudUnknownException extends SystemException {
    public RevenueCloudUnknownException() {
        super("Unknown Exception Occurred");
    }

    public RevenueCloudUnknownException(String message) {
        super(message);
    }
}

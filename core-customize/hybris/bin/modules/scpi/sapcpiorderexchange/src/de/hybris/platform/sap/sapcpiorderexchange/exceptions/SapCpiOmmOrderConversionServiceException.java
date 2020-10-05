/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.exceptions;

public class SapCpiOmmOrderConversionServiceException extends RuntimeException {

    public SapCpiOmmOrderConversionServiceException(final String message) {
        super(message);
    }

    public SapCpiOmmOrderConversionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}

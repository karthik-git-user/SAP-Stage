/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalPaymentsBusinessException extends ConfigurationException {
    private static final Logger LOG = LoggerFactory.getLogger(DigitalPaymentsBusinessException.class);

    public DigitalPaymentsBusinessException() {
        super("Error in digital payments");
        LOG.info("Possible problems: Invalid Business data, Incorrect configurations");
    }

    public DigitalPaymentsBusinessException(String message) {
        super(message);
    }
}

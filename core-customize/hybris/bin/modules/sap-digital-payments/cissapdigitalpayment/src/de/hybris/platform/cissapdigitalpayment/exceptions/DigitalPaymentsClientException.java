/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalPaymentsClientException extends ConfigurationException {
    private static final Logger LOG = LoggerFactory.getLogger(DigitalPaymentsClientException.class);

    public DigitalPaymentsClientException() {
        super("Wrong or Missing Digital Payments Configurations.");
        LOG.info("Possible problems: Configurations are missing. or check if Digital payments accessible? ");
    }

    public DigitalPaymentsClientException(String message) {
        super(message);
    }
}

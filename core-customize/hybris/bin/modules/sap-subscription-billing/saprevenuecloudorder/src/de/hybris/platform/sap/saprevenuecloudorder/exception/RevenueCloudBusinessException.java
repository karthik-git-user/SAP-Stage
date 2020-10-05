/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevenueCloudBusinessException extends ConfigurationException {
    private static final Logger LOG = LoggerFactory.getLogger(RevenueCloudBusinessException.class);

    public RevenueCloudBusinessException() {
        super("Error in revenue cloud");
        LOG.info("Possible problems: Invalid Business data, Incorrect configurations");
    }

    public RevenueCloudBusinessException(String message) {
        super(message);
    }
}

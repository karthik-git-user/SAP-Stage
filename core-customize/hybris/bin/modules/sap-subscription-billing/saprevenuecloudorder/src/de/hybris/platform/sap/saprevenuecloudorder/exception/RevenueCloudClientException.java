/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevenueCloudClientException extends ConfigurationException {
    private static final Logger LOG = LoggerFactory.getLogger(RevenueCloudClientException.class);

    public RevenueCloudClientException() {
        super("Wrong or Missing Revenue Cloud Configurations.");
        LOG.info("Possible problems: Configurations are missing. or check if Revenue Cloud accessible? ");
    }

    public RevenueCloudClientException(String message) {
        super(message);
    }
}

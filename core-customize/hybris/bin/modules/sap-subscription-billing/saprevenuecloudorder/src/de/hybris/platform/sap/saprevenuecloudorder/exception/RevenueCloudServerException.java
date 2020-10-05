/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.exception;

import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevenueCloudServerException extends SystemException {

    private static final Logger LOG = LoggerFactory.getLogger(RevenueCloudServerException.class);

    public RevenueCloudServerException() {
        super("Error in revenue cloud");
        LOG.info("Revenue Cloud Server is throwing exception.");
    }

    public RevenueCloudServerException(String message) {
        super(message);
    }
}

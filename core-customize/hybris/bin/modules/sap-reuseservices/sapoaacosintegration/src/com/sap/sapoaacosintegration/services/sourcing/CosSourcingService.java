/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;


/**
 * Soucing service provider for COS
 */
public interface CosSourcingService extends SourcingService
{



	/**
	 * Calls the COS rest service for fetching sourcing results based on the {@link AbstractOrderModel}
	 *
	 * @param orderModel
	 *           The orderModel for which COS rest call is to be made
	 * @return {@link SourcingResponse}
	 */
	SourcingResponse callCosRestService(AbstractOrderModel orderModel);
}

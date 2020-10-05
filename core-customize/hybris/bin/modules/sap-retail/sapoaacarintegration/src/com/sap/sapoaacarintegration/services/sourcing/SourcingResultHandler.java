/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing;


import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Sourcing Result Handler
 */
public interface SourcingResultHandler
{
	/**
	 * Adds sourcing result to cart and checks if the sourcing was successful
	 *
	 * @param sourcing
	 *           sourcing result
	 * @param cart
	 *           hybris shopping cart
	 * @param restServiceConfig
	 *           REST service configuration
	 * @throws SourcingException
	 */
	void persistSourcingResultInCart(final SourcingResponse sourcing, final AbstractOrderModel cart,
			final RestServiceConfiguration restServiceConfig);
}

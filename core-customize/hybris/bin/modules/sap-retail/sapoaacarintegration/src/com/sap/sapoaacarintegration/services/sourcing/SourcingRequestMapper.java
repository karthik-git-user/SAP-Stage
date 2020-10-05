/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Maps Hybris cart or order model to sourcing request
 */
public interface SourcingRequestMapper
{
	/**
	 * Maps hybris cart to sourcing request
	 *
	 * @param cartModel
	 *           Hybris Cart
	 * @param execAllStrategies
	 *           execute all strategies
	 * @param reserve
	 *           create or update a temporary reservation
	 * @param restConfiguration
	 *           configuration object
	 * @return ABAP sourcing request
	 * @throws SourcingException
	 */
	SourcingAbapRequest mapCartModelToSourcingRequest(CartModel cartModel, boolean execAllStrategies, boolean reserve,
			RestServiceConfiguration restConfiguration);


	/**
	 * Maps hybris order to sourcing request
	 *
	 * @param orderModel
	 *           Hybris Cart
	 * @param execAllStrategies
	 *           execute all strategies
	 * @param reserve
	 *           create or update a temporary reservation
	 * @param restConfiguration
	 *           configuration object
	 * @return ABAP sourcing request
	 * @throws SourcingException
	 */
	SourcingAbapRequest mapOrderModelToSourcingRequest(OrderModel orderModel, boolean execAllStrategies, boolean reserve,
			RestServiceConfiguration restConfiguration);
}

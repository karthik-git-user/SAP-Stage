/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.springframework.core.Ordered;

import com.sap.ppengine.client.dto.PriceCalculateResponse;


/**
 * Mapper for the result of a PPS call to the order / cart
 *
 */
public interface PriceCalculateToOrderMapper extends Ordered
{
	/**
	 * Maps the given response to the given order / cart model
	 * 
	 * @param response
	 * @param order
	 */
	void map(PriceCalculateResponse response, AbstractOrderModel order);
}

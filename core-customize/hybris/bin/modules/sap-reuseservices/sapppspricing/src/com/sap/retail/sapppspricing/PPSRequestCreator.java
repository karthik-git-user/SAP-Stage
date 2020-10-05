/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import com.sap.ppengine.client.dto.PriceCalculate;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * Creator of a request for the PPS
 *
 */
public interface PPSRequestCreator
{

	/**
	 * Create a price calculation request for a single product in the catalog
	 * 
	 * @param productModel
	 *           product for which price is requested
	 * @param isNet
	 *           flag net yes / no
	 * @return request built
	 */
	PriceCalculate createRequestForCatalog(ProductModel productModel, boolean isNet);

	/**
	 * Create a price calculation request for a complete order / cart
	 * 
	 * @param order
	 *           the order / cart
	 * @return request built
	 */
	PriceCalculate createRequestForCart(AbstractOrderModel order);

}

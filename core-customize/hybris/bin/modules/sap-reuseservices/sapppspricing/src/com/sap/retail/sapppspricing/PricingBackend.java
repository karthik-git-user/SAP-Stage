/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

import java.util.List;


/**
 * Top level interface offering the price calculation for products / carts
 */
public interface PricingBackend
{
	/**
	 * Price calculation for a cart
	 *
	 * @param order
	 * @throws SapPPSPricingRuntimeException
	 */
	public void readPricesForCart(AbstractOrderModel order);

	/**
	 * Price calculation for a list of products
	 *
	 * @param productModels
	 * @param isNet
	 * @return List<PriceInformation>
	 * @throws SapPPSPricingRuntimeException
	 */
	public List<PriceInformation> readPriceInformationForProducts(List<ProductModel> productModels, boolean isNet);

}

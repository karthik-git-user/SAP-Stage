/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.sap.sapsalesordersimulation.service;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
/**
 * Get the price information for the  product
 *
 */
public interface PricingService {
	
	/**
	 * Get the Price information for the product
	 *
	 * @param product the ProductModel
	 * l
	 * @return list of PriceInformation for the corresponding product 
	 */
	List<PriceInformation> getPriceForProduct(ProductModel product);
}

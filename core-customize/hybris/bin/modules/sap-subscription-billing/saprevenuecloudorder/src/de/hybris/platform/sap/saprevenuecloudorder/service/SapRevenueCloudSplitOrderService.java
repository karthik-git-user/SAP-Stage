/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import java.math.BigDecimal;
import java.util.Map;

import de.hybris.platform.core.model.order.CartModel;

/**
 * 
 * Methods to handle authorization amount split 
 *
 */
public interface SapRevenueCloudSplitOrderService {
	
	/**
	 * Splits the authorization amount split in the cart for different target systems
	 * 
	 * @param  cart cart
	 * 
	 * @return authorizationAmountMap<Target,Amount>
	 */
	
	Map<String,BigDecimal> getAuthorizationAmountListFromCart(final CartModel cart);

}

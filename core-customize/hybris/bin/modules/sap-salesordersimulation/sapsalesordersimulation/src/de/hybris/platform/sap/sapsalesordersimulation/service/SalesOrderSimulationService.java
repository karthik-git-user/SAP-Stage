/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
/**
 * 
 * Service to fetch the price, stock and also to verify the credit limit
 *
 */
public interface SalesOrderSimulationService {
	
	/**
	 * Get all price information for given product.
	 *
	 * @param productModel the product model
	 * @return map containing stock information as well as price information.
	 */
	public List<PriceInformation> getPriceDetailsForProduct(ProductModel productModel);
	
	/**
	 * Get the stock level information  for the product.
	 *
	 * @param productModel the ProductModel
	 * @param baseStore   BaseStoreModel
	 * @return containing SapProductAvailability information for the corresponding plants.
	 */
	public SapProductAvailability getStockAvailability(ProductModel productModel,BaseStoreModel baseStore);

	
	/**
	 * Get the stock level information in different warehouses for the product.
	 *
	 * @param productModel the ProductModel
	 * @param warehouses   Collection of WarehouseModel
	 * @return containing SapProductAvailability information for the corresponding plants.
	 */
	public Map<String, SapProductAvailability> getStockLevels(ProductModel productModel, Collection<WarehouseModel> warehouses);

	/**
	 * Get the price level information for products.
	 *
	 * @param productModels list of products
	 * @return List containing priceInformation .
	 */
	public Map<String, List<PriceInformation>> getPriceDetailsForProducts(List<ProductModel> productModels);

	/**
	 * Sets the live stock, price, discounts, delivery & total in cartModel and CartData.
	 *
	 * @param cartModel The cart Model
	 * @param CartData  The Cart Data
	 * @return
	 */
	public void setCartDetails(AbstractOrderModel cartModel);
	

	/**
	 * Gets the credit limit status for the Cart for the user.
	 *
	 * @param cartModel The cart Model
	 * @param user The User Model
	 * @return boolean
	 */
	public Boolean checkCreditLimitExceeded(ItemModel cartModel, UserModel user);


}

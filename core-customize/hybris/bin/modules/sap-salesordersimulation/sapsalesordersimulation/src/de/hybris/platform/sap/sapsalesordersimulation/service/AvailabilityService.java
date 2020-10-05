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

import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
/**
 * 
 * Availability service provides the availability and future stock details.
 *
 */
public interface AvailabilityService {
	/**
	 * Get the stock level information for the product and the warehouses
	 *
	 * @param product - Product for which stock level is fetched
	 * @param warehouses - list of warehouses for which the availability has to be fetched
	 * @param warehouses   Collection of WarehouseModel
	 * @return list of StockLevelModels for the corresponding product and
	 *         warehouses.
	 */
	Collection<StockLevelModel> getStockAvailability(ProductModel product, Collection<WarehouseModel> warehouses);

	/**
	 * Get the stock level information for the product and the warehouse
	 *
	 * @param product - Product for which future stock level has to be  fetched
	 * @param warehouses - list of warehouses for which the future stock has to be fetched
	 * @return list of StockLevelModels for the corresponding product and warehouse.
	 */
	Collection<FutureStockModel> getFutureAvailability(ProductModel product, Collection<WarehouseModel> warehouses);

	/**
	 * Get the SapProductAvailability (contains stock , future stock details) for
	 * the product and the given basestore
	 *
	 * @param productModel   the ProductModel
	 * @param baseStore the baseStoreModel
	 * @return SapProductAvailability for the corresponding product and warehouse.
	 */
	SapProductAvailability getProductAvailability(ProductModel productModel, BaseStoreModel baseStore);
}

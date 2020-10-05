/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.stock;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;

import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;



/**
 * Stock Service used in Omni Channel Commerce.
 */
public interface SapOaaCommerceStockService extends CommerceStockService
{
	/**
	 * Get the availability for a given cart item.
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product This product availability will be checked.
	 *
	 * @return list of availability
	 */
	public List<ATPAvailability> getAvailabilityForProduct(final String cartGuid, String itemId, final ProductModel product);

	/**
	 * Get the availability for a given product and source.
	 *
	 * @param product This product availability will be checked.
	 * @param source  Product availability will be checked against given source.
	 * @return list of availability
	 */
	public List<ATPAvailability> getAvailabilityForProductAndSource(final ProductModel product, final String source);

	/**
	 * Get the availability for a given cart item and source.
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product This product availability will be checked.
	 * @param source Product availability will be checked against given source.
	 * @return list of availability
	 */
	public List<ATPAvailability> getAvailabilityForProductAndSource(final String cartGuid, String itemId,
			final ProductModel product, final String source);

	/**
	 * Get the availability for a given cart items and corresponding product unit.
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param productUnit This product unit will be checked.
	 * @param productList This list of product whose availability will be checked.
	 * @return list of ATP product availability
	 */
	public List<ATPProductAvailability> getAvailabilityForProducts(final String cartGuid, String itemId, final String productUnit,
			final List<ProductModel> productList);

	/**
	 * Get the availability for a given cart item and sources.
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product This product availability will be checked.
	 * @param sourcesList This source list availability will be checked.
	 * @return list of ATP product availability
	 */
	public List<ATPProductAvailability> getAvailabilityForProductAndSources(final String cartGuid, String itemId,
			final ProductModel product, final List<String> sourcesList);


	/**
	 * Get current stock information calling ATP Service (CAR)
	 *
	 * @param cartGuid The id for the cart.
	 * @param productModel This product availability will be checked.
	 * @param pointOfService This point of service availability will be checked.
	 * @return total aggregated ATP amount
	 */
	public Long getAvailableStockLevelForPos(final String cartGuid, final ProductModel productModel,
			final PointOfServiceModel pointOfService);

	/**
	 * Get the stock information for a product and base store
	 * 
	 * @param product This product rough stock will be checked.
	 * @param baseStore This base store rough stock will be checked.
	 * @return StockLevelModel
	 */
	public StockLevelModel getStockLevelForRSI(final ProductModel product, final BaseStoreModel baseStore);

	/**
	 * Get current stock information calling ATP Service (CAR)
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param productModel This product stock will be checked.
	 * @param pointOfServiceModel This point of service stock will be checked.
	 * @return total aggregated ATP amount
	 */
	public Long getAvailableStockLevel(final String cartGuid, final String itemId, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel);
	
	
	/**
	 * Returns stock data for combination of given product and base store
	 * 
	 * @param product This product availability will be checked.
	 * @param baseStore This is used for validating associated warehouse . 
	 * @param stockData This is used for populating Stock Level and Stock Level Status.
	 * @return {@link StockData} information
	 */
	public StockData getStockDataForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore,final StockData stockData);

}

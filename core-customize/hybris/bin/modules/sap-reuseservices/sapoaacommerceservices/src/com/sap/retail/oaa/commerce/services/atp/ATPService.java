/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;

import de.hybris.platform.core.model.product.ProductModel;




/**
 * Service for aggregated availability
 */
public interface ATPService
{

	/**
	 * Call the REST Service in Customer Activity Repository (CAR) for a given product to get current product
	 * availability
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product The product availability to be checked.
	 * @return list of current product availability
	 */
	List<ATPAvailability> callRestAvailabilityServiceForProduct(final String cartGuid, String itemId, final ProductModel product);

	/**
	 * Call the REST Service in Customer Activity Repository (CAR) for a given product to get current product
	 * availability in the given source
	 *
	 * @param product The product availability to be checked.
	 * @param source The source availability to be checked.
	 * @return list of current product location availability.
	 */
	List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final ProductModel product, final String source);

	/**
	 * Call the REST Service in Customer Activity Repository (CAR) for a given product to get current product
	 * availability in the given source
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product The product availability to be checked.
	 * @param source The source availability to be checked.
	 * @return list of current product location availability.
	 */
	List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final String cartGuid, String itemId,
			final ProductModel product, final String source);

	/**
	 * Call the REST Service in Customer Activity Repository (CAR) for a given product list to get current product
	 * availability lines
	 *
	 * @param cartGuid
	 * 			 The id for the cart.
	 * @param itemIdList
	 *           Must have the same order as parameter productList
	 * @param productUnit
	 *           Single unit for all products
	 * @param productList
	 *           Must have the same order as parameter itemIdList
	 * @return list of current availability for given products
	 */
	List<ATPProductAvailability> callRestAvailabilityServiceForProducts(final String cartGuid, List<String> itemIdList,
			final String productUnit, List<ProductModel> productList);


	/**
	 * Call the REST Service in Customer Activity Repository (CAR) for a given product and sources list to get current
	 * product availability lines
	 *
	 * @param cartGuid The id for the cart.
	 * @param itemId The id for the item.
	 * @param product The product availability to be checked.
	 * @param sourcesList The source list availability to be checked.
	 * @return list of current availability for given product and sources
	 */
	List<ATPProductAvailability> callRestAvailabilityServiceForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList);

}
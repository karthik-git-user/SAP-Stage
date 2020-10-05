/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;

import de.hybris.platform.core.model.product.ProductModel;



/**
 * Resource Builder for ATP Service
 */
public interface ATPResourcePathBuilder
{

	/**
	 * Set URI Query Parameters for calling availability service for batch call
	 *
	 * @param cartId
	 * @param itemIdList
	 * @param productUnit
	 * @param restConfiguration
	 * @return URI Query parameters
	 */
	MultiValueMap<String, String> compileUriQueryParameters(final String cartId, final List<String> itemIdList,
			final String productUnit, final RestServiceConfiguration restConfiguration);

	/**
	 * Set URI Query Parameters for calling availability service
	 *
	 * @param cartId
	 * @param itemId
	 * @param productUnit
	 * @param restConfiguration
	 * @return URI Query parameters
	 */
	MultiValueMap<String, String> compileUriQueryParameters(final String cartId, final String itemId, final String productUnit,
			final RestServiceConfiguration restConfiguration);

	/**
	 * Assembles the resource service path string for batch call
	 *
	 * @param product
	 * @param sourcesList
	 * @return assembled service path
	 */
	String buildBatchRessourceServicePath(final ProductModel product, final List<String> sourcesList);

	/**
	 * Assembles the resource service path string for batch call
	 *
	 * @param productList
	 * @return assembled service path
	 */
	String buildBatchRessourceServicePath(final List<ProductModel> productList);

	/**
	 * Assembles the resource service path string for product / source ATP call
	 *
	 * @param product
	 * @param source
	 * @return assembled service path
	 */
	String buildRessourceServicePath(final ProductModel product, final String source);

	/**
	 * Assembles the resource service path string for product ATP call
	 *
	 * @param product
	 * @return assembled service path
	 */
	String buildRessourceServicePath(final ProductModel product);

}

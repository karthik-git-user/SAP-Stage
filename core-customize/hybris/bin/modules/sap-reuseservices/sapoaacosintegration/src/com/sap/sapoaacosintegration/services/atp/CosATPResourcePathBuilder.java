/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.springframework.http.HttpEntity;


/**
 * Resource Builder for COS ATP Service
 */
public interface CosATPResourcePathBuilder
{

	/**
	 * prepare HttpEntity for given product and source.
	 *
	 * @param product
	 * @param sourceId
	 * @return HttpEntity
	 *
	 */
	HttpEntity prepareRestCallForProductAndSource(final ProductModel product, final String sourceId);

	/**
	 * prepare HttpEntity for given product.
	 *
	 * @param product
	 * @return HttpEntity
	 *
	 */
	HttpEntity prepareRestCallForProduct(final ProductModel product);


	/**
	 * prepare HttpEntity for given product List.
	 *
	 * @param productList
	 * @return HttpEntity
	 *
	 */
	HttpEntity prepareRestCallForProducts(List<ProductModel> productList);


}

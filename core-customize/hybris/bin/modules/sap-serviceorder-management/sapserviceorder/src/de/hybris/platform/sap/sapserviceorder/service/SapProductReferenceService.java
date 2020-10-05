/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service;

import java.util.List;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Interface for retrieving product references
 */
public interface SapProductReferenceService {
	
	/**
	 * Method for retrieving product references for given target product
	 * @param product target in product reference model
	 * @return list of product references containing given product as target.
	 */
	public List<ProductReferenceModel> getProductReferences(final ProductModel product);

}

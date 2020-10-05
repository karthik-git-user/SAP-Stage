/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.daos.ProductReferencesDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapserviceorder.service.SapProductReferenceService;

/**
 * Implementation service for retrieving product references
 */
public class DefaultSapProductReferenceService implements SapProductReferenceService {
	

	private ProductReferencesDao productReferencesDao;
	
	@Override
	public List<ProductReferenceModel> getProductReferences(ProductModel product) {
		if(product != null) {
			return productReferencesDao.findAllReferences(product);
		}
		return Collections.emptyList();
	}

	public ProductReferencesDao getProductReferencesDao() {
		return productReferencesDao;
	}

	public void setProductReferencesDao(ProductReferencesDao productReferencesDao) {
		this.productReferencesDao = productReferencesDao;
	}

}

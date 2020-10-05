/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;


/**
 * SAP Service Product populator
 */
public class DefaultSapServiceProductPopulator implements Populator<ProductModel, ProductData>
{

	@Override
	public void populate(ProductModel source, ProductData target) {
		
		target.setServiceCode((source.getServiceCode() != null && !source.getServiceCode().isEmpty()) ? source.getServiceCode() : "");
	}


	

}

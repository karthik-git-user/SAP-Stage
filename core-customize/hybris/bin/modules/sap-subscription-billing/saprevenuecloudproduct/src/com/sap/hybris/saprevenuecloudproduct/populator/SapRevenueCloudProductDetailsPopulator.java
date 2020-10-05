/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class SapRevenueCloudProductDetailsPopulator implements Populator<ProductModel, ProductData>{
	@Override
	public void populate(ProductModel source, ProductData target) throws ConversionException {
		target.setRenewalTerm(source.getRenewalTerm() != null ? source.getRenewalTerm().toString() : null);
        target.setMinimumTerm(source.getContractTerms() != null ? source.getContractTerms().toString() : null);
        target.setSubscriptionValidTerm(source.getSubscriptionValidTerm() != null ? source.getSubscriptionValidTerm().toString() : null);
        
	}

}

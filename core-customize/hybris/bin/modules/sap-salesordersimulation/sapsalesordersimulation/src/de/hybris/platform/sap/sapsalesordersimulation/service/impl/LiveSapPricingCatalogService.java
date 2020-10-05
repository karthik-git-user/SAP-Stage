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
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.List;

import de.hybris.platform.commerceservices.price.impl.NetPriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sapsalesordersimulation.service.PricingService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
/**
 * 
 * Live pricing fetch for catalog
 *
 */
public class LiveSapPricingCatalogService  extends NetPriceService {
private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
private PricingService pricingService;
	
	@Override
	public List<PriceInformation> getPriceInformationsForProduct(ProductModel model)
	{
		if (getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled()) {
			
			return getPricingService().getPriceForProduct(model)  ;
			
		}
		
		return super.getPriceInformationsForProduct(model);
	}

	protected SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}


	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}


	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	

}

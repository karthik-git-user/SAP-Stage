/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.converters.populator.StockPopulator;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Populate the product data with stock information
 */
public class DefaultOaaStockPopulator<SOURCE extends ProductModel, TARGET extends StockData> extends StockPopulator<SOURCE, TARGET>
{
	private CommerceStockService oaaStockService;
	
	
	public CommerceStockService getOaaStockService() {
		return oaaStockService;
	}

	public void setOaaStockService(CommerceStockService oaaStockService) {
		this.oaaStockService = oaaStockService;
	}


	@Override
	public void populate(final SOURCE productModel, final TARGET stockData)
	{
		
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (!isStockSystemEnabled(baseStore))
		{
			stockData.setStockLevelStatus(StockLevelStatus.INSTOCK);
			stockData.setStockLevel(Long.valueOf(0));
		}
		else
		{
			((SapOaaCommerceStockService)getOaaStockService()).getStockDataForProductAndBaseStore(productModel, baseStore, stockData);
		}
	}

	@Override
	protected boolean isStockSystemEnabled(final BaseStoreModel baseStore)
	{
		return getOaaStockService().isStockSystemEnabled(baseStore);
	}
}

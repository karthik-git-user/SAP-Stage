/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.stock.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.stock.strategy.impl.DefaultStockLevelStatusStrategy;


/**
 * Example implementation for mapping from rough stock indicator to stock level status.
 */
public class DefaultSapOaaStockLevelStatusStrategy extends DefaultStockLevelStatusStrategy
{
	public static final String RED = "R";
	public static final String YELLOW = "Y";
	public static final String GREEN = "G";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.stock.strategy.impl.DefaultStockLevelStatusStrategy#checkStatus(de.hybris.platform.
	 * ordersplitting.model.StockLevelModel)
	 */
	@Override
	public StockLevelStatus checkStatus(final StockLevelModel stockLevel)
	{
		if (stockLevel == null || stockLevel.getSapoaa_roughStockIndicator() == null)
		{
			return StockLevelStatus.OUTOFSTOCK;
		}

		switch (stockLevel.getSapoaa_roughStockIndicator())
		{
			case GREEN:
				return StockLevelStatus.INSTOCK;
			case YELLOW:
				return StockLevelStatus.LOWSTOCK;
			case RED:
				return StockLevelStatus.OUTOFSTOCK;
			default:
				return StockLevelStatus.INSTOCK;
		}
	}
}

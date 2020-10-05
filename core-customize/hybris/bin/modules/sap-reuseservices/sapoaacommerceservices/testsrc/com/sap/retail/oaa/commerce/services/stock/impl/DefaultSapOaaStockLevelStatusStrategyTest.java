/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.stock.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 */
@UnitTest
public class DefaultSapOaaStockLevelStatusStrategyTest
{

	private DefaultSapOaaStockLevelStatusStrategy classUnderTest = null;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSapOaaStockLevelStatusStrategy();
	}

	@Test
	public void checkStatusTest()
	{
		StockLevelModel stockLevel = null;

		StockLevelStatus stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertNotNull(stockLevelStatus);
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, stockLevelStatus);

		stockLevel = new StockLevelModel();
		stockLevel.setSapoaa_roughStockIndicator(null);
		stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, stockLevelStatus);

		stockLevel.setSapoaa_roughStockIndicator("Default");
		stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertEquals(StockLevelStatus.INSTOCK, stockLevelStatus);

		stockLevel.setSapoaa_roughStockIndicator(DefaultSapOaaStockLevelStatusStrategy.GREEN);
		stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertEquals(StockLevelStatus.INSTOCK, stockLevelStatus);

		stockLevel.setSapoaa_roughStockIndicator(DefaultSapOaaStockLevelStatusStrategy.YELLOW);
		stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertEquals(StockLevelStatus.LOWSTOCK, stockLevelStatus);

		stockLevel.setSapoaa_roughStockIndicator(DefaultSapOaaStockLevelStatusStrategy.RED);
		stockLevelStatus = classUnderTest.checkStatus(stockLevel);
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, stockLevelStatus);
	}
}

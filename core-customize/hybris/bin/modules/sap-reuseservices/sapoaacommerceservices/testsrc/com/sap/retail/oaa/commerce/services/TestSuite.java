/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.sap.retail.oaa.commerce.services.atp.strategy.impl.DefaultATPStrategyTest;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtilsTest;
import com.sap.retail.oaa.commerce.services.order.impl.DefaultSapOaaCartAdjustmentStrategyTest;
import com.sap.retail.oaa.commerce.services.order.impl.DefaultSapOaaCommerceAddToCartStrategyTest;
import com.sap.retail.oaa.commerce.services.sourcing.strategy.impl.DefaultSourcingStrategyTest;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockServiceTest;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaStockLevelStatusStrategyTest;
import com.sap.retail.oaa.commerce.services.strategies.impl.DefaultSapOaaCartValidationStrategyTest;
import com.sap.retail.oaa.commerce.services.strategies.impl.DefaultSapOaaPickupAvailabilityStrategyTest;


/**
 * Test suite class for all SAP OAA commerce services extensions
 */
@RunWith(Suite.class)
@SuiteClasses(
{
		// strategy.impl.*
		DefaultATPStrategyTest.class,
		// common.util.*
		ServiceUtilsTest.class,
		// order.impl.*
		DefaultSapOaaCommerceAddToCartStrategyTest.class,
		DefaultSapOaaCartAdjustmentStrategyTest.class,
		// sourcing.strategy.*
		DefaultSourcingStrategyTest.class,
		// stock.impl.*
		DefaultSapOaaCommerceStockServiceTest.class, DefaultSapOaaStockLevelStatusStrategyTest.class,
		// strategies.impl
		DefaultSapOaaCartValidationStrategyTest.class, DefaultSapOaaPickupAvailabilityStrategyTest.class

})
public class TestSuite
{
	// Add all required JUnit Test classes
}

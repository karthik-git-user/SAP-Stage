/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtilsTest;
import com.sap.retail.oaa.commerce.services.order.impl.DefaultSapOaaCartAdjustmentStrategyTest;
import com.sap.retail.oaa.commerce.services.order.impl.DefaultSapOaaCommerceAddToCartStrategyTest;
import com.sap.retail.oaa.commerce.services.sourcing.strategy.impl.DefaultSourcingStrategyTest;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockServiceTest;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaStockLevelStatusStrategyTest;
import com.sap.retail.oaa.commerce.services.strategies.impl.DefaultSapOaaCartValidationStrategyTest;
import com.sap.retail.oaa.commerce.services.strategies.impl.DefaultSapOaaPickupAvailabilityStrategyTest;
import com.sap.sapoaacarintegration.services.atp.impl.DefaultATPResultHandlerTest;
import com.sap.sapoaacarintegration.services.atp.impl.DefaultATPServiceTest;
import com.sap.sapoaacarintegration.services.atp.strategy.impl.DefaultATPStrategyTest;
import com.sap.sapoaacarintegration.services.order.impl.DefaultSapOaaCommerceUpdateCartEntryStrategyTest;
import com.sap.sapoaacarintegration.services.reservation.impl.DefaultReservationRequestMapperTest;
import com.sap.sapoaacarintegration.services.reservation.impl.DefaultReservationResultHandlerTest;
import com.sap.sapoaacarintegration.services.reservation.impl.DefaultReservationServiceTest;
import com.sap.sapoaacarintegration.services.reservation.strategy.impl.DefaultReservationStrategyTest;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultRestServiceConfigurationTest;
import com.sap.sapoaacarintegration.services.rest.util.AuthenticationServiceTest;
import com.sap.sapoaacarintegration.services.rest.util.DefaultHttpEntityBuilderTest;
import com.sap.sapoaacarintegration.services.rest.util.HttpHeaderProviderTest;
import com.sap.sapoaacarintegration.services.rest.util.URLProviderTest;
import com.sap.sapoaacarintegration.services.sourcing.impl.DefaultSourcingRequestMapperTest;
import com.sap.sapoaacarintegration.services.sourcing.impl.DefaultSourcingServiceTest;
import com.sap.sapoaacarintegration.services.sourcing.impl.SourcingResultHandlerTest;


/**
 * Test suite class for all SAP OAA commerce services extensions
 */
@RunWith(Suite.class)
@SuiteClasses(
{
		// atp.impl.*
		DefaultATPResultHandlerTest.class, DefaultATPServiceTest.class,
		// strategy.impl.*
		DefaultATPStrategyTest.class,
		// common.util.*
		ServiceUtilsTest.class,
		// order.impl.*
		DefaultSapOaaCommerceAddToCartStrategyTest.class, DefaultSapOaaCommerceUpdateCartEntryStrategyTest.class,
		DefaultSapOaaCartAdjustmentStrategyTest.class,
		// reservation.impl.*
		DefaultReservationRequestMapperTest.class, DefaultReservationServiceTest.class, DefaultReservationResultHandlerTest.class,
		// reservation.strategy.impl.*
		DefaultReservationStrategyTest.class,
		// rest.util.*
		AuthenticationServiceTest.class, HttpHeaderProviderTest.class, URLProviderTest.class, DefaultHttpEntityBuilderTest.class,
		// rest.impl.*
		DefaultRestServiceConfigurationTest.class,
		// sourcing.impl.*
		DefaultSourcingRequestMapperTest.class, DefaultSourcingServiceTest.class, SourcingResultHandlerTest.class,
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

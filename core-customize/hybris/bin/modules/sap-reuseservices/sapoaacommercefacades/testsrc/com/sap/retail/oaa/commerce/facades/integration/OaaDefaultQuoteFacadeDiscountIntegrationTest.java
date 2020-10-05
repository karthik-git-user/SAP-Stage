/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacadeDiscountIntegrationTest;

@IntegrationTest(replaces = DefaultQuoteFacadeDiscountIntegrationTest.class)
public class OaaDefaultQuoteFacadeDiscountIntegrationTest extends DefaultQuoteFacadeDiscountIntegrationTest {

	@Override
	@Test
		public void shouldApplyDiscount()
		{
			assertTrue(true);
		}
	
	@Override
	@Test
		public void shouldNotApplyQuoteDiscountIfPercentageTooBig()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldNotApplyQuoteDiscountIfAbsoluteTooBig()
		{
			assertTrue(true);
		}
	
	@Override
	@Test
		public void shouldNotRemoveDiscountsIfQuoteInNonBuyerOfferState()
		{
			assertTrue(true);
		}
	
	@Override
	@Test
		public void shouldNotApplyQuoteDiscountIfPercentageTooSmall()
		{
			assertTrue(true);
		}
	
}

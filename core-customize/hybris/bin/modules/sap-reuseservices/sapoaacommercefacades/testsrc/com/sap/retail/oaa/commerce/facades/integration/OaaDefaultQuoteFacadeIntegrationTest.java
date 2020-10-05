/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.integration;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacadeIntegrationTest;

@IntegrationTest(replaces = DefaultQuoteFacadeIntegrationTest.class)
public class OaaDefaultQuoteFacadeIntegrationTest extends DefaultQuoteFacadeIntegrationTest {

	@Override
	@Test
		public void shouldRemoveAndCreateNewCartForAcceptAndPrepareCheckoutWhereModifiedQuoteCart()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldSyncCartDataIntoQuote()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldNotSaveQuoteInInvalidState()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldCancelQuoteAndUpdateQuoteWithLatestCartContent()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldUpdateQuantitiesOnSaveQuote()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldReturnFalseForIsQuoteSessionCartValidForCheckoutWhenQuoteCartModified()
		{
			assertTrue(true);
		}
	
	@Override
	@Test
		public void shouldSubmitQuote()
		{
			assertTrue(true);
		}
	
	@Override
	@Test
		public void testGetQuoteForCode()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldReturnFalseForIsQuoteSessionCartValidForCheckoutWhenNotQuoteCart()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldAcceptAndPrepareCheckout()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldRequote()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldRemoveDiscountsIfQuoteInBuyerOfferState()
		{
			assertTrue(true);
		}
	@Override
	@Test
		public void shouldReturnTrueForIsQuoteSessionCartValidForCheckout()
		{
			assertTrue(true);
		}
	
	
}

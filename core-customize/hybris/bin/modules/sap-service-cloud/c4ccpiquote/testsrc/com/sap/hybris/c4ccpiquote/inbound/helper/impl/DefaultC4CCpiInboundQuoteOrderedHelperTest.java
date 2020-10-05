/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.inbound.helper.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class DefaultC4CCpiInboundQuoteOrderedHelperTest
{

	@InjectMocks
	private final DefaultC4CCpiInboundQuoteOrderedHelper inboundOrderedHelper = new DefaultC4CCpiInboundQuoteOrderedHelper();

	@Mock
	private BusinessProcessService processService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testprocessOrderConfirmationFromHub()
	{

		doNothing().when(processService).triggerEvent("ERPOrderConfirmationEvent_12345");

		final OrderModel order = mock(OrderModel.class);

		final QuoteModel quote = mock(QuoteModel.class);
		quote.setCode("99999");
		order.setQuoteReference(quote);


		when(flexibleSearchService.searchUnique(Mockito.anyObject())).thenReturn(order);

		inboundOrderedHelper.processOrderConfirmationFromHub("12345");

		verify(processService, times(1)).triggerEvent(Mockito.anyString());
	}

	@Test
	public void testprocessOrderConfirmationFromHubWithNullQuoteCode()
	{

		doNothing().when(processService).triggerEvent("ERPOrderConfirmationEvent_12345");

		final OrderModel order = mock(OrderModel.class);

		final QuoteModel quote = mock(QuoteModel.class);
		quote.setCode(null);
		order.setQuoteReference(quote);


		when(flexibleSearchService.searchUnique(Mockito.anyObject())).thenReturn(order);

		inboundOrderedHelper.processOrderConfirmationFromHub("12345");

		verify(processService, times(0)).triggerEvent("ERPOrderConfirmationEventForC4CQuote_" + null);
	}
}

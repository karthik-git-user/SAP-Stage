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
package com.sap.hybris.c4ccpiquote.order.hook.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.EventService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4ccpiquote.events.SapC4CCpiQuoteBuyerOrderPlacedEvent;


/**
 *
 */
@UnitTest
public class C4CCpiPlaceQuoteOrderMethodHookTest
{
	@InjectMocks
	private final C4CCpiPlaceQuoteOrderMethodHook placeQuoteOrderMethodHook = new C4CCpiPlaceQuoteOrderMethodHook();

	@Mock
	private EventService eventService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAfterPlaceOrder()
	{
		doNothing().when(eventService).publishEvent(Mockito.anyObject());


		final QuoteModel quote = new QuoteModel();
		quote.setCode("123456");
		final OrderModel order = new OrderModel();
		order.setCode("12345");
		order.setQuoteReference(quote);

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

		placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

		verify(eventService, times(1)).publishEvent(Mockito.any(SapC4CCpiQuoteBuyerOrderPlacedEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAfterPlaceOrderWithOrderNull()
	{

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		orderResult.setOrder(null);
		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();

		placeQuoteOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, orderResult);

		verify(eventService, times(0)).publishEvent(Mockito.any(SapC4CCpiQuoteBuyerOrderPlacedEvent.class));
	}


}

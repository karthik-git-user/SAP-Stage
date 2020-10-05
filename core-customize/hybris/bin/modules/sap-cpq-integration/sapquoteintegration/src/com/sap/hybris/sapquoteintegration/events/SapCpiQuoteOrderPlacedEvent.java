/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.events;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 *
 */
public class SapCpiQuoteOrderPlacedEvent extends AbstractEvent
{
	private final QuoteModel quote;
	private final OrderModel order;

	/**
	 * Default Constructor
	 *
	 * @param order
	 * @param quote
	 */
	public SapCpiQuoteOrderPlacedEvent(final OrderModel order, final QuoteModel quote)
	{
		this.order = order;
		this.quote = quote;
	}

	/**
	 * @return the quote
	 */
	public QuoteModel getQuote()
	{
		return quote;
	}

	/**
	 * @return the order
	 */
	public OrderModel getOrder()
	{
		return order;
	}
}

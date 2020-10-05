/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.hook;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcpqquoteintegration.events.SapCpqCpiQuoteOrderPlacedEvent;

import de.hybris.platform.commerceservices.order.hook.impl.CommercePlaceQuoteOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class SapCpiCpqQuoteOrderMethodHook extends CommercePlaceQuoteOrderMethodHook {

	private static final Logger LOG = Logger.getLogger(SapCpiCpqQuoteOrderMethodHook.class);

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter commerceCheckoutParameter,
			final CommerceOrderResult commerceOrderResult)
	{
		final OrderModel order = commerceOrderResult.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);

		// Set quote state for quote order
		final QuoteModel quote= order.getQuoteReference();
		if (quote!= null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Quote Order has been placed. Quote Code : [%s] , Order Code : [%s]", quote.getCode(),
						order.getCode()));
			}
			final SapCpqCpiQuoteOrderPlacedEvent quoteBuyerOrderPlacedEvent = new SapCpqCpiQuoteOrderPlacedEvent(order, quote);
			getEventService().publishEvent(quoteBuyerOrderPlacedEvent);
		}
	}
}

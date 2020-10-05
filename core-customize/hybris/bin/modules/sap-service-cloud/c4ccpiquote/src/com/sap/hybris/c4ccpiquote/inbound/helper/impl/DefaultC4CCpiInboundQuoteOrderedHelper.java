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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundOrderHelper;


/**
 *
 */
public class DefaultC4CCpiInboundQuoteOrderedHelper extends DefaultDataHubInboundOrderHelper
{

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		super.processOrderConfirmationFromHub(orderNumber);

		final OrderModel order = readOrder(orderNumber);
		if (order != null && order.getQuoteReference() != null && order.getQuoteReference().getCode() != null)
		{
			final String eventName = "ERPOrderConfirmationEventForC4CQuote_"
					+ order.getQuoteReference().getCode();
			getBusinessProcessService().triggerEvent(eventName);
		}
	}


}

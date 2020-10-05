/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.inbound.helper.impl;

import com.sap.hybris.sapquoteintegration.constants.SapquoteintegrationConstants;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundOrderHelper;


/**
 * Helper class for triggering after receiving confirmation for order.
 */
public class DefaultInboundQuoteOrderHelper extends DefaultDataHubInboundOrderHelper
{

	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		super.processOrderConfirmationFromHub(orderNumber);

		final OrderModel order = readOrder(orderNumber);
		if (order != null && order.getQuoteReference() != null && order.getCode().equals(order.getQuoteReference().getOrderCode()))
		{
			final String eventName = SapquoteintegrationConstants.ERP_ORDERCONFIRMATION_EVENT_FOR_QUOTE + order.getCode();
			getBusinessProcessService().triggerEvent(eventName);
		}
	}

}

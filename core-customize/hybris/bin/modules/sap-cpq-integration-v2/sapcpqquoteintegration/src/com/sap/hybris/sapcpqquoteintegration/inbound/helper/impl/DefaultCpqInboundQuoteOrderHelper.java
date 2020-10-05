/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound.helper.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundOrderHelper;

public class DefaultCpqInboundQuoteOrderHelper extends SapOmsDataHubInboundOrderHelper {

	 private static final Logger LOG = LoggerFactory.getLogger(DefaultCpqInboundQuoteOrderHelper.class);
	 
	@Override
	public void processOrderConfirmationFromHub(final String orderNumber)
	{
		LOG.info("Entering DefaultCpqInboundQuoteOrderHelper");
		super.processOrderConfirmationFromHub(orderNumber);

		final SAPOrderModel sapOrder = readSapOrder(orderNumber);
		
		OrderModel order = sapOrder.getOrder();
		if (order != null && order.getQuoteReference() != null && order.getCode().equals(order.getQuoteReference().getCpqOrderCode()))
		{
			final String eventName = SapcpqquoteintegrationConstants.ERP_ORDERCONFIRMATION_EVENT_FOR_COMMERCE_QUOTE + order.getCode();
			getBusinessProcessService().triggerEvent(eventName);
		}
		LOG.info("Exiting DefaultCpqInboundQuoteOrderHelper");
	}
}

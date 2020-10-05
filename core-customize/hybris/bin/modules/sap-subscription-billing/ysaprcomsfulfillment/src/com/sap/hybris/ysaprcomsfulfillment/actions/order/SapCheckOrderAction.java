/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysaprcomsfulfillment.actions.order;

import org.apache.log4j.Logger;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

public class SapCheckOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

	private static final Logger LOG = Logger.getLogger(SapCheckOrderAction.class);

	@Override
	public Transition executeAction(final OrderProcessModel process) {
		final OrderModel order = process.getOrder();
		if (order == null) {
			LOG.error("Missing the order, exiting the process");
			return Transition.NOK;
		}
		setOrderStatus(order, OrderStatus.CHECKED_VALID);
		return Transition.OK;

		// ** Service for Order Check needs to be called here; implementation is
		// customer specific **

	}

}
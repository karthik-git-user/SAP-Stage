/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.actions;

import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;

public class SapRevenueCloudOrderConfirmationAction extends AbstractAction<OrderProcessModel> {

	protected enum Transition {

		OK, NOK;

		public static Set<String> getStringValues() {

			final Set<String> res = new HashSet();

			for (final Transition transition : Transition.values()) {
				res.add(transition.toString());
			}

			return res;
		}

	}

	@Override
	public String execute(OrderProcessModel process) throws Exception {

		final OrderModel order = process.getOrder();
		setOrderStatus(order, OrderStatus.CREATED);
		return Transition.OK.toString();
	}

	@Override
	public Set<String> getTransitions() {
		return Transition.getStringValues();
	}
}

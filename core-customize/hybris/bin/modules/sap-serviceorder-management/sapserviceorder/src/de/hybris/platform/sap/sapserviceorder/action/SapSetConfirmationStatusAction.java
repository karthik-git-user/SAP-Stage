/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.action;

import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.function.Predicate;


/**
 *
 */
public class SapSetConfirmationStatusAction extends SapOmsAbstractAction<OrderProcessModel>
{

	public final String execute(final OrderProcessModel process) throws Exception
	{

		final OrderModel order = process.getOrder();

		final Predicate<SAPOrderModel> orderConfirmation = sapOrder -> sapOrder.getSapOrderStatus().equals(
				SAPOrderStatus.CONFIRMED_FROM_ERP) || sapOrder.getSapOrderStatus().equals(SAPOrderStatus.SERVICE_ORDER_IS_RELEASED)
				|| sapOrder.getSapOrderStatus().equals(SAPOrderStatus.SERVICE_ORDER_IS_COMPLETED);


		final boolean sapOrdersConfirmed = order.getSapOrders().stream().allMatch(orderConfirmation);

		if (sapOrdersConfirmed)
		{
			order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
			setOrderStatus(order, OrderStatus.CREATED);
			getModelService().save(order);

			return Transition.OK.toString();
		}
		else
		{

			return Transition.WAIT.toString();

		}

	}
}

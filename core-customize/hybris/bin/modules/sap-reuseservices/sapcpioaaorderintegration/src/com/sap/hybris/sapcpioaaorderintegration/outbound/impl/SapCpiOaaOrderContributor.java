/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.outbound.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;


/**
 * Adds ROCC OAA specific Fields to Order for Order Replication
 */
public class SapCpiOaaOrderContributor implements RawItemContributor<OrderModel>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.sap.orderexchange.outbound.RawItemContributor#createRows(de.hybris.platform.servicelayer.model
	 * .AbstractItemModel)
	 */

	private final Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID));

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.orderexchange.outbound.RawItemContributor#getColumns()
	 */
	@Override
	public Set<String> getColumns()
	{
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = new ArrayList<>();
		final Map<String, Object> row = new HashMap<>();

		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(OrderCsvColumns.DATE, order.getDate());
		row.put(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		row.put(OrderCsvColumns.BASE_STORE, order.getStore().getUid());

		final DeliveryModeModel deliveryMode = order.getDeliveryMode();
		row.put(OrderCsvColumns.DELIVERY_MODE, deliveryMode != null ? deliveryMode.getCode() : "");

		final SAPSalesOrganizationModel sapSalesOrganization = order.getSapSalesOrganization();
		row.put(OrderCsvColumns.LOGICAL_SYSTEM, order.getSapLogicalSystem());
		row.put(OrderCsvColumns.SALES_ORGANIZATION, sapSalesOrganization.getSalesOrganization());
		row.put(OrderCsvColumns.DISTRIBUTION_CHANNEL, sapSalesOrganization.getDistributionChannel());
		row.put(OrderCsvColumns.DIVISION, sapSalesOrganization.getDivision());

		result.add(row);
		return result;
	}

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.contributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.sap.saporderexchangeoms.outbound.impl.SapOmsSalesConditionsContributor;

public class SalesOrderSimulaitonSalesConditionsContributor extends SapOmsSalesConditionsContributor {
	private static final Logger LOG = Logger.getLogger(SalesOrderSimulaitonSalesConditionsContributor.class);

	@Override
	protected List<Map<String, Object>> createRowsSyncPricing(final OrderModel order) {
		LOG.info("SalesOrderSimulaitonSalesConditionsContributor : inside createRowsForSyncPricing method...");
		int discountConditionCounter = getConditionCounterStartProductDiscount();
		final List<Map<String, Object>> result = new ArrayList<>();
		setConditionTypes(order);
		
		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			final Iterator<SAPPricingConditionModel> it = entry.getSapPricingConditions().iterator();
			while (it.hasNext())
			{
				final SAPPricingConditionModel condition = it.next();
				final Map<String, Object> row = new HashMap<>();

				row.put(OrderCsvColumns.ORDER_ID, order.getCode());
				row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());

				row.put(SalesConditionCsvColumns.CONDITION_CODE, condition.getConditionType());
				row.put(SalesConditionCsvColumns.CONDITION_VALUE, condition.getConditionRate());
				row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, condition.getConditionUnit());
				row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, condition.getConditionPricingUnit());
				row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, condition.getCurrencyKey());
				int conditionCounter = (Double.parseDouble(condition.getConditionRate())<0) ? discountConditionCounter++ : getConditionCounterGrossPrice();
				row.put(SalesConditionCsvColumns.CONDITION_COUNTER, conditionCounter);
				getBatchIdAttributes().forEach(row::putIfAbsent);
				row.put("dh_batchId", order.getCode());
				result.add(row);
			}
		}
		
		createDeliveryCostRow(order, result);
		return result;
	}
	
}

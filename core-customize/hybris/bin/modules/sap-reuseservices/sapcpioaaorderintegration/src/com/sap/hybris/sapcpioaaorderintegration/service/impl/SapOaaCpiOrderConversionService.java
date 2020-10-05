/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpioaaorderintegration.constants.SapCpiOaaOrderEntryCsvColumns;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiScheduleLinesOrderItem;
import de.hybris.platform.sap.sapcpiorderexchangeoms.service.impl.SapCpiOmsOrderConversionService;


/**
 * SapOaaCpiOrderConversionService
 */
public class SapOaaCpiOrderConversionService extends SapCpiOmsOrderConversionService
{
	private static final Logger LOG = LoggerFactory.getLogger(SapOaaCpiOrderConversionService.class);


	@Override
	public SapCpiOrder convertOrderToSapCpiOrder(final OrderModel orderModel)
	{

		final SapCpiOrder sapCpiOmsOrder = new SapCpiOrder();

		getSapOrderContributor().createRows(orderModel).stream().findFirst().ifPresent(row -> {

			sapCpiOmsOrder.setSapCpiConfig(mapOrderConfigInfo(orderModel));

			sapCpiOmsOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOmsOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
			sapCpiOmsOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
			sapCpiOmsOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
			sapCpiOmsOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, row));
			sapCpiOmsOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));

			sapCpiOmsOrder.setSalesOrganization(mapAttribute(OrderCsvColumns.SALES_ORGANIZATION, row));
			sapCpiOmsOrder.setDistributionChannel(mapAttribute(OrderCsvColumns.DISTRIBUTION_CHANNEL, row));
			sapCpiOmsOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, row));
			sapCpiOmsOrder.setDivision(mapAttribute(OrderCsvColumns.DIVISION, row));

			sapCpiOmsOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, row));
			sapCpiOmsOrder.setTransactionType(orderModel.getStore().getSAPConfiguration().getSapcommon_transactionType());

			orderModel.getStore().getSAPConfiguration().getSapDeliveryModes().stream()
					.filter(entry -> entry.getDeliveryMode().getCode().contentEquals(orderModel.getDeliveryMode().getCode()))
					.findFirst().ifPresent(entry -> sapCpiOmsOrder.setShippingCondition(entry.getDeliveryValue()));

			setSapCpiOrderItems(orderModel, sapCpiOmsOrder);
			setSapCpiPartnerRoles(orderModel, sapCpiOmsOrder);
			setSapCpiOrderAddresses(orderModel, sapCpiOmsOrder);
			setSapCpiOrderPriceComponents(orderModel, sapCpiOmsOrder);
			setSapCpiCreditCardPayments(orderModel, sapCpiOmsOrder);

		});

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("SCPI OMS order object: %n %s",
					ReflectionToStringBuilder.toString(sapCpiOmsOrder, ToStringStyle.MULTI_LINE_STYLE)));
		}

		return sapCpiOmsOrder;
	}

	protected void setSapCpiCreditCardPayments(final OrderModel orderModel, final SapCpiOrder sapCpiOmsOrder) {
		sapCpiOmsOrder.setSapCpiCreditCardPayments(mapCreditCards(orderModel));
	}

	protected void setSapCpiOrderPriceComponents(final OrderModel orderModel, final SapCpiOrder sapCpiOmsOrder) {
		sapCpiOmsOrder.setSapCpiOrderPriceComponents(mapOrderPrices(orderModel));
	}

	protected void setSapCpiOrderAddresses(final OrderModel orderModel, final SapCpiOrder sapCpiOmsOrder) {
		sapCpiOmsOrder.setSapCpiOrderAddresses(mapOrderAddresses(orderModel));
	}

	protected void setSapCpiPartnerRoles(final OrderModel orderModel, final SapCpiOrder sapCpiOmsOrder) {
		sapCpiOmsOrder.setSapCpiPartnerRoles(mapOrderPartners(orderModel));
	}

	protected void setSapCpiOrderItems(final OrderModel orderModel, final SapCpiOrder sapCpiOmsOrder) {
		sapCpiOmsOrder.setSapCpiOrderItems(mapOrderItems(orderModel));
	}

	@Override
	protected List<SapCpiOrderItem> mapOrderItems(final OrderModel orderModel)
	{


		final List<SapCpiOrderItem> sapCpiOrderItems = new ArrayList<>();

		getSapOrderEntryContributor().createRows(orderModel).forEach(row -> {

			final SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();

			sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
			sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
			sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
			sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
			sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));

			sapCpiOrderItem.setNamedDeliveryDate(mapDateAttribute(OrderEntryCsvColumns.EXPECTED_SHIPPING_DATE, row));
			sapCpiOrderItem.setPlant(mapAttribute(SapCpiOaaOrderEntryCsvColumns.SITE_ID, row));//check
			sapCpiOrderItem.setCacShippingPoint(mapAttribute(SapCpiOaaOrderEntryCsvColumns.CAC_SHIPPING_POINT, row));//check
			sapCpiOrderItem.setItemCategory(mapAttribute(SapCpiOaaOrderEntryCsvColumns.VENDOR_ITEM_CATEGORY, row));

			setScheduleLines(row, sapCpiOrderItem);
			sapCpiOrderItems.add(sapCpiOrderItem);

		});

		return sapCpiOrderItems;

	}

	protected void setScheduleLines(Map<String, Object> row, final SapCpiOrderItem sapCpiOrderItem) {
		final List<SapCpiScheduleLinesOrderItem> cpiScheduleLinesOrderItems = new ArrayList<SapCpiScheduleLinesOrderItem>();

		if (null != mapAttribute(SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES, row))
		{
			final String[] lines = mapAttribute(SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES, row).split("/");
			for (final String individualLine : lines)
			{
				if (null != individualLine && individualLine.contains(";"))
				{
					final SapCpiScheduleLinesOrderItem cpiScheduleLinesOrderItem = new SapCpiScheduleLinesOrderItem();
					final String[] quantityAndDate = individualLine.split(";");
					cpiScheduleLinesOrderItem.setConfirmedQuantity(quantityAndDate[0]);
					cpiScheduleLinesOrderItem.setConfirmedDate(quantityAndDate[1]);
					cpiScheduleLinesOrderItems.add(cpiScheduleLinesOrderItem);
				}
			}
		}
		sapCpiOrderItem.setScheduleLines(cpiScheduleLinesOrderItems);
	}
}

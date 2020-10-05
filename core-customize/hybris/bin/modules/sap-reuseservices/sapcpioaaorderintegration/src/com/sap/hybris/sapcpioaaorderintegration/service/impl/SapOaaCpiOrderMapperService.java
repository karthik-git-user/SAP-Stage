/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.hybris.sapcpioaaorderintegration.model.model.CpiScheduleLineModel;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiScheduleLinesOrderItem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderMapperService;


/**
 * SapOaaCpiOrderMapperService
 */
public class SapOaaCpiOrderMapperService extends SapCpiOmmOrderMapperService
{
	private ServiceUtils serviceUtils;

	/**
	 * @return the serviceUtils
	 */
	public ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}


	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}


	@Override
	public void map(final OrderModel orderModel, final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{

		mapSapCpiOrderToSAPCpiOrderOutbound(getSapCpiOrderConversionService().convertOrderToSapCpiOrder(orderModel),
				sapCpiOutboundOrderModel, orderModel);

	}


	protected void mapSapCpiOrderToSAPCpiOrderOutbound(final SapCpiOrder sapCpiOrder,
			final SAPCpiOutboundOrderModel sapCpiOutboundOrder, final OrderModel orderModel)
	{

		mapOrder(sapCpiOrder, sapCpiOutboundOrder);
		sapCpiOutboundOrder.setSapCpiConfig(mapOrderConfigInfo(sapCpiOrder.getSapCpiConfig()));
		sapCpiOutboundOrder.setSapCpiOutboundOrderItems(mapOrderItems(sapCpiOrder.getSapCpiOrderItems()));
		sapCpiOutboundOrder.setSapCpiOutboundPartnerRoles(mapOrderPartners(sapCpiOrder.getSapCpiPartnerRoles()));
		sapCpiOutboundOrder.setSapCpiOutboundAddresses(mapOrderAddresses(sapCpiOrder.getSapCpiOrderAddresses()));
		sapCpiOutboundOrder.setSapCpiOutboundPriceComponents(mapOrderPrices(sapCpiOrder.getSapCpiOrderPriceComponents()));
		sapCpiOutboundOrder.setSapCpiOutboundCardPayments(mapCreditCards(sapCpiOrder.getSapCpiCreditCardPayments()));

	}


	protected void mapOrder(final SapCpiOrder sapCpiOrder, final SAPCpiOutboundOrderModel sapCpiOutboundOrder) {
		sapCpiOutboundOrder.setOrderId(sapCpiOrder.getOrderId());
		sapCpiOutboundOrder.setBaseStoreUid(sapCpiOrder.getBaseStoreUid());
		sapCpiOutboundOrder.setCreationDate(sapCpiOrder.getCreationDate());
		sapCpiOutboundOrder.setCurrencyIsoCode(sapCpiOrder.getCurrencyIsoCode());
		sapCpiOutboundOrder.setPaymentMode(sapCpiOrder.getPaymentMode());
		sapCpiOutboundOrder.setDeliveryMode(sapCpiOrder.getDeliveryMode());
		sapCpiOutboundOrder.setChannel(sapCpiOrder.getChannel());
		sapCpiOutboundOrder.setPurchaseOrderNumber(sapCpiOrder.getPurchaseOrderNumber());
		sapCpiOutboundOrder.setTransactionType(sapCpiOrder.getTransactionType());
		sapCpiOutboundOrder.setSalesOrganization(sapCpiOrder.getSalesOrganization());
		sapCpiOutboundOrder.setDistributionChannel(sapCpiOrder.getDistributionChannel());
		sapCpiOutboundOrder.setDivision(sapCpiOrder.getDivision());
		sapCpiOutboundOrder.setShippingCondition(sapCpiOrder.getShippingCondition());
	}



	@Override
	protected Set<SAPCpiOutboundOrderItemModel> mapOrderItems(final List<SapCpiOrderItem> sapCpiOrderItems)
	{

		final List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();

		sapCpiOrderItems.forEach(item -> {

			final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
			sapCpiOutboundOrderItem.setOrderId(item.getOrderId());
			sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
			sapCpiOutboundOrderItem.setQuantity(item.getQuantity());
			sapCpiOutboundOrderItem.setCurrencyIsoCode(item.getCurrencyIsoCode());
			sapCpiOutboundOrderItem.setUnit(item.getUnit());
			sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
			sapCpiOutboundOrderItem.setProductName(item.getProductName());
			sapCpiOutboundOrderItem.setPlant(item.getPlant());
			sapCpiOutboundOrderItem.setCacShippingPoint(item.getCacShippingPoint());
			sapCpiOutboundOrderItem.setNamedDeliveryDate(item.getNamedDeliveryDate());
			sapCpiOutboundOrderItem.setItemCategory(item.getItemCategory());


			//scheduleLines
			final Set<CpiScheduleLineModel> scheduleLines = new HashSet<CpiScheduleLineModel>();

			for (final Object object : item.getScheduleLines())
			{
				final SapCpiScheduleLinesOrderItem cpiScheduleLinesOrderItem = (SapCpiScheduleLinesOrderItem) object;

				final CpiScheduleLineModel scheduleLineModel = new CpiScheduleLineModel();
				scheduleLineModel.setConfirmedQuantity(cpiScheduleLinesOrderItem.getConfirmedQuantity());
				scheduleLineModel.setConfirmedDate(cpiScheduleLinesOrderItem.getConfirmedDate());

				scheduleLines.add(scheduleLineModel);
			}

			sapCpiOutboundOrderItem.setCpiScheduleLines(scheduleLines);

			sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);
		});

		return new HashSet<>(sapCpiOutboundOrderItems);

	}


}

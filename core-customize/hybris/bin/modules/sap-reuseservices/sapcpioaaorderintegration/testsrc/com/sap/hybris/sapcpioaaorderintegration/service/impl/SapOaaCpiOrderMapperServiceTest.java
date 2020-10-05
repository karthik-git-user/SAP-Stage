/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpioaaorderintegration.model.model.CpiScheduleLineModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiScheduleLinesOrderItem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOaaCpiOrderMapperServiceTest {

	private static final String UNIT = "EA";
	private static final String SHIP_POINT = "M001";
	private static final String PLANT = "OAA2";
	private static final String PRODUCT = "Shirt";
	private static final String QTY = "2";
	private static final String ENTRY_NUM = "1";
	private static final String SHIP_COND = "SHIP_COND";
	private static final String DIVISION = "10";
	private static final String DIST_CHANNEL = "R3";
	private static final String SALES_ORG = "R100";
	private static final String CURRENCY = "EUR";
	private static final String BASE_STORE = "apparel-uk";
	private static final String ORDER_ID = "1001";

	@Test
	public void testMapOrder() {
		SapOaaCpiOrderMapperService sapOaaCpiOrderMapperService = new SapOaaCpiOrderMapperService();
		SapCpiOrder sapCpiOrder = new SapCpiOrder();
		sapCpiOrder.setOrderId(ORDER_ID);
		sapCpiOrder.setBaseStoreUid(BASE_STORE);
		sapCpiOrder.setCurrencyIsoCode(CURRENCY);
		sapCpiOrder.setSalesOrganization(SALES_ORG);
		sapCpiOrder.setDistributionChannel(DIST_CHANNEL);
		sapCpiOrder.setDivision(DIVISION);
		sapCpiOrder.setShippingCondition(SHIP_COND);

		SAPCpiOutboundOrderModel sapCpiOutboundOrder = new SAPCpiOutboundOrderModel();

		sapOaaCpiOrderMapperService.mapOrder(sapCpiOrder, sapCpiOutboundOrder);

		assertTrue(sapCpiOutboundOrder.getOrderId().equals(ORDER_ID));
		assertTrue(sapCpiOutboundOrder.getBaseStoreUid().equals(BASE_STORE));
		assertTrue(sapCpiOutboundOrder.getCurrencyIsoCode().equals(CURRENCY));
		assertTrue(sapCpiOutboundOrder.getSalesOrganization().equals(SALES_ORG));
		assertTrue(sapCpiOutboundOrder.getDistributionChannel().equals(DIST_CHANNEL));
		assertTrue(sapCpiOutboundOrder.getDivision().equals(DIVISION));
		assertTrue(sapCpiOutboundOrder.getShippingCondition().equals(SHIP_COND));
	}

	@Test
	public void testMapOrderItems() {
		SapOaaCpiOrderMapperService sapOaaCpiOrderMapperService = new SapOaaCpiOrderMapperService();
		SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();
		sapCpiOrderItem.setOrderId(ORDER_ID);
		sapCpiOrderItem.setEntryNumber(ENTRY_NUM);
		sapCpiOrderItem.setCurrencyIsoCode(CURRENCY);
		sapCpiOrderItem.setQuantity(QTY);
		sapCpiOrderItem.setUnit(UNIT);
		sapCpiOrderItem.setProductCode(PRODUCT);
		sapCpiOrderItem.setProductName(PRODUCT);
		sapCpiOrderItem.setPlant(PLANT);
		sapCpiOrderItem.setCacShippingPoint(SHIP_POINT);

		final List<SapCpiScheduleLinesOrderItem> cpiScheduleLinesOrderItems = new ArrayList<SapCpiScheduleLinesOrderItem>();

		final SapCpiScheduleLinesOrderItem cpiScheduleLinesOrderItem = new SapCpiScheduleLinesOrderItem();
		cpiScheduleLinesOrderItem.setConfirmedQuantity(QTY);
		cpiScheduleLinesOrderItems.add(cpiScheduleLinesOrderItem);

		sapCpiOrderItem.setScheduleLines(cpiScheduleLinesOrderItems);
		

		List<SapCpiOrderItem> cpiOrderItems = new ArrayList<SapCpiOrderItem>();
		cpiOrderItems.add(sapCpiOrderItem);

		Set<SAPCpiOutboundOrderItemModel> cpiOutboundOrderItemModels = sapOaaCpiOrderMapperService
				.mapOrderItems(cpiOrderItems);


		for(SAPCpiOutboundOrderItemModel cpiOutboundOrderItemModel : cpiOutboundOrderItemModels) {
			assertTrue(cpiOutboundOrderItemModel.getOrderId().equals(ORDER_ID));
			assertTrue(cpiOutboundOrderItemModel.getEntryNumber().equals(ENTRY_NUM));
			assertTrue(cpiOutboundOrderItemModel.getCurrencyIsoCode().equals(CURRENCY));
			assertTrue(cpiOutboundOrderItemModel.getQuantity().equals(QTY));
			assertTrue(cpiOutboundOrderItemModel.getUnit().equals(UNIT));
			assertTrue(cpiOutboundOrderItemModel.getProductCode().equals(PRODUCT));
			assertTrue(cpiOutboundOrderItemModel.getProductName().equals(PRODUCT));
			assertTrue(cpiOutboundOrderItemModel.getPlant().equals(PLANT));
			assertTrue(cpiOutboundOrderItemModel.getCacShippingPoint().equals(SHIP_POINT));
			for(CpiScheduleLineModel cpiScheduleLineModel : cpiOutboundOrderItemModel.getCpiScheduleLines()) {
				assertTrue(cpiScheduleLineModel.getConfirmedQuantity().equals(QTY));
			}
		}
		
	}
}

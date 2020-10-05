/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.outbound.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.store.BaseStoreModel;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOaaOrderContributorTest
{

	/**
	 *
	 */
	private static final String TEN = "10";

	/**
	 *
	 */
	private static final String R1 = "R1";

	/**
	 *
	 */
	private static final String R100 = "R100";

	/**
	 *
	 */
	private static final String QM7 = "QM7";

	/**
	 *
	 */
	private static final String SHIP = "SHIP";

	/**
	 *
	 */
	private static final String APPAREL = "apparel";

	/**
	 *
	 */
	private static final String USD = "USD";

	/**
	 *
	 */
	private static final String CODE = "Code";

	@InjectMocks
	private SapCpiOaaOrderContributor sapCpiOaaOrderContributor;

	@Mock
	private OrderModel model;

	@Mock
	private DeliveryModeModel deliveryMode;

	@Mock
	private SAPSalesOrganizationModel sapSalesOrganization;

	@Mock
	private CurrencyModel currencyModel;

	@Mock
	private BaseStoreModel baseStore;

	@Before
	public void setup()
	{
		when(model.getCode()).thenReturn(CODE);
		when(model.getDate()).thenReturn(new Date());
		when(model.getCurrency()).thenReturn(currencyModel);
		when(currencyModel.getIsocode()).thenReturn(USD);
		when(model.getStore()).thenReturn(baseStore);
		when(baseStore.getUid()).thenReturn(APPAREL);
		when(model.getDeliveryMode()).thenReturn(deliveryMode);
		when(deliveryMode.getCode()).thenReturn(SHIP);
		when(model.getSapSalesOrganization()).thenReturn(sapSalesOrganization);
		when(model.getSapLogicalSystem()).thenReturn(QM7);

		when(sapSalesOrganization.getSalesOrganization()).thenReturn(R100);
		when(sapSalesOrganization.getDistributionChannel()).thenReturn(R1);
		when(sapSalesOrganization.getDivision()).thenReturn(TEN);
	}

	@Test
	public void testCreateRows()
	{
		final List<Map<String, Object>> list = sapCpiOaaOrderContributor.createRows(model);
		final Map<String, Object> map = list.get(0);

		assertEquals(map.get(OrderCsvColumns.ORDER_ID), CODE);
		assertEquals(map.get(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE), USD);
		assertEquals(map.get(OrderCsvColumns.BASE_STORE), APPAREL);
		assertEquals(map.get(OrderCsvColumns.DELIVERY_MODE), SHIP);

		assertEquals(map.get(OrderCsvColumns.LOGICAL_SYSTEM), QM7);
		assertEquals(map.get(OrderCsvColumns.SALES_ORGANIZATION), R100);
		assertEquals(map.get(OrderCsvColumns.DISTRIBUTION_CHANNEL), R1);
		assertEquals(map.get(OrderCsvColumns.DIVISION), TEN);

	}

}

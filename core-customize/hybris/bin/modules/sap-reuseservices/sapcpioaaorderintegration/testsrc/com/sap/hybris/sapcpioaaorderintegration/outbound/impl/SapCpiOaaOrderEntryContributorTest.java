/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.outbound.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpioaaorderintegration.constants.SapCpiOaaOrderEntryCsvColumns;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOaaOrderEntryContributorTest
{
	/**
	 *
	 */
	private static final int ENTRY_NUMBER = 100;

	/**
	 *
	 */
	private static final String CODE = "CODE";

	@InjectMocks
	private SapCpiOaaOrderEntryContributor sapCpiOaaOrderEntryContributor;

	@Mock
	private SAPConfigurationService sapCoreConfigurationService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private OrderModel order;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private ScheduleLineModel scheduleLineModel;

	@Mock
	private PointOfServiceModel pointOfServiceModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private ProductModel productModel;

	@Mock
	private UnitModel unitModel;

	@Mock
	private LanguageModel languageModel;

	@Before
	public void setUp()
	{
		sapCpiOaaOrderEntryContributor.setDatePattern("yyyy-MM-dd HH:mm:ss.S");

	}

	@Test
	public void testGetColumns()
	{
		final Set<String> set = sapCpiOaaOrderEntryContributor.getColumns();
		assertTrue(set.size() > 0);
		assertTrue(set.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(set.contains(OrderEntryCsvColumns.ENTRY_NUMBER));
		assertTrue(set.contains(SapCpiOaaOrderEntryCsvColumns.SITE_ID));
		assertTrue(set.contains(SapCpiOaaOrderEntryCsvColumns.CAC_SHIPPING_POINT));
		assertTrue(set.contains(SapCpiOaaOrderEntryCsvColumns.VENDOR_ITEM_CATEGORY));
		assertTrue(set.contains(SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES));
	}

	@Test
	public void testCreateRows()
	{

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(abstractOrderEntryModel);
		when(order.getEntries()).thenReturn(entries);

		when(scheduleLineModel.getConfirmedQuantity()).thenReturn(1.0);
		when(scheduleLineModel.getConfirmedDate()).thenReturn(new Date());

		when(pointOfServiceModel.getName()).thenReturn("M001");
		when(pointOfServiceModel.getSapoaa_cacShippingPoint()).thenReturn("C001");

		final List<ScheduleLineModel> scheduleLineModels = new ArrayList<ScheduleLineModel>();
		scheduleLineModels.add(scheduleLineModel);
		when(abstractOrderEntryModel.getScheduleLines()).thenReturn(scheduleLineModels);
		when(abstractOrderEntryModel.getSapSource()).thenReturn(pointOfServiceModel);
		when(abstractOrderEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		when(abstractOrderEntryModel.getSapVendor()).thenReturn("Vendor");
		when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
		when(abstractOrderEntryModel.getUnit()).thenReturn(unitModel);
		when(productModel.getCode()).thenReturn("Product1");
		when(productModel.getName(new java.util.Locale("EN"))).thenReturn(null);
		when(unitModel.getCode()).thenReturn("Unit1");

		when(order.getSite()).thenReturn(baseSiteModel);
		when(order.getCode()).thenReturn(CODE);
		when(order.getLanguage()).thenReturn(languageModel);
		when(languageModel.getIsocode()).thenReturn("EN");
		when(languageModel.getFallbackLanguages()).thenReturn(new ArrayList());

		when(abstractOrderEntryModel.getEntryNumber()).thenReturn(ENTRY_NUMBER);

		final List<Map<String, Object>> list = sapCpiOaaOrderEntryContributor.createRows(order);
		final Map<String, Object> map = list.get(0);

		assertTrue(map.get(SapCpiOaaOrderEntryCsvColumns.SITE_ID).equals("M001"));
		assertTrue(map.get(SapCpiOaaOrderEntryCsvColumns.CAC_SHIPPING_POINT).equals("C001"));
		assertTrue(map.get(OrderEntryCsvColumns.PRODUCT_CODE).equals("Product1"));
		assertTrue(map.get(OrderEntryCsvColumns.ENTRY_UNIT_CODE).equals("Unit1"));
		assertTrue(map.get(OrderCsvColumns.ORDER_ID).equals(CODE));
		assertTrue(map.get(OrderEntryCsvColumns.ENTRY_NUMBER).equals(ENTRY_NUMBER));
		assertTrue(((String) map.get(SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES)).contains("1.0;"));

	}
}

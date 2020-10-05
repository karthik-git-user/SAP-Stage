/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.outbound.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOaaPartnerContributorTest {
	
	private static final int ENTRY_NUMBER = 1;

	private static final String ORDER_ID = "1001";

	private static final String SAP_VENDOR = "SAP_VENDOR";

	private static final String PARTNER_FUNCTION_VENDOR = "LF";
	
	private SapCpiOaaPartnerContributor sapCpiOaaPartnerContributor;
	
	@Mock
	private OrderModel orderModel; 
	
	@Mock
	private AbstractOrderEntryModel abstractOrderEnrtyModel;

	@Before
	public void setUp()
	{
		sapCpiOaaPartnerContributor = new SapCpiOaaPartnerContributor();
		
		List<AbstractOrderEntryModel> abstractOrderEntryModels = new ArrayList<AbstractOrderEntryModel>();
		abstractOrderEntryModels.add(abstractOrderEnrtyModel);
		
		when(orderModel.getCode()).thenReturn(ORDER_ID);
		when(orderModel.getEntries()).thenReturn(abstractOrderEntryModels);
		
		when(abstractOrderEnrtyModel.getEntryNumber()).thenReturn(ENTRY_NUMBER);
		when(abstractOrderEnrtyModel.getSapVendor()).thenReturn(SAP_VENDOR);
	}
	
	@Test
	public void testCheckSapVendor() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		sapCpiOaaPartnerContributor.checkSapVendor(orderModel, result);
		assertTrue(result.size()==1);
		
		Map<String, Object> map = result.get(0);
		assertTrue(map.get(OrderCsvColumns.ORDER_ID).equals(ORDER_ID));
		assertTrue(map.get(OrderEntryCsvColumns.ENTRY_NUMBER).equals(1));
		assertTrue(map.get(PartnerCsvColumns.PARTNER_CODE).equals(SAP_VENDOR));
		assertTrue(map.get(PartnerCsvColumns.PARTNER_ROLE_CODE).equals(PARTNER_FUNCTION_VENDOR));
		
		
		
	}
}

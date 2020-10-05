/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sapbillinginvoice.facade.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.populator.ExternalSapBillingOrderPopulator;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;


/**
 *
 */
@UnitTest
public class ExtSapBillingInvoiceComOrderPopulatorTest
{

	@InjectMocks
	ExternalSapBillingOrderPopulator extSapBillingInvoiceComOrderPopulator;

	@Mock
	private Map<String, SapBillingInvoiceStrategy> handlers;

	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void billingInvoiceOrderDetailPopulator()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");
		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SALES);
		sapOrder.setServiceOrderId("so12345");

		final OrderData assertOrderData = new OrderData();


		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();

		final Set<SAPOrderModel> sapOrderSet = new HashSet<>();

		sapOrderSet.add(sapOrder);

		orderModel.setSapOrders(sapOrderSet);

		final JsonArray billingDocumentArray = new JsonArray();

		final JsonParser parser = new JsonParser();
		final JsonElement jsonElement = parser.parse("{\"BillingDocument\":\"bill123\"}");
		billingDocumentArray.add(jsonElement);

		final ExternalSystemBillingDocumentData billingDocItemData = new ExternalSystemBillingDocumentData();
		billingDocItemData.setBillingDocumentId("bill123");
		billingDocItemData.setSapOrderCode("so12345");

		billingItems.add(billingDocItemData);

		assertOrderData.setExtBillingDocuments(billingItems);

		final SapBillingInvoiceStrategy sapBillingInvoiceServiceStrategy = Mockito.mock(SapBillingInvoiceStrategy.class);

		given(configurationService.getConfiguration()).willReturn(configuration);

		given(configuration.getBoolean("sapbillinginvoice.enable", false))
				.willReturn(true);

		given(sapBillingInvoiceUtils.getSapOrderType(sapOrder)).willReturn("SERVICE");
		given(handlers.get("SERVICE")).willReturn(sapBillingInvoiceServiceStrategy);

		given(sapBillingInvoiceServiceStrategy.getBillingDocuments(sapOrder)).willReturn(billingItems);

		Assert.assertEquals(billingItems.get(0).getSapOrderCode(),
				assertOrderData.getExtBillingDocuments().get(0).getSapOrderCode());

	}

}

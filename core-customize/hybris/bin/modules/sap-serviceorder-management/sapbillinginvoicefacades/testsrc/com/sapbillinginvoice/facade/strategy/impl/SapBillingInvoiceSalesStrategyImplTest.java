/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sapbillinginvoice.facade.strategy.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.impl.SapBillingInvoiceSalesStrategyImpl;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceSalesStrategyImplTest
{

	@InjectMocks
	SapBillingInvoiceSalesStrategyImpl sapBillingInvoiceSalesStrategyImpl;

	@Mock
	private SapBillingInvoiceService sapBillingInvoiceService;

	@Mock
	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	@Mock
	private SAPPlantLogSysOrgModel sapPlantLogSysOrg;

	@Mock
	private SAPLogicalSystemModel sAPLogicalSystemModel;

	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	@Mock
	private Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getBillingDocuments()
	{

		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setName("baseStore", Locale.ENGLISH);
		orderModel.setStore(baseStore);

		final JsonArray billingDocumentArray = new JsonArray();

		final JsonParser parser = new JsonParser();
		final JsonElement jsonElement = parser.parse(
				"{\"BillingDocument\":\"bill123\",\"CreationDate\":\"/Date(1562803200000)/\",\"TransactionCurrency\":\"USD\",\"NetAmount\":\"100.00\",\"TaxAmount\":\"10.00\"}");
		billingDocumentArray.add(jsonElement);

		final List<ExternalSystemBillingDocumentData> listBillingDocumentArray = new ArrayList<>();
		final ExternalSystemBillingDocumentData externalSystemBillingDocumentData = new ExternalSystemBillingDocumentData();
		externalSystemBillingDocumentData.setBillingDocumentId("bill123");

		listBillingDocumentArray.add(externalSystemBillingDocumentData);

		final Map<String, Object> mockParametersMap = new HashMap<>();
		mockParametersMap.put("billingDocumentArray", billingDocumentArray);


		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SALES);
		sapOrder.setServiceOrderId("so12345");
		sapOrder.setOrder(orderModel);
		final Set<ConsignmentModel> consigenments = new HashSet<>();
		final ConsignmentModel consigenment = new ConsignmentModel();
		consigenment.setCode("con123");

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("wh123");

		consigenment.setWarehouse(warehouse);

		consigenments.add(consigenment);

		sapOrder.setConsignments(consigenments);

		given(sapBillingInvoiceUtils.s4DateStringToDate("/Date(1562803200000)/")).willReturn(new Date());

		given(sapBillingInvoiceService.getBusinessDocumentFromS4SSapOrderCode(sapOrder)).willReturn(mockParametersMap);

		given(externalSystemBillingDocumentConverter.convert(jsonElement.getAsJsonObject()))
				.willReturn(externalSystemBillingDocumentData);

		final List<ExternalSystemBillingDocumentData> billinDocArray = sapBillingInvoiceSalesStrategyImpl
				.getBillingDocuments(sapOrder);

		Assert.assertEquals(listBillingDocumentArray.get(0).getBillingDocumentId(),
				billinDocArray.get(0).getBillingDocumentId());


	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.client;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
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
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sap.hybris.sapbillinginvoiceservices.client.impl.SapBillingInvoiceClientImpl;

import rx.Observable;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceClientImplTest
{

	@InjectMocks
	@Spy
	private SapBillingInvoiceClientImpl sapBillingInvoiceClientImpl;

	@Mock
	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	@Mock
	private SAPPlantLogSysOrgModel sapPlantLogSysOrg;

	@Mock
	private SAPLogicalSystemModel sAPLogicalSystemModel;

	@Mock
	private OutboundServiceFacade outboundServiceFacade;

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
	public void getBillingDocForServiceOrderId()
	{


		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setName("baseStore", Locale.ENGLISH);
		orderModel.setStore(baseStore);

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

		final SAPCpiOutboundConfigModel extBillingCpiConfigModel = new SAPCpiOutboundConfigModel();
		extBillingCpiConfigModel.setUrl("http://ext-system-url.com/sap/id");
		extBillingCpiConfigModel.setUsername("extSysUsername");

		final String targetSuffixUrl = "/sap/id?$filter=DocumentReferenceID eq '{serviceOrderCode}'";

		final Map<String, Object> parametersMap = new HashMap<>();

		final JsonArray billingDocumentArray = new JsonArray();

		final JsonParser parser = new JsonParser();
		final JsonElement jsonElement = parser.parse(
				"{\"BillingDocument\":\"90003372\",\"CreationDate\":\"/Date(1562803200000)/\",\"TransactionCurrency\":\"USD\",\"TotalGrossAmount\":\"100.00\"}");

		billingDocumentArray.add(jsonElement);

		final String responseString = "{\"d\":{\"results\":[{\"BillingDocument\":\"90003372\",\"TotalGrossAmount\":\"145.60\",\"CreationDate\":\"/Date(1562803200000)/\"}]}}";

		parametersMap.put("extBillingResponseValue", responseString);
		parametersMap.put("extBillingResponseStatusCode", "success");
		parametersMap.put("billingDocumentArray", billingDocumentArray);

		final ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(parametersMap, HttpStatus.OK);

		Mockito.doReturn(sapPlantLogSysOrg).when(sapPlantLogSysOrgService).getSapPlantLogSysOrgForPlant(baseStore, "wh123");

		Mockito.doReturn(sAPLogicalSystemModel).when(sapPlantLogSysOrg).getLogSys();

		Mockito.doReturn(SapSystemType.SAP_S4HANA).when(sAPLogicalSystemModel).getSapSystemType();

		Mockito.doReturn(configuration).when(configurationService).getConfiguration();

		Mockito.doReturn(targetSuffixUrl).when(configuration).getString(Mockito.any());

		Mockito.doReturn(extBillingCpiConfigModel).when(sapBillingInvoiceClientImpl)
				.getSAPCpiOutboundConfigModelForExtBilling(Mockito.any(), Mockito.any(), Mockito.any());

		Mockito.doReturn(true).when(sapBillingInvoiceClientImpl).isSentSuccessfully(Mockito.any());

		Mockito.doReturn(responseString).when(sapBillingInvoiceClientImpl).getPropertyValue(Mockito.any(), Mockito.any());

		Mockito.doReturn(Observable.just(objectResponseEntity)).when(outboundServiceFacade).send(any(), any(), any());

		final Map<String, Object> assertParametersMap = sapBillingInvoiceClientImpl
				.getBusinessDocumentFromS4ServiceOrderCode(sapOrder);

		final JsonArray assertBillingDocumentArray = (JsonArray) assertParametersMap.get("billingDocumentArray");

		Assert.assertEquals(assertBillingDocumentArray.get(0).getAsJsonObject().get("BillingDocument").toString(),
				billingDocumentArray.get(0).getAsJsonObject().get("BillingDocument").toString());

	}

	@Test
	public void getBillingDocForSapOrderId()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setName("baseStore", Locale.ENGLISH);
		orderModel.setStore(baseStore);

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

		final String targetSuffixUrl = "/sap/id?$filter=SalesDocument eq '{sapOrderCode}'";

		final SAPCpiOutboundConfigModel extBillingCpiConfigModel = new SAPCpiOutboundConfigModel();
		extBillingCpiConfigModel.setUrl("http://ext-system-url.com/sap/id");
		extBillingCpiConfigModel.setUsername("extSysUsername");

		final Map<String, Object> parametersMap = new HashMap<>();


		final JsonArray billingDocumentArray = new JsonArray();

		final JsonParser parser = new JsonParser();
		final JsonElement jsonElement = parser.parse(
				"{\"BillingDocument\":\"90003372\",\"CreationDate\":\"/Date(1562803200000)/\",\"TransactionCurrency\":\"USD\",\"NetAmount\":\"145.60\"}");

		billingDocumentArray.add(jsonElement);

		final String responseString = "{\"d\":{\"results\":[{\"BillingDocument\":\"90003372\",\"NetAmount\":\"145.60\",\"CreationDate\":\"/Date(1562803200000)/\"}]}}";

		parametersMap.put("extBillingResponseValue", responseString);
		parametersMap.put("extBillingResponseStatusCode", "success");
		parametersMap.put("billingDocumentArray", billingDocumentArray);

		final ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(parametersMap, HttpStatus.OK);

		Mockito.doReturn(sapPlantLogSysOrg).when(sapPlantLogSysOrgService).getSapPlantLogSysOrgForPlant(baseStore, "wh123");

		Mockito.doReturn(sAPLogicalSystemModel).when(sapPlantLogSysOrg).getLogSys();

		Mockito.doReturn(SapSystemType.SAP_S4HANA).when(sAPLogicalSystemModel).getSapSystemType();

		Mockito.doReturn(configuration).when(configurationService).getConfiguration();

		Mockito.doReturn(targetSuffixUrl).when(configuration).getString(Mockito.any());

		Mockito.doReturn(extBillingCpiConfigModel).when(sapBillingInvoiceClientImpl)
				.getSAPCpiOutboundConfigModelForExtBilling(Mockito.any(), Mockito.any(), Mockito.any());

		Mockito.doReturn(true).when(sapBillingInvoiceClientImpl).isSentSuccessfully(Mockito.any());

		Mockito.doReturn(responseString).when(sapBillingInvoiceClientImpl).getPropertyValue(Mockito.any(), Mockito.any());

		Mockito.doReturn(Observable.just(objectResponseEntity)).when(outboundServiceFacade).send(any(), any(), any());

		final Map<String, Object> assertParametersMap = sapBillingInvoiceClientImpl
				.getBusinessDocumentFromS4SAPOrderCode(sapOrder);

		final JsonArray assertBillingDocumentArray = (JsonArray) assertParametersMap.get("billingDocumentArray");

		Assert.assertEquals(assertBillingDocumentArray.get(0).getAsJsonObject().get("BillingDocument").toString(),
				billingDocumentArray.get(0).getAsJsonObject().get("BillingDocument").toString());

	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.util.Calendar;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderDestinationService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderItemModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class DefaultSapCpiServiceOrderUpdateOutboundBuilderServiceTest {
	

	@InjectMocks
	DefaultSapCpiServiceOrderUpdateOutboundBuilderService updateBuilder;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private SapCpiOrderDestinationService sapCpiOrderDestinationService;

	static final String ORDER_CODE = "orderCode";
	static final String SAPORDER_CODE = "sapOrderCode";
	static final String SAPORDER_SERVICEORDERID = "sapOrderServiceOrderID";
	static final int CONSIGNMENTENTRY_ONE = 1;
	static final int CONSIGNMENTENTRY_TWO = 2;
	static final String WAREHOUSE_CODE = "s4cmWarehouse";
	static final String TARGET_URL = "https://test:123/serviceorder";
	static final String USER = "user";
	
	
	static final int YEAR = 2020, MONTH = Calendar.FEBRUARY, DAY = 1, HOUR = 11, MINUTE = 0, SECOND = 0;
	static final String REQUESTED_DATE = "2020-02-01T11:00:00";
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		
		Mockito.when(modelService.create(SAPCpiOutboundServiceOrderModel.class)).thenReturn(mockCreateServiceOrder());
		Mockito.when(modelService.create(SAPCpiOutboundServiceOrderItemModel.class)).thenReturn(mockCreateServiceOrderItem());
		Mockito.when(sapCpiOrderDestinationService.readSapLogicalSystem(Mockito.any(BaseStoreModel.class), Mockito.any(String.class))).thenReturn(mockSAPLogicalSystem());
	}


	@Test
	public void buildMainTest()
	{
		ConsignmentModel consignment = generateSourceConsignment();
		SAPCpiOutboundServiceOrderModel outboundOrder = updateBuilder.build(consignment);
		
		Assert.assertEquals(ORDER_CODE, outboundOrder.getCommerceOrderId());
		Assert.assertEquals(SAPORDER_CODE, outboundOrder.getOrderId());
		Assert.assertEquals(SAPORDER_SERVICEORDERID, outboundOrder.getServiceOrderId());
		
		outboundOrder.getSapCpiOutboundServiceOrderItems().forEach(entry ->
			Assert.assertEquals(REQUESTED_DATE, entry.getRequestedServiceStartDateTime())
		);
		Assert.assertEquals(TARGET_URL, outboundOrder.getSapCpiConfig().getUrl());
		Assert.assertEquals(USER, outboundOrder.getSapCpiConfig().getUsername());
	}

	private ConsignmentModel generateSourceConsignment() {
		
		ConsignmentModel consignment = new ConsignmentModel();
		
		OrderModel order = new OrderModel();
		order.setCode(ORDER_CODE);
		final Calendar requestDate = Calendar.getInstance();
		requestDate.set(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
		order.setRequestedServiceStartDate(requestDate.getTime());
		consignment.setOrder(order);
		
		SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode(SAPORDER_CODE);
		sapOrder.setServiceOrderId(SAPORDER_SERVICEORDERID);
		consignment.setSapOrder(sapOrder);
		
		consignment.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		consignment.getConsignmentEntries().add(generateConsignmentEntry(CONSIGNMENTENTRY_ONE));
		consignment.getConsignmentEntries().add(generateConsignmentEntry(CONSIGNMENTENTRY_TWO));
		
		WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode(WAREHOUSE_CODE);
		
		consignment.setWarehouse(warehouse);
		
		BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);
		
		return consignment;
	}
	
	private ConsignmentEntryModel generateConsignmentEntry(int entryNumber)
	{
		ConsignmentEntryModel entry = new ConsignmentEntryModel();
		entry.setSapOrderEntryRowNumber(entryNumber);
		return entry;
	}
	
	private SAPCpiOutboundServiceOrderModel mockCreateServiceOrder() {
		return new SAPCpiOutboundServiceOrderModel();
	}

	private SAPCpiOutboundServiceOrderItemModel mockCreateServiceOrderItem() {
		return new SAPCpiOutboundServiceOrderItemModel();
	}
	
	private SAPLogicalSystemModel mockSAPLogicalSystem() {
		
		SAPLogicalSystemModel sapLogicalSystem = new SAPLogicalSystemModel();
		SAPHTTPDestinationModel httpDestination = new SAPHTTPDestinationModel();
		httpDestination.setTargetURL(TARGET_URL);
		httpDestination.setUserid(USER);
		sapLogicalSystem.setSapHTTPDestination(httpDestination);
		
		return sapLogicalSystem;
	}
	
}

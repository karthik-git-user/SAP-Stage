/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.strategy;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapSendConsignmentToExternalFulfillmentSystemStrategyTest {

  @InjectMocks
  private SapSendConsignmentToExternalFulfillmentSystemStrategy sapSendConsignmentToExternalFulfillmentSystemStrategy;


  @Mock
  private SapCpiOutboundService sapCpiOutboundService;
  @Mock
  private ModelService modelService;
  @Mock
  private SapPlantLogSysOrgService sapPlantLogSysOrgService;
  @Mock
  private OrderHistoryService orderHistoryService;
  @Mock
  private OrderService orderService;
  @Mock
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;
  @Mock
  private TimeService timeService;


  private OrderModel orderModel;


  @Before
  public void setUp() {

    // SAP Logical System
    SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = new SAPPlantLogSysOrgModel();
    SAPLogicalSystemModel sapLogicalSystemModel = new SAPLogicalSystemModel();
    sapPlantLogSysOrgModel.setLogSys(sapLogicalSystemModel);

    // Hybris Order
    orderModel = new OrderModel();
    orderModel.setCode("9999999674");
    BaseStoreModel baseStoreModel = new BaseStoreModel();
    orderModel.setStore(baseStoreModel);
    OrderHistoryEntryModel orderHistoryEntryModel = new OrderHistoryEntryModel();
    orderHistoryEntryModel.setPreviousOrderVersion(orderModel);

    // Order Entries
    OrderEntryModel orderEntryOne = new OrderEntryModel();
    orderEntryOne.setOrder(orderModel);
    orderEntryOne.setEntryNumber(Integer.valueOf(0));
    OrderEntryModel orderEntryTwo = new OrderEntryModel();
    orderEntryTwo.setOrder(orderModel);
    orderEntryTwo.setEntryNumber(Integer.valueOf(1));

    WarehouseModel warehouseModel = new WarehouseModel();

    // Consignment One
    ConsignmentModel consignmentOne = new ConsignmentModel();
    consignmentOne.setCode("cons9999999674_0");
    ConsignmentEntryModel consignmentEntryOne = new ConsignmentEntryModel();
    consignmentEntryOne.setConsignment(consignmentOne);
    consignmentEntryOne.setOrderEntry(orderEntryOne);
    consignmentOne.setConsignmentEntries(Sets.newHashSet(consignmentEntryOne));
    consignmentOne.setWarehouse(warehouseModel);
    consignmentOne.setOrder(orderModel);

    // Consignment Two
    ConsignmentModel consignmentTwo = new ConsignmentModel();
    consignmentTwo.setCode("cons9999999674_1");
    consignmentEntryOne = new ConsignmentEntryModel();
    consignmentEntryOne.setConsignment(consignmentOne);
    consignmentEntryOne.setOrderEntry(orderEntryTwo);
    consignmentTwo.setConsignmentEntries(Sets.newHashSet(consignmentEntryOne));
    consignmentTwo.setWarehouse(warehouseModel);
    consignmentTwo.setOrder(orderModel);

    orderModel.setConsignments(Sets.newHashSet(consignmentOne, consignmentTwo));

    // SCPI Response
    final Map<String, Map> map = Maps.newHashMap();
    final Map<String, String> innerMap = Maps.newHashMap();
    innerMap.put("responseMessage", "The order has been sent successfully to S/4HANA through SCPI!");
    innerMap.put("responseStatus", "Success");
    map.put("SAPCpiOutboundOrder", innerMap);
    ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);

    doNothing().when(modelService).save(any());
    when(timeService.getCurrentTime()).thenReturn(new Date());
    when(modelService.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModel);
    when(modelService.create(SAPOrderModel.class)).thenReturn(new SAPOrderModel());
    when(orderService.clone(any(), any(), any(), any())).thenReturn(orderModel);
    when(orderHistoryService.createHistorySnapshot(orderModel)).thenReturn(orderModel);
    when(sapCpiOutboundService.sendOrder(anyObject())).thenReturn(Observable.just(objectResponseEntity));
    when(sapCpiOrderOutboundConversionService.convertOrderToSapCpiOrder(any())).thenReturn(new SAPCpiOutboundOrderModel());
    when(sapPlantLogSysOrgService.getSapPlantLogSysOrgForPlant(anyObject(), anyObject())).thenReturn(sapPlantLogSysOrgModel);

  }

  @Test
  public void sendConsignment() {

    final AtomicInteger index = new AtomicInteger(0);
    orderModel.getConsignments().forEach(consignment -> {
      setVersionID(index.incrementAndGet());
      sapSendConsignmentToExternalFulfillmentSystemStrategy.sendConsignment(consignment);
    });

    verify(sapCpiOutboundService, times(2)).sendOrder(anyObject());

  }

  void setVersionID(int index) {
    orderModel.setVersionID(Long.toString(Long.valueOf(orderModel.getCode()) + index));
  }


}
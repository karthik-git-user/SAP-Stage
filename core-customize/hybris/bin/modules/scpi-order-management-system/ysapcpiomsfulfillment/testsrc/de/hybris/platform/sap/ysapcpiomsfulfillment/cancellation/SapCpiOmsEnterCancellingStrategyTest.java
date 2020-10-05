/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation;

import com.google.common.collect.Maps;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOmsEnterCancellingStrategyTest {

  @InjectMocks
  private SapCpiOmsEnterCancellingStrategy sapCpiOmsEnterCancellingStrategy;

  @Mock
  private SapCpiOutboundService sapCpiOutboundService;
  @Mock
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;
  @Mock
  private ModelService modelService;

  @Test
  public void changeOrderStatusAfterCancelOperation() {

   // Order Cancellation Record
    OrderModel orderModel =new OrderModel();
    orderModel.setCode("9999999675");
    OrderModificationRecordModel orderModificationRecordModel = new OrderModificationRecordModel();
    orderModificationRecordModel.setOrder(orderModel);
    OrderCancelRecordEntryModel orderCancelRecordEntryModel = new OrderCancelRecordEntryModel();
    orderCancelRecordEntryModel.setModificationRecord(orderModificationRecordModel);

    // SCPI Response
    final Map<String, Map> map = Maps.newHashMap();
    final Map<String, String> innerMap = Maps.newHashMap();
    innerMap.put("responseMessage", "The order cancellation has been sent successfully to S/4HANA through SCPI!");
    innerMap.put("responseStatus", "Success");
    map.put("SAPCpiOutboundOrderCancellation", innerMap);
    ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);

    // Order Cancellation Outbound object
    SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel = new SAPCpiOutboundOrderCancellationModel();

    doNothing().when(modelService).save(any());
    when(sapCpiOrderOutboundConversionService.convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntryModel)).thenReturn(Arrays.asList(sapCpiOutboundOrderCancellationModel));

    when(sapCpiOutboundService.sendOrderCancellation(anyObject())).thenReturn(Observable.just(objectResponseEntity));

    sapCpiOmsEnterCancellingStrategy.changeOrderStatusAfterCancelOperation(orderCancelRecordEntryModel,false);
    verify(sapCpiOutboundService,times(1)).sendOrderCancellation(sapCpiOutboundOrderCancellationModel);

  }

}
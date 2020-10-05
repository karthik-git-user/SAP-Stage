/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.ysapcpiomsfulfillment.SapCpiOmsFulfillmentUtil;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCipOmsInboundOrderHelperTest {

  @InjectMocks
  private SapCipOmsInboundOrderHelper sapCipOmsInboundOrderHelper;
  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private ModelService modelService;
  @Mock
  private BusinessProcessService businessProcessService;
  @Mock
  private OrderCancelService orderCancelService;

  private  SAPOrderModel sapOrderModel;

  @Before
  public void setUp() {

    doNothing().when(modelService).save(any());

    // Hybris Order
    OrderModel orderModel = new OrderModel();
    orderModel.setCode("9999999674");

    // SAP Order
    sapOrderModel = new SAPOrderModel();
    sapOrderModel.setCode("9999999675");
    sapOrderModel.setOrder(orderModel);

    // Consignment Process
    ConsignmentProcessModel consignmentProcessModel = new ConsignmentProcessModel();
    consignmentProcessModel.setCode("cons9999999674_0_ordermanagement");

    ConsignmentModel consignmentModel = new ConsignmentModel();
    consignmentModel.setConsignmentEntries(Sets.newHashSet(new ConsignmentEntryModel()));
    consignmentModel.setConsignmentProcesses(Sets.newHashSet(consignmentProcessModel));

    sapOrderModel.setConsignments(Sets.newHashSet(consignmentModel));
    when(flexibleSearchService.searchUnique(anyObject())).thenReturn(sapOrderModel);


  }

  @Test
  public void processOrderConfirmationFromHub() {

    sapOrderModel.setSapOrderStatus(SAPOrderStatus.NOT_SENT_TO_ERP);
    sapCipOmsInboundOrderHelper.processOrderConfirmationFromHub("9999999675");
    verify(businessProcessService,times(1)).triggerEvent("cons9999999674_0_ordermanagement_ConsignmentSubmissionConfirmationEvent");

  }

  @Test
  public void cancelOrder() throws OrderCancelException {

    sapOrderModel.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
    when(orderCancelService.getCancelRecordForOrder(anyObject())).thenReturn(new OrderCancelRecordModel());

    sapCipOmsInboundOrderHelper.cancelOrder(null,"9999999675");

    ArgumentCaptor<BusinessProcessEvent> eventArgumentCaptor = ArgumentCaptor.forClass(BusinessProcessEvent.class);
    verify(businessProcessService, times(1)).triggerEvent(eventArgumentCaptor.capture());

    assertThat(eventArgumentCaptor.getValue().getEvent()).isEqualTo("cons9999999674_0_ordermanagement_ConsignmentActionEvent");
    assertThat(eventArgumentCaptor.getValue().getChoice()).isEqualTo(SapCpiOmsFulfillmentUtil.CANCEL_CONSIGNMENT);

  }

  @Test
  public void cancelOrderFromSapBackend() throws OrderCancelException {

    sapOrderModel.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
    when(orderCancelService.getCancelRecordForOrder(anyObject())).thenReturn(null);

    sapCipOmsInboundOrderHelper.cancelOrder(null,"9999999675");

    ArgumentCaptor<BusinessProcessEvent> eventArgumentCaptor = ArgumentCaptor.forClass(BusinessProcessEvent.class);
    verify(businessProcessService, times(0)).triggerEvent(eventArgumentCaptor.capture());

  }

}
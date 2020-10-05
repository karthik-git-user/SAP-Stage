/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCipOmsInboundHelperTest {

  @InjectMocks
  private SapCipOmsInboundHelper sapCipOmsInboundHelper;
  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private ModelService modelService;
  @Mock
  private BusinessProcessService businessProcessService;

  private OrderModel orderModel;
  private ConsignmentModel consignmentModel;

  @Before
  public void setUp() {

    doNothing().when(modelService).save(any());

    // Hybris Order
    orderModel = new OrderModel();
    orderModel.setCode("9999999674");

    ProductModel productOne = new ProductModel();
    productOne.setCode("266899");

    ProductModel productTwo = new ProductModel();
    productTwo.setCode("266999");

    OrderEntryModel orderEntryOne = new OrderEntryModel();
    orderEntryOne.setProduct(productOne);
    orderEntryOne.setOrder(orderModel);
    orderEntryOne.setEntryNumber(Integer.valueOf(0));

    OrderEntryModel orderEntryTwo = new OrderEntryModel();
    orderEntryTwo.setProduct(productTwo);
    orderEntryTwo.setOrder(orderModel);
    orderEntryTwo.setEntryNumber(Integer.valueOf(1));

    // SAP Order
    SAPOrderModel sapOrderModel = new SAPOrderModel();
    sapOrderModel.setCode("9999999675");
    sapOrderModel.setOrder(orderModel);
    sapOrderModel.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);

    // Consignment Process
    ConsignmentProcessModel consignmentProcessModel = new ConsignmentProcessModel();
    consignmentProcessModel.setCode("cons9999999674_0_ordermanagement");

    consignmentModel = new ConsignmentModel();
    consignmentModel.setCode("cons9999999674_0");
    consignmentModel.setConsignmentProcesses(Sets.newHashSet(consignmentProcessModel));

    ConsignmentEntryModel consignmentEntryOne = new ConsignmentEntryModel();
    consignmentEntryOne.setSapOrderEntryRowNumber(orderEntryOne.getEntryNumber() + 1);
    consignmentEntryOne.setShippedQuantity(Long.valueOf(0));
    consignmentEntryOne.setQuantity(Long.valueOf(2));
    consignmentEntryOne.setConsignment(consignmentModel);
    consignmentEntryOne.setOrderEntry(orderEntryOne);


    ConsignmentEntryModel consignmentEntryTwo = new ConsignmentEntryModel();
    consignmentEntryTwo.setSapOrderEntryRowNumber(orderEntryTwo.getEntryNumber() + 1);
    consignmentEntryTwo.setShippedQuantity(Long.valueOf(0));
    consignmentEntryTwo.setQuantity(Long.valueOf(3));
    consignmentEntryTwo.setConsignment(consignmentModel);
    consignmentEntryTwo.setOrderEntry(orderEntryTwo);
    consignmentEntryTwo.setStatus(ConsignmentEntryStatus.READY);

    consignmentModel.setConsignmentEntries(Sets.newHashSet(consignmentEntryOne, consignmentEntryTwo));

    when(flexibleSearchService.searchUnique(any())).thenReturn(sapOrderModel);

  }

  @Test
  public void processDeliveryNotification() {

    consignmentModel.setStatus(ConsignmentStatus.READY);
    orderModel.setConsignments(Sets.newHashSet(consignmentModel));
    orderModel.setStatus(OrderStatus.READY);

    sapCipOmsInboundHelper.processDeliveryNotification("9999999675", "1");

    verify(flexibleSearchService, times(1)).searchUnique(anyObject());

    ArgumentCaptor<BusinessProcessEvent> eventArgumentCaptor = ArgumentCaptor.forClass(BusinessProcessEvent.class);
    verify(businessProcessService, times(1)).triggerEvent(eventArgumentCaptor.capture());

    assertThat(eventArgumentCaptor.getValue().getEvent()).isEqualTo("cons9999999674_0_ordermanagement_ConsignmentActionEvent");
    assertThat(eventArgumentCaptor.getValue().getChoice()).isEqualTo(SapCpiOmsFulfillmentUtil.PACK_CONSIGNMENT);

  }

  @Test
  public void processDeliveryNotificationWithWrongEntryNumber() {

    consignmentModel.setStatus(ConsignmentStatus.READY);
    orderModel.setConsignments(Sets.newHashSet(consignmentModel));
    orderModel.setStatus(OrderStatus.READY);

    sapCipOmsInboundHelper.processDeliveryNotification("9999999675", "0");

    verify(flexibleSearchService, times(1)).searchUnique(anyObject());
    verify(businessProcessService, times(0)).triggerEvent(isA(BusinessProcessEvent.class));

  }

  @Test
  public void processGoodsIssueNotificationWithFullyQuantity() {

    consignmentModel.setStatus(ConsignmentStatus.READY_FOR_SHIPPING);
    orderModel.setConsignments(Sets.newHashSet(consignmentModel));

    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "1", "2", "20190212");

    verify(flexibleSearchService, times(1)).searchUnique(anyObject());
    ArgumentCaptor<BusinessProcessEvent> eventArgumentCaptor = ArgumentCaptor.forClass(BusinessProcessEvent.class);
    verify(businessProcessService, times(0)).triggerEvent(eventArgumentCaptor.capture());

    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "2", "3", "20190214");

    verify(flexibleSearchService, times(2)).searchUnique(anyObject());
    verify(businessProcessService, times(1)).triggerEvent(eventArgumentCaptor.capture());

    assertThat(eventArgumentCaptor.getValue().getEvent()).isEqualTo("cons9999999674_0_ordermanagement_ConsignmentActionEvent");
    assertThat(eventArgumentCaptor.getValue().getChoice()).isEqualTo(SapCpiOmsFulfillmentUtil.CONFIRM_SHIP_CONSIGNMENT);

  }

  @Test
  public void processGoodsIssueNotificationWithPartialQuantity() {

    consignmentModel.setStatus(ConsignmentStatus.READY_FOR_SHIPPING);
    orderModel.setConsignments(Sets.newHashSet(consignmentModel));

    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "1", "2", "20190212");
    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "2", "2", "20190214");

    verify(flexibleSearchService, times(2)).searchUnique(anyObject());
    verify(businessProcessService, times(0)).triggerEvent(isA(BusinessProcessEvent.class));

  }

  @Test
  public void processGoodsIssueNotificationWithExtraQuantity() {

    consignmentModel.setStatus(ConsignmentStatus.READY_FOR_SHIPPING);
    orderModel.setConsignments(Sets.newHashSet(consignmentModel));

    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "1", "2", "20190212");
    sapCipOmsInboundHelper.processGoodsIssueNotification("9999999675", "2", "4", "20190214");

    verify(flexibleSearchService, times(2)).searchUnique(anyObject());
    verify(businessProcessService, times(0)).triggerEvent(isA(BusinessProcessEvent.class));

  }


}
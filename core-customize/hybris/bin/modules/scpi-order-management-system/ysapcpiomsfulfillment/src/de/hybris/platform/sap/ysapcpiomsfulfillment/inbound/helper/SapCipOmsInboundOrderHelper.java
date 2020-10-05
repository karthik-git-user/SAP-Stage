/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundOrderHelper;
import de.hybris.platform.sap.ysapcpiomsfulfillment.SapCpiOmsFulfillmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class SapCipOmsInboundOrderHelper extends SapOmsDataHubInboundOrderHelper {

  private static final Logger LOG = LoggerFactory.getLogger(SapCipOmsInboundOrderHelper.class);

  private OrderCancelService orderCancelService;

  /**
   * Trigger the consignment submission confirmation event after receiving the confirmation from SAP backend
   *
   * @param orderNumber
   */
  @Override
  public void processOrderConfirmationFromHub(String orderNumber) {

    final SAPOrderModel sapOrder = readSapOrder(orderNumber);

    // Check if SAP Order has already been confirmed from SAP backend and it has not been cancelled
    if (sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP) ||
            sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CANCELLED_FROM_ERP)) {
      LOG.warn("SAP order [{}] confirmation has already been processed!", sapOrder.getCode());
      return;
    }

    sapOrder.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
    getModelService().save(sapOrder);

    sapOrder.getConsignments().stream().findFirst().ifPresent(consignment ->

            consignment.getConsignmentProcesses().stream().findFirst().ifPresent(consignmentProcess -> {

                      String event = new StringBuilder()
                              .append(consignmentProcess.getCode())
                              .append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT)
                              .toString();

                      LOG.info("Consignment confirmation event [{}] has been triggered!", event);
                      getBusinessProcessService().triggerEvent(event);

                    }
            )
    );

  }

  /**
   * Trigger the consignment action event to cancel consignment after receiving the cancellation confirmation from SAP backend
   *
   * @param orderInformation
   * @param sapOrderCode
   */
  @Override
  public void cancelOrder(String orderInformation, String sapOrderCode) {

    final SAPOrderModel sapOrder = readSapOrder(sapOrderCode);

    try {
      // Check if SAP order cancellation has been initiated from Hybris
      if (getOrderCancelService().getCancelRecordForOrder(sapOrder.getOrder()) == null) {
        LOG.error("Initiating SAP order [{}] cancellation from the SAP backend is not supported!", sapOrderCode);
        return;
      }
    } catch (OrderCancelException exception) {
      LOG.error("Error while reading the order [{}] cancellation record for the SAP order [{}]!", sapOrder.getOrder().getCode(), sapOrderCode, exception);
      return;
    }

    // Check if SAP order has already received creation confirmation from SAP backend
    if (!sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP)) {
      LOG.warn("SAP order [{}] has not received the order creation confirmation from SAP backend. The SAP order status is [{}]!",
              sapOrder.getCode(), sapOrder.getSapOrderStatus());
      return;
    }

    // Check if SAP order has already received cancellation confirmation from SAP backend
    if (sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CANCELLED_FROM_ERP)) {
      LOG.warn("SAP order [{}] has already been cancelled!", sapOrderCode);
      return;
    }

    sapOrder.setSapOrderStatus(SAPOrderStatus.CANCELLED_FROM_ERP);
    getModelService().save(sapOrder);

    sapOrder.getConsignments().stream().findFirst().ifPresent(consignment -> {

      consignment.setStatus(ConsignmentStatus.CANCELLED);
      getModelService().save(consignment);

      consignment.getConsignmentEntries().forEach(consignmentEntry -> {
                consignmentEntry.setStatus(ConsignmentEntryStatus.CANCELLED);
                getModelService().save(consignmentEntry);
              }
      );

      consignment.getConsignmentProcesses().stream().findFirst().ifPresent(consignmentProcess -> {


        final BusinessProcessEvent event = BusinessProcessEvent.builder(
                new StringBuilder()
                        .append(consignmentProcess.getCode())
                        .append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_ACTION_EVENT)
                        .toString()
        ).withChoice(SapCpiOmsFulfillmentUtil.CANCEL_CONSIGNMENT).build();

        LOG.info("Consignment cancellation event [{}] has been triggered!", event);
        getBusinessProcessService().triggerEvent(event);

      });

    });

  }


  protected OrderCancelService getOrderCancelService() {
    return orderCancelService;
  }

  @Required
  public void setOrderCancelService(OrderCancelService orderCancelService) {
    this.orderCancelService = orderCancelService;
  }

}

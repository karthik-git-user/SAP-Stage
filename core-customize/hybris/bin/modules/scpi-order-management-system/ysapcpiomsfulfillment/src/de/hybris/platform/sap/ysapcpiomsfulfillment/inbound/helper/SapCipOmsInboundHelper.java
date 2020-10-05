/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundHelper;
import de.hybris.platform.sap.ysapcpiomsfulfillment.SapCpiOmsFulfillmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SapCipOmsInboundHelper extends SapOmsDataHubInboundHelper {

  private static final Logger LOG = LoggerFactory.getLogger(SapCipOmsInboundHelper.class);

  /**
   * Process delivery notification from SAP backend
   *
   * @param orderCode
   * @param entryNumber
   */
  @Override
  public void processDeliveryNotification(String orderCode, String entryNumber) {

    SAPOrderModel sapOrder = readSapOrder(orderCode);

    if (sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP)) {

      final OrderModel order = sapOrder.getOrder();

      if (order.getStatus().equals(OrderStatus.CANCELLED)) {
        LOG.warn("The delivery notification for SAP order [{}] cannot be processed. The parent order [{}] has been cancelled!",
                sapOrder.getCode(), order.getCode());
        return;
      }

      findConsignmentEntry(sapOrder, entryNumber).ifPresent(entry -> triggerDeliveryEvent(order, entry));

    } else {

      LOG.warn("The delivery notification for SAP order [{}] cannot be processed. The SAP order status is [{}]!",
              sapOrder.getCode(), sapOrder.getSapOrderStatus());

    }

  }

  /**
   * Process goods issue notification from SAP backend
   *
   * @param orderCode
   * @param entryNumber
   * @param quantity
   * @param goodsIssueDate
   */
  @Override
  public void processGoodsIssueNotification(String orderCode, String entryNumber, String quantity, String goodsIssueDate) {

    final SAPOrderModel sapOrder = readSapOrder(orderCode);
    final OrderModel order = sapOrder.getOrder();

    findConsignmentEntry(sapOrder, entryNumber).ifPresent(entry -> {

      if (entry.getConsignment().getStatus().equals(ConsignmentStatus.READY_FOR_SHIPPING) ||
              entry.getConsignment().getStatus().equals(ConsignmentStatus.READY_FOR_PICKUP)) {

        triggerGoodsIssueEvent(order, entry, quantity, goodsIssueDate);

      } else {

        LOG.warn("The goods issue notification for SAP order [{}] cannot be processed. The related consignment status is [{}]!",
                sapOrder.getCode(), entry.getConsignment().getStatus());

      }

    });

  }

  /**
   * Trigger delivery notification
   *
   * @param order
   * @param consignmentEntry
   */
  @Override
  protected void triggerDeliveryEvent(OrderModel order, ConsignmentEntryModel consignmentEntry) {

    if (consignmentEntry.getConsignment().getStatus().equals(ConsignmentStatus.READY)) {

      getModelService().save(consignmentEntry.getConsignment());

      LOG.info("Delivery of consignment entry [{}] for product[{}] with SAP order entry number [{}] has been processed!",
              consignmentEntry.getConsignment().getCode(),
              consignmentEntry.getOrderEntry().getProduct().getCode(),
              consignmentEntry.getSapOrderEntryRowNumber());

      consignmentEntry.getConsignment().getConsignmentProcesses().stream().findFirst().ifPresent(consignmentProcess -> {

        final BusinessProcessEvent event = BusinessProcessEvent.builder(new StringBuilder()
                .append(consignmentProcess.getCode())
                .append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_ACTION_EVENT)
                .toString()
        ).withChoice(SapCpiOmsFulfillmentUtil.PACK_CONSIGNMENT).build();

        LOG.info("Consignment entry delivery event [{}] has been triggered!", event);

        getBusinessProcessService().triggerEvent(event);

      });

    } else {

      LOG.warn("The delivery notification for consignment [{}] has already been processed!", consignmentEntry.getConsignment().getCode());

    }

  }

  /**
   * Trigger goods issue notification
   *
   * @param order
   * @param consignmentEntry
   * @param quantity
   * @param goodsIssueDate
   */
  @Override
  protected void triggerGoodsIssueEvent(OrderModel order, ConsignmentEntryModel consignmentEntry, String quantity, String goodsIssueDate) {

    // Already shipped quantity
    Long shippedQuantity = consignmentEntry.getShippedQuantity() != null ? consignmentEntry.getShippedQuantity() : Long.valueOf(0);

    // Remaining quantity to ship
    Long toShipQuantity = Long.parseLong(quantity);

    // Updated shipped quantity
    Long updatedShippedQuantity = shippedQuantity + toShipQuantity;

    if ((updatedShippedQuantity.compareTo(consignmentEntry.getQuantity()) <= 0)) {

      consignmentEntry.setShippedQuantity(updatedShippedQuantity);
      getModelService().save(consignmentEntry);

    } else {

      LOG.error("Shipped quantity [{}] cannot be greater than the consignment entry quantity [{}]!", updatedShippedQuantity, consignmentEntry.getQuantity());
      return;

    }

    if (consignmentEntry.getQuantity().equals(consignmentEntry.getShippedQuantity())) {

      consignmentEntry.setStatus(ConsignmentEntryStatus.SHIPPED);
      consignmentEntry.getConsignment().setShippingDate(convertStringToDate(goodsIssueDate));
      getModelService().save(consignmentEntry);
      updateConsignmentBusinessProcess(consignmentEntry);

    } else {

      LOG.info("Partially shipped quantity of [{}] for product [{}_{}] with SAP order entry number [{}]!",
              consignmentEntry.getShippedQuantity(),
              consignmentEntry.getConsignment().getCode(),
              consignmentEntry.getOrderEntry().getProduct().getCode(),
              consignmentEntry.getSapOrderEntryRowNumber());
    }

  }

  /**
   * Update the status of the external consignment process
   *
   * @param consignmentEntry
   */
  protected void updateConsignmentBusinessProcess(ConsignmentEntryModel consignmentEntry) {

    boolean isConsignmentFullyShipped = consignmentEntry.getConsignment().getConsignmentEntries().stream()
            .allMatch(entry -> entry.getStatus().equals(ConsignmentEntryStatus.SHIPPED));

    LOG.info("Shipping of consignment entry [{}_{}] with SAP order entry number [{}] has been processed!",
            consignmentEntry.getConsignment().getCode(),
            consignmentEntry.getOrderEntry().getProduct().getCode(),
            consignmentEntry.getSapOrderEntryRowNumber());

    if (isConsignmentFullyShipped) {

      consignmentEntry.getConsignment().getConsignmentProcesses().stream().findFirst().ifPresent(consignmentProcess -> {

        final BusinessProcessEvent event = BusinessProcessEvent.builder(
                new StringBuilder()
                        .append(consignmentProcess.getCode())
                        .append(SapCpiOmsFulfillmentUtil.CONSIGNMENT_ACTION_EVENT)
                        .toString()
        ).withChoice(SapCpiOmsFulfillmentUtil.CONFIRM_SHIP_CONSIGNMENT).build();

        LOG.info("Consignment shipping event [{}] has been triggered!", event);
        getBusinessProcessService().triggerEvent(event);

      });

    } else {

      LOG.info("Consignment [{}] has not yet been fully shipped!", consignmentEntry.getConsignment().getCode());

    }

  }

  /**
   * Find the consignment entry for the given order entry
   *
   * @param sapOrder
   * @param entryNumber
   * @return
   */
  protected Optional<ConsignmentEntryModel> findConsignmentEntry(SAPOrderModel sapOrder, String entryNumber) {

    Optional<ConsignmentEntryModel> consignmentEntry = Optional.empty();

    for (final ConsignmentModel consignment : sapOrder.getOrder().getConsignments()) {

      consignmentEntry = consignment.getConsignmentEntries().stream()
              .filter(entry -> entry.getSapOrderEntryRowNumber() == Integer.parseInt(entryNumber)).findFirst();

      if (consignmentEntry.isPresent()) {
        return consignmentEntry;
      }

    }

    LOG.error("SAP order [{}] with order entry number [{}] cannot be mapped to any order entry in the parent Hybris order [{}]!",
            sapOrder.getCode(), entryNumber, sapOrder.getOrder().getCode());
    return consignmentEntry;
  }

}

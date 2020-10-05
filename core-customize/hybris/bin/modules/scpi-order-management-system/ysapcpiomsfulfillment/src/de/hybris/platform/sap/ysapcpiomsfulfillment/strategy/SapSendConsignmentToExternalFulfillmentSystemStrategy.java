/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.strategy;

import com.google.common.collect.Sets;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import rx.Single;
import rx.SingleSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class SapSendConsignmentToExternalFulfillmentSystemStrategy implements SendConsignmentToExternalFulfillmentSystemStrategy {


  private static final Logger LOG = LoggerFactory.getLogger(SapSendConsignmentToExternalFulfillmentSystemStrategy.class);

  private SapPlantLogSysOrgService sapPlantLogSysOrgService;
  private OrderHistoryService orderHistoryService;
  private ModelService modelService;
  private SapCpiOutboundService sapCpiOutboundService;
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;
  private OrderService orderService;
  private TimeService timeService;


  /**
   * {@inheritDoc}
   */
  @Override
  public void sendConsignment(ConsignmentModel consignment) {

    consignment.getConsignmentEntries().forEach(consignmentEntry ->
    {
      consignmentEntry.setSapOrderEntryRowNumber(consignmentEntry.getOrderEntry().getEntryNumber() + 1);
      getModelService().save(consignmentEntry);
    });

    final OrderModel order = (OrderModel) consignment.getOrder();

    // Read customizing data from the base store configuration
    final SAPPlantLogSysOrgModel sapPlantLogSysOrg = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
            order.getStore(), consignment.getWarehouse().getCode());

    // Initialize order history entry
    final OrderHistoryEntryModel orderHistoryEntry = initializeOrderHistory(order, sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());

    // Initialize SAPOrder
    final SAPOrderModel sapOrder = initializeSapOrder(orderHistoryEntry, consignment);


    sendOrderToScpi(consignment, orderHistoryEntry).subscribe(

            new SingleSubscriber<ResponseEntity<Map>>() {

              @Override
              public void onSuccess(ResponseEntity<Map> mapResponseEntity) {

                if (isSentSuccessfully(mapResponseEntity)) {

                  // Save order history entry
                  orderHistoryEntry.setTimestamp(getTimeService().getCurrentTime());
                  getOrderHistoryService().saveHistorySnapshot(orderHistoryEntry.getPreviousOrderVersion());
                  getModelService().save(orderHistoryEntry);

                  // Save SAPOrder
                  sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);
                  sapOrder.setOrder(order);
                  getModelService().save(sapOrder);

                  LOG.info("SAP Order [{}] that is related to Hybris order [{}] has been sent successfully to the SAP backend through SCPI! {}",
                          sapOrder.getCode(), order.getCode(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
                } else {

                  LOG.error("SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend!",
                          sapOrder.getCode(), order.getCode());

                }


              }

              @Override
              public void onError(Throwable error) {

                LOG.error("SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend through SCPI!",
                        sapOrder.getCode(), order.getCode(), error);
              }

            });

  }

  /**
   * Send the consignment to the external system after wrapping it within an order
   *
   * @param consignment
   * @return
   */
  protected Single<ResponseEntity<Map>> sendOrderToScpi(ConsignmentModel consignment, OrderHistoryEntryModel orderHistoryEntry) {


    // Read customizing data from the base store configuration
    SAPPlantLogSysOrgModel sapPlantLogSysOrg = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
            consignment.getOrder().getStore(), consignment.getWarehouse().getCode());

    // Clone hybris parent order
    OrderModel clonedOrder = getOrderService().clone(null, null, consignment.getOrder(),
            orderHistoryEntry.getPreviousOrderVersion().getVersionID());

    List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

    // Copy order entries
    consignment.getConsignmentEntries().stream()
            .forEach(entry -> orderEntries.add(entry.getOrderEntry()));

    // Set cloned order attributes
    clonedOrder.setConsignments(Sets.newHashSet(consignment));
    clonedOrder.setSapLogicalSystem(sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());
    clonedOrder.setSapSalesOrganization(sapPlantLogSysOrg.getSalesOrg());
    clonedOrder.setEntries(orderEntries);
    clonedOrder.setSapSystemType(sapPlantLogSysOrg.getLogSys().getSapSystemType());
    clonedOrder.setPaymentTransactions(consignment.getOrder().getPaymentTransactions());

    // Send cloned order to SCPI
    return getSapCpiOutboundService().sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(clonedOrder)).toSingle();

  }

  /**
   * Initialize an entry in the order history for the SAP order before sending it to the external system
   *
   * @param order
   * @param logicalSystem
   * @return
   */
  protected OrderHistoryEntryModel initializeOrderHistory(final OrderModel order, String logicalSystem) {

    final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
    final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);

    historyEntry.setOrder(order);
    historyEntry.setPreviousOrderVersion(snapshot);

    historyEntry.setDescription(String.format("SAP sales document [%s] has been sent to the logical system [%s]!",
            snapshot.getVersionID(), logicalSystem));

    return historyEntry;

  }

  /**
   * Initialize an SAP order from the parent Hybris order
   *
   * @param orderHistoryEntry
   * @param consignment
   * @return SAPOrderModel
   */
  protected SAPOrderModel initializeSapOrder(final OrderHistoryEntryModel orderHistoryEntry,
                                             final ConsignmentModel consignment) {
    SAPOrderModel sapOrder = getModelService().create(SAPOrderModel.class);
    sapOrder.setCode(orderHistoryEntry.getPreviousOrderVersion().getVersionID());
    sapOrder.setConsignments(Sets.newHashSet(consignment));
    return sapOrder;
  }

  protected SapPlantLogSysOrgService getSapPlantLogSysOrgService() {
    return sapPlantLogSysOrgService;
  }

  @Required
  public void setSapPlantLogSysOrgService(SapPlantLogSysOrgService sapPlantLogSysOrgService) {
    this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
  }

  protected OrderHistoryService getOrderHistoryService() {
    return orderHistoryService;
  }

  @Required
  public void setOrderHistoryService(OrderHistoryService orderHistoryService) {
    this.orderHistoryService = orderHistoryService;
  }

  protected ModelService getModelService() {
    return modelService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  protected SapCpiOutboundService getSapCpiOutboundService() {
    return sapCpiOutboundService;
  }

  @Required
  public void setSapCpiOutboundService(SapCpiOutboundService sapCpiOutboundService) {
    this.sapCpiOutboundService = sapCpiOutboundService;
  }

  protected SapCpiOrderOutboundConversionService getSapCpiOrderOutboundConversionService() {
    return sapCpiOrderOutboundConversionService;
  }

  @Required
  public void setSapCpiOrderOutboundConversionService(SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService) {
    this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
  }

  protected OrderService getOrderService() {
    return orderService;
  }

  @Required
  public void setOrderService(OrderService orderService) {
    this.orderService = orderService;
  }

  protected TimeService getTimeService() {
    return timeService;
  }

  @Required
  public void setTimeService(TimeService timeService) {
    this.timeService = timeService;
  }


}

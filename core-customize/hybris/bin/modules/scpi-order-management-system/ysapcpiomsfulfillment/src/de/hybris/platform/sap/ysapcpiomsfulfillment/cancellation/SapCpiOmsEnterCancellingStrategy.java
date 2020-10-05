/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.ordercancel.impl.orderstatechangingstrategies.EnterCancellingStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import rx.SingleSubscriber;

import java.util.List;
import java.util.Map;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class SapCpiOmsEnterCancellingStrategy extends EnterCancellingStrategy {


  private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsEnterCancellingStrategy.class);

  private SapCpiOutboundService sapCpiOutboundService;
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;

  /**
   * Change OrderStatus of an order after cancel operation
   *
   * @param orderCancelRecordEntry
   * @param saveOrderModel
   */
  @Override
  public void changeOrderStatusAfterCancelOperation(OrderCancelRecordEntryModel orderCancelRecordEntry, boolean saveOrderModel) {

    super.changeOrderStatusAfterCancelOperation(orderCancelRecordEntry, saveOrderModel);
    processConsignmentsCancellations(orderCancelRecordEntry);

  }

  /**
   * Process external consignments cancellations
   *
   * @param orderCancelRecordEntry
   */
  protected void processConsignmentsCancellations(OrderCancelRecordEntryModel orderCancelRecordEntry) {

    final List<SAPCpiOutboundOrderCancellationModel> sapCpiOrderCancellations = getSapCpiOrderOutboundConversionService()
            .convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry);

    if (sapCpiOrderCancellations.isEmpty()) {

      LOG.warn("There are no SAP orders attached to the order {} so there is no cancellation requests to be sent to SCPI!",
              orderCancelRecordEntry.getModificationRecord().getOrder().getCode());

      return;
    }

    if (orderCancelRecordEntry.getCancelReason() == null) {
      orderCancelRecordEntry.setCancelReason(CancelReason.OTHER);
    }

    sapCpiOrderCancellations.forEach(sapCpiOrderCancellation -> sendConsignmentCancellationToScpi(sapCpiOrderCancellation, orderCancelRecordEntry));

  }

  /**
   * Send consignment cancellation to the SAP backend through SCPI
   *
   * @param sapCpiOutboundOrderCancellation
   * @param orderCancelRecordEntry
   */
  protected void sendConsignmentCancellationToScpi(SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation, OrderCancelRecordEntryModel orderCancelRecordEntry) {

    getSapCpiOutboundService().sendOrderCancellation(sapCpiOutboundOrderCancellation).toSingle().subscribe(new SingleSubscriber<ResponseEntity<Map>>() {

      @Override
      public void onSuccess(ResponseEntity<Map> mapResponseEntity) {

        if (isSentSuccessfully(mapResponseEntity)) {

          LOG.info("The OMS order {} cancellation request has been successfully sent to the SAP backend through SCPI!{}",
                  orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));

        } else {

          LOG.error("The OMS order {} cancellation request has not been sent to the SAP backend! {}",
                  orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
        }

      }

      @Override
      public void onError(Throwable throwable) {

        LOG.error("The OMS order {} cancellation request has not been sent to the SAP backend through SCPI!",
                orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), throwable);

      }

    });

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

}

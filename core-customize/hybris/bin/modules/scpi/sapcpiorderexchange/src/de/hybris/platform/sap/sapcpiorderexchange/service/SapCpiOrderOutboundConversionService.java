/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.List;

/**
 * SapCpiOrderOutboundConversionService
 */
public interface SapCpiOrderOutboundConversionService {

  /**
   * Converts OrderModel to SAPCpiOutboundOrderModel
   * @param orderModel OrderModel
   * @return SAPCpiOutboundOrderModel
   */
  SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel);

  /**
   * Converts OrderModel to any type that extends SAPCpiOutboundOrderModel
   * @param orderModel  Representation of order within commerce
   * @param sapCpiOutboundOrderModel  Representation of outbound order
   * @param <T>
   * @return  Representation of converted order as outbound order
   */
  default <T extends SAPCpiOutboundOrderModel> T convertOrderToSapCpiOrder(OrderModel orderModel, T sapCpiOutboundOrderModel ){
    return sapCpiOutboundOrderModel;
  }

  /**
   * convertCancelOrderToSapCpiCancelOrder
   * @param orderCancelRecordEntryModel OrderCancelRecordEntryModel
   * @return List<SAPCpiOutboundOrderCancellationModel>
   */
  List<SAPCpiOutboundOrderCancellationModel> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel);

}
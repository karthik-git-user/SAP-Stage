/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderCancellationMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * SAP CPI OMM Order Outbound Conversion Service
 */
public class SapCpiOmmOrderOutboundConversionService implements SapCpiOrderOutboundConversionService {

  private List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderMappers;
  private List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> sapCpiOrderCancellationMappers;

  @Override
  public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel) {

    return convertOrderToSapCpiOrder(orderModel, new SAPCpiOutboundOrderModel());

  }

  @Override
  public <T extends SAPCpiOutboundOrderModel> T convertOrderToSapCpiOrder(OrderModel orderModel, T sapCpiOutboundOrderModel) {

    getSapCpiOrderMappers().forEach(mapper -> mapper.map(orderModel, sapCpiOutboundOrderModel));
    return sapCpiOutboundOrderModel;

  }

  @Override
  public List<SAPCpiOutboundOrderCancellationModel> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel) {

    List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations = new ArrayList<>();
    getSapCpiOrderCancellationMappers().forEach(mapper -> mapper.map(orderCancelRecordEntryModel, sapCpiOutboundOrderCancellations));
    return sapCpiOutboundOrderCancellations;

  }

  protected List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> getSapCpiOrderMappers() {
    return sapCpiOrderMappers;
  }

  @Required
  public void setSapCpiOrderMappers(List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderExchangeMappers) {
    this.sapCpiOrderMappers = sapCpiOrderExchangeMappers;
  }

  protected List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> getSapCpiOrderCancellationMappers() {
    return sapCpiOrderCancellationMappers;
  }

  @Required
  public void setSapCpiOrderCancellationMappers(List<SapCpiOrderCancellationMapperService<OrderCancelRecordEntryModel, SAPCpiOutboundOrderCancellationModel>> sapCpiOrderCancellationMappers) {
    this.sapCpiOrderCancellationMappers = sapCpiOrderCancellationMappers;
  }

}
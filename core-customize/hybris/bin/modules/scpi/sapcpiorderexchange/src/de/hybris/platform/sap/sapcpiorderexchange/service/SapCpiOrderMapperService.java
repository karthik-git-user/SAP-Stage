/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

/**
 * Provides mapping from {@link OrderModel} to {@link SAPCpiOutboundOrderModel}.
 *
 * @param <SOURCE> the source object should of type OrderModel
 * @param <TARGET> the target object should of type SAPCpiOutboundOrderModel
 */
public interface SapCpiOrderMapperService<SOURCE extends OrderModel, TARGET extends SAPCpiOutboundOrderModel> {
  /**
   * Performs mapping from source to target.
   *
   * @param source Order Model
   * @param target SAP CPI Outbound Order Model
   */
  void map(SOURCE source, TARGET target);

}
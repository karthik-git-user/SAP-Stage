/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;

import java.util.List;

/**
 * Provides mapping from {@link OrderCancelRecordEntryModel} to {@link List<SAPCpiOutboundOrderCancellationModel>}.
 *
 * @param <SOURCE> the source object should of type OrderCancelRecordEntryModel
 * @param <TARGET> the target object should of type SAPCpiOutboundOrderCancellationModel
 */

public interface SapCpiOrderCancellationMapperService<SOURCE extends OrderCancelRecordEntryModel, TARGET extends SAPCpiOutboundOrderCancellationModel> {

  /**
   *  Performs mapping from source to target.
   *
   * @param source Order Cancel Record Entry Model
   * @param target SAP CPI Outbound Order Cancellation Model
   */
  void map(SOURCE source, List<TARGET> target);

}

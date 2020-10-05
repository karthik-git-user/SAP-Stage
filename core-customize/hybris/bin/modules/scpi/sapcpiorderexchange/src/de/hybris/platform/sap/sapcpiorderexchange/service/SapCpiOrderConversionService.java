/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;

import java.util.List;

/**
 * SapCpiOrderConversionService
 */
public interface SapCpiOrderConversionService {

    /**
     * convertOrderToSapCpiOrder
     * @param orderModel OrderModel
     * @return SapCpiOrder
     */
    SapCpiOrder convertOrderToSapCpiOrder(OrderModel orderModel);

    /**
     * convertCancelOrderToSapCpiCancelOrder
     * @param orderCancelRecordEntryModel OrderCancelRecordEntryModel
     * @return List<SapCpiOrderCancellation>
     */
    List<SapCpiOrderCancellation> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel);
}

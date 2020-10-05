/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegrationoms.inbound.helper.impl;

import com.sap.hybris.sapquoteintegration.constants.SapquoteintegrationConstants;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundOrderHelper;

public class DefaultInboundQuoteOmsOrderHelper extends SapOmsDataHubInboundOrderHelper{

    @Override
    public void processOrderConfirmationFromHub(String orderNumber) {
        super.processOrderConfirmationFromHub(orderNumber);
        SAPOrderModel sapOrder = readSapOrder(orderNumber);
        OrderModel order = sapOrder.getOrder();
        if (order != null && order.getQuoteReference() != null && order.getCode().equals(order.getQuoteReference().getOrderCode()))
        {
                final String eventName = SapquoteintegrationConstants.ERP_ORDERCONFIRMATION_EVENT_FOR_QUOTE + order.getCode();
                getBusinessProcessService().triggerEvent(eventName);
        }
    }
}

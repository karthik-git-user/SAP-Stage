/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.impl.SapRevenueCloudOrderConversionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DefaultSapCpqSbOrderConversionService extends SapRevenueCloudOrderConversionService {

    @Override
    public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel) {
        SAPCpiOutboundOrderModel sapCpiOutboundOrder = super.convertOrderToSapCpiOrder(orderModel);
        populateSapCpiOutboundOrderModel(sapCpiOutboundOrder, orderModel);
        return sapCpiOutboundOrder;
    }

    protected void populateSapCpiOutboundOrderModel(SAPCpiOutboundOrderModel sapCpiOutboundOrder, OrderModel order) {


//        Set<SAPCpiOutboundOrderItemModel> items = new HashSet<>();

        sapCpiOutboundOrder.getSapCpiOutboundOrderItems().forEach( item -> {
            order.getEntries().forEach(entry -> {
                //Setting flag OverwriteContractTerms=TRUE, contractStartDate=CURRENT_DATE
                if ( (!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails())) && item.getProductCode() == entry.getProduct().getSubscriptionCode()) {
                        entry.getCpqSubscriptionDetails().forEach(model -> {
                        if (model.getContractStartDate() != null) {
                            item.setSubscriptionValidFrom(SapRevenueCloudSubscriptionUtil.dateToString(model.getContractStartDate()));
                        }
                        if(model.getContractLength().equals("0")){
                            model.setContractLength(null);
                        }

                        if(model.getContractLength() != null || model.getMinimumContractLength() != null){
                            item.setOverwriteContractTerm("true");
                        }
                    });

                    item.setPricePlanId(null);// Need to pass either subscription price plan r Cpq rateplan
                    item.setCpqSubscriptionDetails(entry.getCpqSubscriptionDetails());
                }
                if(entry.getProduct().getCode() != null){
                    item.setProductName(entry.getProduct().getCode());
                    item.setProductCode(null);//Need to send eith Id or code
                }
            });
        });
    }
}

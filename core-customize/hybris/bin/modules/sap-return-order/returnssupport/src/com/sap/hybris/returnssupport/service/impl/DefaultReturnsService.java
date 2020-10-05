/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.returnssupport.service.impl;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.impl.DefaultReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import java.util.function.Predicate;

public class DefaultReturnsService extends DefaultReturnService {
	public ReturnRequestModel createReturnRequest(OrderModel order) {
		this.validateReturnRequest(order);
		ReturnRequestModel returnRequest = super.createReturnRequest(order);
		returnRequest.setStatus(ReturnStatus.APPROVAL_PENDING);
		this.createRMA(returnRequest);
		return returnRequest;
	}
	

	public RefundEntryModel createRefund(ReturnRequestModel request, AbstractOrderEntryModel entry, String notes,
			Long expectedQuantity, ReturnAction action, RefundReason reason) {
		this.validateRefund(request, (OrderEntryModel) entry, notes, expectedQuantity, action, reason);
		RefundEntryModel refundEntry = super.createRefund(request,entry,notes,expectedQuantity,action,reason);
		
			refundEntry.getReturnRequest().setStatus(ReturnStatus.APPROVAL_PENDING);
			refundEntry.setStatus(ReturnStatus.APPROVAL_PENDING);
			refundEntry.setReceivedQuantity(Long.valueOf(0L));
	
		this.getModelService().save(refundEntry);
		this.getModelService().save(refundEntry.getReturnRequest());
		return refundEntry;
	}

	protected void validateReturnRequest(OrderModel order) {
		ServicesUtil.validateParameterNotNull(order, "Parameter order cannot be null");
	}


	protected void validateRefund(ReturnRequestModel request, OrderEntryModel entry, String notes,
			Long expectedQuantity, ReturnAction action, RefundReason reason) {
		ServicesUtil.validateParameterNotNull(request, "Parameter request cannot be null");
		ServicesUtil.validateParameterNotNull(entry, "Parameter entry cannot be null");
		ServicesUtil.validateParameterNotNull(expectedQuantity, "Parameter expectedQuantity cannot be null");
		ServicesUtil.validateParameterNotNull(action, "Parameter action cannot be null");
		ServicesUtil.validateParameterNotNull(reason, "Parameter reason cannot be null");
		if (expectedQuantity.longValue() <= 0L) {
			throw new IllegalArgumentException("Expected quantity must be above 0");
		} else {
			OrderModel order = request.getOrder();
			if (!order.equals(entry.getOrder())) {
				throw new IllegalArgumentException("Order entry is not part of the order");
			} else if (!this.isReturnable(order, entry, expectedQuantity.longValue())) {
				throw new IllegalArgumentException("Item is not returnable for this quantity");
			}
		}
	}


}
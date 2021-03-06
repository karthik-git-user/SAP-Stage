/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sapomsreturnprocess.returns.strategy;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class ReturnSourcingContext
{

	private ReturnOrderStartegy returnOrderStrategy;


	public void splitConsignment(final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap)
	{
		returnOrderStrategy.splitOrder(returnEntryConsignmentListMap);
	}

	@Required
	public void setReturnOrderStrategy(final ReturnOrderStartegy returnOrderStrategy)
	{
		this.returnOrderStrategy = returnOrderStrategy;
	}



}

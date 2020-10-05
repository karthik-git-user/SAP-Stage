/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.service;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;

import java.util.Set;


/**
 * SapCpiOmsReturnsOutboundConversionService
 */
public interface SapCpiOmsReturnsOutboundConversionService
{
	/**
	 * convertReturnOrderToSapCpiOutboundReturnOrder
	 *
	 * @param clonedReturnModel ReturnRequestModel
	 * @param consignments          Set<ConsignmentEntryModel>
	 * @return SAPCpiOutboundReturnOrderModel
	 */
	SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel clonedReturnModel,
			Set<ConsignmentEntryModel> consignments);

	/**
	 * convertCancelReturnOrderToSapCpiOutboundReturnOrder
	 *
	 * @param clonedReturnModel ReturnRequestModel
	 * @param consignments          Set<ConsignmentEntryModel>
	 * @return SAPCpiOutboundReturnOrderModel
	 */
	SAPCpiOutboundReturnOrderModel convertCancelReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel clonedReturnModel,
			Set<ConsignmentEntryModel> consignments);

}

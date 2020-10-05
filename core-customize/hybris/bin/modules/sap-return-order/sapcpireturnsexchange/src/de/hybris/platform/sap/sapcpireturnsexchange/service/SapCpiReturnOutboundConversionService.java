/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpireturnsexchange.service;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;

/**
 * SapCpiReturnOutboundConversionService
 */
public interface SapCpiReturnOutboundConversionService {
	/**
	 * convertReturnOrderToSapCpiOutboundReturnOrder
	 * @param returnRequest ReturnRequestModel
	 * @return SAPCpiOutboundReturnOrderModel
	 */
    SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(ReturnRequestModel returnRequest);

}

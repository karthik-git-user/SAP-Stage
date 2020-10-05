/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;

/**
 * Interface for converting one model to another 
 *
 */
public interface SapCpiServiceOrderOutboundConversionService {

	/**
	 * Converts create service order payload OrderModel to SAPCpiOutboundServiceOrderModel
	 * @param orderModel order
	 * @return SAPCpiOutboundServiceOrderModel outbound service order
	 */
	SAPCpiOutboundServiceOrderModel convertServiceOrderToSapCpiOrder(OrderModel orderModel);
	 
}

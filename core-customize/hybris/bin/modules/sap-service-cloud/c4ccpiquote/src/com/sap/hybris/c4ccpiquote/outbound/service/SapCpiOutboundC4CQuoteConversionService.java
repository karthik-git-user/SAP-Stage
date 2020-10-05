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
package com.sap.hybris.c4ccpiquote.outbound.service;

import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Public interface used to handle conversion operations
 *
 */
public interface SapCpiOutboundC4CQuoteConversionService {

	/**
	 * Convert Quote to SCPI code
	 *
	 * @param quote parameter used to convert it into integration object
	 * @return SAPC4CCpiOutboundQuoteModel
	 *
	 *
	 */
	SAPC4CCpiOutboundQuoteModel convertQuoteToSapCpiQuote(QuoteModel quote);


	/**
	 * Convert Quote to Sales Order Notification
	 *
	 * @param order parameter used to convert it into integration object
	 * @return C4CSalesOrderNotificationModel
	 *
	 *
	 */
	C4CSalesOrderNotificationModel convertQuoteToSalesOrderNotification(QuoteModel order);
}

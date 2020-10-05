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
package com.sap.hybris.c4ccpiquote.outbound.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteConversionService;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteService;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import rx.Observable;


/**
 * Default Implementation of SapCpiOutboundC4CQuoteService
 */
public class DefaultSapCpiOutboundC4CQuoteService implements SapCpiOutboundC4CQuoteService
{

	// Quote Outbound
	private static final String OUTBOUND_QUOTE_OBJECT = "C4COutboundQuote";
	private static final String OUTBOUND_QUOTE_DESTINATION = "scpiSalesQuoteReplication";

	private static final String OUTBOUND_ORDER_NOTIFICATION_OBJECT = "C4COrderNotificationOutbound";
	private static final String OUTBOUND_ORDER_NOTIFICATION_DESTINATION = "scpiSalesOrderNotification";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendQuote(final SAPC4CCpiOutboundQuoteModel sapC4CCpiOutboundQuoteModel)
	{
		return getOutboundServiceFacade().send(sapC4CCpiOutboundQuoteModel, OUTBOUND_QUOTE_OBJECT, OUTBOUND_QUOTE_DESTINATION);
	}

	@Override
	public Observable<ResponseEntity<Map>> sendOrderNotification(
			final C4CSalesOrderNotificationModel c4cSalesOrderNotificationModel)
	{
		return getOutboundServiceFacade().send(c4cSalesOrderNotificationModel, OUTBOUND_ORDER_NOTIFICATION_OBJECT,
				OUTBOUND_ORDER_NOTIFICATION_DESTINATION);
	}

	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	/**
	 * @param outboundServiceFacade
	 *           the outboundServiceFacade to set
	 */
	@Required
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService;

import rx.Observable;


/**
 * Default class to provide Quote Outbound Service
 *
 */
public class DefaultSapCpiOutboundQuoteService implements SapCpiOutboundQuoteService
{

	// Quote Outbound
	private static final String OUTBOUND_QUOTE_OBJECT = "OutboundQuote";
	private static final String OUTBOUND_QUOTE_DESTINATION = "scpiQuoteDestination";
	
	// Quote Status Outbound
	private static final String OUTBOUND_QUOTE_STATUS_OBJECT = "OutboundQuoteStatus";
	private static final String OUTBOUND_QUOTE_STATUS_DESTINATION = "scpiQuoteStatusDestination";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendQuote(final SAPCpiOutboundQuoteModel sapCpiOutboundQuoteModel)
	{
		return getOutboundServiceFacade().send(sapCpiOutboundQuoteModel, OUTBOUND_QUOTE_OBJECT, OUTBOUND_QUOTE_DESTINATION);
	}

	@Override
	public Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCpiOutboundQuoteStatusModel sapCpiOutboundQuoteStatusModel) 
	{
		return getOutboundServiceFacade().send(sapCpiOutboundQuoteStatusModel, OUTBOUND_QUOTE_STATUS_OBJECT, OUTBOUND_QUOTE_STATUS_DESTINATION);
	}
	
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	@Required
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}

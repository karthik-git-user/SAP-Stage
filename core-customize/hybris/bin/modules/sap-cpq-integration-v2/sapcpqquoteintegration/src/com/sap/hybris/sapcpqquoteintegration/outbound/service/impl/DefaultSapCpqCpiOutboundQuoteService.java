/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService;

import rx.Observable;


/**
 * Default Implementation of SapCpqCpiOutboundQuoteService
 */
public class DefaultSapCpqCpiOutboundQuoteService implements SapCpqCpiOutboundQuoteService
{

	// Quote Outbound
	private static final String CPQ_OUTBOUND_QUOTE_OBJECT = "CPQOutboundQuote";
	private static final String CPQ_OUTBOUND_QUOTE_DESTINATION = "scpiCPQQuoteReplication";
	
    // Quote Status Outbound : Need to change the Destination.
    private static final String CPQ_OUTBOUND_QUOTE_STATUS_OBJECT = "CPQOutboundQuoteStatus";
    private static final String CPQ_OUTBOUND_QUOTE_STATUS_DESTINATION = "SAPCPQQuoteStatusDestination";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendQuote(final SAPCPQOutboundQuoteModel sapCPQOutboundQuoteModel)
	{
		return getOutboundServiceFacade().send(sapCPQOutboundQuoteModel, CPQ_OUTBOUND_QUOTE_OBJECT, CPQ_OUTBOUND_QUOTE_DESTINATION);
	}

	@Override
	public Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCPQOutboundQuoteStatusModel sapCPQOutboundQuoteStatusModel) 
	{
		return getOutboundServiceFacade().send(sapCPQOutboundQuoteStatusModel, CPQ_OUTBOUND_QUOTE_STATUS_OBJECT, CPQ_OUTBOUND_QUOTE_STATUS_DESTINATION);
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

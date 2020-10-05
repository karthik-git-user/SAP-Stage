/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Interface to convert QuoteModel to OutboundModels
 *
 */
public interface SapCpiOutboundQuoteConversionService
{

	/**
	 * Converts quotemodel to SAPCpiOutboundQuoteModel 
	 * @param quoteModel QuoteModel Object
	 * @return SAPCpiOutboundQuoteModel
	 */
	SAPCpiOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel);
	
	/**
	 * Converts QuoteModel to SAPCpiOutboundQuoteStatusModel
	 * @param quoteModel QuoteModel Object
	 * @return SAPCpiOutboundQuoteStatusModel
	 */
	SAPCpiOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(final QuoteModel quoteModel);

}

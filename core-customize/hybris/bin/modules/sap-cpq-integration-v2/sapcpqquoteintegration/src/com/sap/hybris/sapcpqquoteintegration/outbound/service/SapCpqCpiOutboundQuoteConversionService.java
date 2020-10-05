/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Interface that provides utility methods for conversion
 */
public interface SapCpqCpiOutboundQuoteConversionService
{

	/**
	 * Converts quote to SAPCPQOutboundQuoteModel to be sent to SCPI
	 * 
	 * @param quoteModel Quote Model 
	 * @return SAPCPQOutboundQuoteModel model to be sent to SCPI
	 */
	SAPCPQOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel);
	
	
	/**
	 * Converts quote to SAPCPQOutboundQuoteStatusModel to be sent to SCPI
	 * 
	 * @param quoteModel  Quote Model 
	 * @return SAPCPQOutboundQuoteStatusModel model to be sent to SCPI
	 */
	SAPCPQOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(final QuoteModel quoteModel);

}

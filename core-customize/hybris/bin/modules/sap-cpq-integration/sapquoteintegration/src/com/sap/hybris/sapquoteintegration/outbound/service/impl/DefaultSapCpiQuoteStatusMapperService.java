/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteStatusMapperService;

import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Default implementation of mapping Quote Status
 *
 */
public class DefaultSapCpiQuoteStatusMapperService
		implements SapCpiQuoteStatusMapperService<QuoteModel, SAPCpiOutboundQuoteStatusModel> {

	@Override
	public void map(QuoteModel quote, SAPCpiOutboundQuoteStatusModel scpiQuoteStatus) {
		mapQuoteStatus(quote, scpiQuoteStatus);
	}

	private void mapQuoteStatus(QuoteModel quote, SAPCpiOutboundQuoteStatusModel scpiQuoteStatus) {

		scpiQuoteStatus.setExternalQuoteId(quote.getExternalQuoteId());
		scpiQuoteStatus.setQuoteId(quote.getCode());
		scpiQuoteStatus.setStatus(quote.getState().toString());
		if (QuoteState.BUYER_ORDERED.equals(quote.getState())) {
			scpiQuoteStatus.setOrderId(quote.getOrderCode());
		}
	}

}

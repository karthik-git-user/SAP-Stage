/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.service;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Interface for Quote Service
 *
 */
public interface SapQuoteService {
	
	/**
	 * Method to get current quote version for externalQuoteId
	 * @param externalQuoteId parameter
	 * @return QuoteModel
	 */
	public QuoteModel getCurrentQuoteForExternalQuoteId(String externalQuoteId);
	
}

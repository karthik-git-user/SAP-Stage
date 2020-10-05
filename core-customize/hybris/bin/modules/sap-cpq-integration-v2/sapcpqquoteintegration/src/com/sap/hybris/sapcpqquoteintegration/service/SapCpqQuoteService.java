/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.service;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Interface that provides utility methods
 */
public interface SapCpqQuoteService
{
	/**
	 * Method that fetches quote based on externalQuoteId
	 * @param externalQuoteId Quote External Id
	 * @return QuoteModel the Quote Model
	 */
	public QuoteModel getCurrentQuoteForExternalQuoteId(String externalQuoteId);
	
	
	/**
	 * Method that fetches base store
	 * @param salesOrganization Sales Organization
	 * @param distributionChannel Distribution Channel
	 * @param division Division
	 * @return Base store name
	 */
	public String getSiteAndStoreFromSalesArea(String salesOrganization, String distributionChannel, String division);

}

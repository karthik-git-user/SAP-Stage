/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegrationaddon.facade;

/**
 * Interface for Quote Facade
 *
 */
public interface SapQuoteFacade
{
	/**
	 * Method to download Quote Proposal Document
	 * @param quoteCode parameter
	 * @return byte[]
	 */
	public byte[] downloadQuoteProposalDocument(String quoteCode);

}

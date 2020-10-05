/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades;

/**
 * Interface that provides an API for actions on Quote
 */
public interface SapCpqQuoteFacade
{

	/**
	 * Makes a call to external system to fetch the document
	 * 
	 * @param quoteCode Quote Code
	 * @return document as a byte array
	 */
	public byte[] downloadQuoteProposalDocument(String quoteCode);

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.clients;

public interface SapCpqQuoteApiClientService {

	byte[] fetchProposalDocument(final String quoteCode);
	
}

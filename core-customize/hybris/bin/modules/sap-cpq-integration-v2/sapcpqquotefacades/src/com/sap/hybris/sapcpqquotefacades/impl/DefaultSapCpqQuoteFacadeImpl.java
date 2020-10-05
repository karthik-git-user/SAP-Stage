/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;

import com.sap.hybris.sapcpqquotefacades.SapCpqQuoteFacade;
import com.sap.hybris.sapcpqquoteintegration.clients.SapCpqQuoteApiClientService;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 *
 */
public class DefaultSapCpqQuoteFacadeImpl implements SapCpqQuoteFacade
{

	private static final Logger logger = Logger.getLogger(DefaultSapCpqQuoteFacadeImpl.class.getName());

	private QuoteService quoteService;

	private UserService userService;
	
	private SapCpqQuoteApiClientService sapCpqQuoteApiClientService;


	@Override
	public byte[] downloadQuoteProposalDocument(final String quoteCode)
	{
		byte[] dataFromMedia = null;
		try
		{
			final QuoteModel currentQuote = getQuoteService().getCurrentQuoteForCode(quoteCode);

			final UserModel currentUser = userService.getCurrentUser();


			if (null != currentQuote && CollectionUtils.isNotEmpty(currentUser.getQuotes())
					&& currentUser.getQuotes().contains(currentQuote))
			{

				dataFromMedia = sapCpqQuoteApiClientService.fetchProposalDocument(quoteCode);
			}

		}
		catch (final RestClientException exp)
		{
			logger.error("Unable to Retrieve the PDF from backend");

		}
		return dataFromMedia;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
	
	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	/**
	 * @param quoteService
	 *           the quoteService to set
	 */
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	public SapCpqQuoteApiClientService getSapCpqQuoteApiClientService() {
		return sapCpqQuoteApiClientService;
	}

	public void setSapCpqQuoteApiClientService(SapCpqQuoteApiClientService sapCpqQuoteApiClientService) {
		this.sapCpqQuoteApiClientService = sapCpqQuoteApiClientService;
	}



}

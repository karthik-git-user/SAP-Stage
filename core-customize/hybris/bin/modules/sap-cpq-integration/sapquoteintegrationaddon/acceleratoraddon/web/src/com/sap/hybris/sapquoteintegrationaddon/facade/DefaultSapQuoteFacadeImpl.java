/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegrationaddon.facade;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;

import com.sap.hybris.sapquoteintegration.exception.DefaultSapQuoteIntegrationException;

/**
 *
 */
public class DefaultSapQuoteFacadeImpl implements SapQuoteFacade {

	private QuoteService quoteService;

	private MediaService mediaService;

	private UserService userService;

	private static final Logger logger = Logger.getLogger(DefaultSapQuoteFacadeImpl.class.getName());

	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService() {
		return quoteService;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService() {
		return mediaService;
	}

	/**
	 * @param mediaService the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService) {
		this.mediaService = mediaService;
	}

	/**
	 * @param quoteService the quoteService to set
	 */
	public void setQuoteService(final QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	@Override
	public byte[] downloadQuoteProposalDocument(final String quoteCode) {

		final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(quoteCode);
		String loggedInUid = userService.getCurrentUser().getUid();
		if (loggedInUid.equals(quote.getUser().getUid())) {
			return (byte[]) quote.getExternalQuoteDocumentBlob();
		} else {
			throw new DefaultSapQuoteIntegrationException("Access Denied to open Proposal Document");
		}

	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.inbound.hook;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.c4ccpiquote.inbound.helper.C4CCpiInboundQuoteHelper;


/**
 *
 */
public class SapCpiSalesQuoteInboundPrePersistHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiSalesQuoteInboundPrePersistHook.class);
	private List<C4CCpiInboundQuoteHelper> salesInboundQuoteHelpers;


	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		LOG.info("Entering SapCpiSalesQuoteInboundPrePersistHook#execute");

		if (item instanceof QuoteModel)
		{
			QuoteModel sapQuoteModel = (QuoteModel) item;

			for (final C4CCpiInboundQuoteHelper salesInboundQuoteHelper : salesInboundQuoteHelpers)
			{
				sapQuoteModel = salesInboundQuoteHelper.processSalesInboundQuote(sapQuoteModel);
			}

			return Optional.of(sapQuoteModel);
		}

		LOG.info("Exiting SapCpiSalesQuoteInboundPrePersistHook#execute");

		return Optional.of(item);
	}



	public List<C4CCpiInboundQuoteHelper> getSalesInboundQuoteHelpers()
	{
		return salesInboundQuoteHelpers;
	}

	@Required
	public void setSalesInboundQuoteHelpers(final List<C4CCpiInboundQuoteHelper> salesInboundQuoteHelpers)
	{
		this.salesInboundQuoteHelpers = salesInboundQuoteHelpers;
	}
}

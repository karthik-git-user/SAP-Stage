/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;
import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;


public class SapCpiInboundQuoteEntryPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiInboundQuoteEntryPersistenceHook.class);
	private List<InboundQuoteEntryHelper> sapInboundQuoteEntryHelpers;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		LOG.info("Entering SapCpiInboundQuoteEntryPersistenceHook#execute");
		if (item instanceof QuoteEntryModel)
		{			
			QuoteEntryModel sapQuoteEntryModel = (QuoteEntryModel) item;
			for (InboundQuoteEntryHelper inboundQuoteEntryHelper : getSapInboundQuoteEntryHelpers()) {
				sapQuoteEntryModel = inboundQuoteEntryHelper.processInboundQuoteEntry(sapQuoteEntryModel);
			}
			LOG.info("Exiting SapCpiInboundQuoteEntryPersistenceHook#execute");
			return Optional.of(sapQuoteEntryModel);
		}
		LOG.info("Exiting SapCpiInboundQuoteEntryPersistenceHook#execute");
		return Optional.of(item);
	}

	public List<InboundQuoteEntryHelper> getSapInboundQuoteEntryHelpers() {
		return sapInboundQuoteEntryHelpers;
	}

	public void setSapInboundQuoteEntryHelpers(List<InboundQuoteEntryHelper> sapInboundQuoteEntryHelpers) {
		this.sapInboundQuoteEntryHelpers = sapInboundQuoteEntryHelpers;
	}


}

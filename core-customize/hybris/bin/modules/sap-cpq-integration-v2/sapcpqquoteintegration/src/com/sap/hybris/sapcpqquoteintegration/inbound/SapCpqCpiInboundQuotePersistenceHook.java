/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqInboundQuoteHelper;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

public class SapCpqCpiInboundQuotePersistenceHook implements PrePersistHook {

	private static final Logger LOG = LoggerFactory.getLogger(SapCpqCpiInboundQuotePersistenceHook.class);
	private List<CpqInboundQuoteHelper> sapCpqInboundQuoteHelpers;
	
	@Override
	public Optional<ItemModel> execute(ItemModel item) {

		LOG.info("Entering SapCpqCpiInboundQuotePersistenceHook#execute");
		if (item instanceof QuoteModel)
		{
			QuoteModel sapQuoteModel = (QuoteModel) item;
			for (CpqInboundQuoteHelper inboundQuoteHelper : sapCpqInboundQuoteHelpers) {
				sapQuoteModel = inboundQuoteHelper.processInboundQuote(sapQuoteModel);
			}
			return Optional.of(sapQuoteModel);
		}
		LOG.info("Exiting SapCpqCpiInboundQuotePersistenceHook#execute");
		
		return Optional.of(item);
	}

	public List<CpqInboundQuoteHelper> getSapCpqInboundQuoteHelpers() {
		return sapCpqInboundQuoteHelpers;
	}

	public void setSapCpqInboundQuoteHelpers(List<CpqInboundQuoteHelper> sapCpqInboundQuoteHelpers) {
		this.sapCpqInboundQuoteHelpers = sapCpqInboundQuoteHelpers;
	}

	
	
	
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqQuoteHelper;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;

public class SapCpiCpqQuotePostPersistenceHook implements PostPersistHook {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(SapCpiCpqQuotePostPersistenceHook.class);
	private List<CpqQuoteHelper> sapCpqQuoteHelpers;
	
	@Override
	public void execute(ItemModel item) {

		LOG.info("Entering SapCpiCpqQuotePostPersistenceHook#execute");
		if (item instanceof QuoteModel) {
			
			QuoteModel sapQuoteModel = (QuoteModel) item;
			for (CpqQuoteHelper quoteHelper : sapCpqQuoteHelpers) {
				sapQuoteModel = quoteHelper.performQuoteOperation(sapQuoteModel);
			}
			
		}
		LOG.info("Exiting SapCpiCpqQuotePostPersistenceHook#execute");
	}

	public List<CpqQuoteHelper> getSapCpqQuoteHelpers() {
		return sapCpqQuoteHelpers;
	}

	public void setSapCpqQuoteHelpers(List<CpqQuoteHelper> sapCpqQuoteHelpers) {
		this.sapCpqQuoteHelpers = sapCpqQuoteHelpers;
	}
	
	

}

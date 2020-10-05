/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.sapquoteintegration.exception.DefaultSapQuoteIntegrationException;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation.
 */
public class DefaultSapQuoteService implements SapQuoteService {

	protected static final String QUOTES_QUERY = "SELECT {quote:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
			+ " as quote}";
	protected static final String WHERE_CODE_CLAUSE = " WHERE {quote:" + QuoteModel.CODE + "}=?code";
	protected static final String ORDER_BY_VERSION_DESC = " ORDER BY {quote:" + QuoteModel.VERSION + "} DESC";

	private FlexibleSearchService flexibleSearchService;

	public QuoteModel getCurrentQuoteForExternalQuoteId(final String externalQuoteId) {
		validateParameterNotNullStandardMessage("externalQuoteId", externalQuoteId);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(
				QUOTES_QUERY + WHERE_CODE_CLAUSE + ORDER_BY_VERSION_DESC);
		searchQuery.addQueryParameter("externalQuoteId", externalQuoteId);
		searchQuery.setCount(1);

		return getFlexibleSearchService().searchUnique(searchQuery);
	}


	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

}

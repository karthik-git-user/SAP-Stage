/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqquoteintegration.exception.DefaultSapCpqQuoteIntegrationException;
import com.sap.hybris.sapcpqquoteintegration.service.SapCpqQuoteService;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default Implementation of SapCpqQuoteService
 */
public class DefaultSapCpqQuoteService implements SapCpqQuoteService
{
	private FlexibleSearchService flexibleSearchService;
	
	protected static final String SALES_AREA_QUERY = "SELECT {bsc:" + SAPConfigurationModel.PK + "} FROM {"
			+ SAPConfigurationModel._TYPECODE + " as bsc}";
	protected static final String SALES_AREA_WHERE_CLAUSE = " WHERE {bsc:"
			+ SAPConfigurationModel.SAPCOMMON_SALESORGANIZATION + "}=?salesOrg  AND {bsc:"
			+ SAPConfigurationModel.SAPCOMMON_DISTRIBUTIONCHANNEL + "}=?distChannel AND {bsc:"
			+ SAPConfigurationModel.SAPCOMMON_DIVISION + "}=?division";

	protected static final String QUOTES_QUERY = "SELECT {quote:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
			+ " as quote}";
	protected static final String WHERE_CODE_CLAUSE = " WHERE {quote:" + QuoteModel.CODE + "}=?code";
	protected static final String ORDER_BY_VERSION_DESC = " ORDER BY {quote:" + QuoteModel.VERSION + "} DESC";


	@Override
	public QuoteModel getCurrentQuoteForExternalQuoteId(String externalQuoteId) {
		
		validateParameterNotNullStandardMessage("externalQuoteId", externalQuoteId);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(
				QUOTES_QUERY + WHERE_CODE_CLAUSE + ORDER_BY_VERSION_DESC);
		searchQuery.addQueryParameter("externalQuoteId", externalQuoteId);
		searchQuery.setCount(1);

		return getFlexibleSearchService().searchUnique(searchQuery);
	}

	@Override
	public String getSiteAndStoreFromSalesArea(String salesOrganization, String distributionChannel, String division) {
		
		List<BaseStoreModel> baseStores;
		try {
			final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(SALES_AREA_QUERY + SALES_AREA_WHERE_CLAUSE);
			searchQuery.addQueryParameter("salesOrg", salesOrganization);
			searchQuery.addQueryParameter("distChannel", distributionChannel);
			searchQuery.addQueryParameter("division", division);
			searchQuery.setCount(1);

			SAPConfigurationModel scm = getFlexibleSearchService().searchUnique(searchQuery);
			baseStores = new ArrayList<>(scm.getBaseStores());
		} catch (Exception mnfe) {
			throw (new DefaultSapCpqQuoteIntegrationException("Sales Area not found in Commerce"));
		}

		return baseStores.get(0).getUid();
	}

	
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}
	
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.populator;

import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchTextPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.FreeTextQueryBuilder;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Allows to enhance the list of {@link FreeTextQueryBuilder} of an existing {@link SearchTextPopulator} spring
 * definition.
 */
public class SearchTextPopulatorEnhancer
{

	private static final Logger LOGGER = Logger.getLogger(SearchTextPopulatorEnhancer.class);

	private SearchTextPopulator searchTextPopulator;
	private List<FreeTextQueryBuilder> freeTextQueryBuilders; //NOSONAR

	/**
	 * Injection setter for {@link SearchTextPopulator}.
	 *
	 * @param searchTextPopulator
	 *           the searchTextPopulator to set
	 */
	@Required //NOSONAR
	public void setSearchTextPopulator(final SearchTextPopulator searchTextPopulator)
	{
		this.searchTextPopulator = searchTextPopulator;
	}

	/**
	 * Injection setter for list of {@link FreeTextQueryBuilder} which should be added to the {@link SearchTextPopulator}
	 * .
	 *
	 * @param freeTextQueryBuilders
	 *           list of {@link FreeTextQueryBuilder}
	 */
	@Required //NOSONAR
	public void setFreeTextQueryBuilders(final List<FreeTextQueryBuilder> freeTextQueryBuilders) //NOSONAR
	{
		this.freeTextQueryBuilders = freeTextQueryBuilders;
	}

	/**
	 * Enhances the {@link SearchTextPopulator}.
	 */
	public void enhance()
	{
		final List freeTextQueryBuildersList = searchTextPopulator.getFreeTextQueryBuilders();
		for (final FreeTextQueryBuilder freeTextQueryBuilder : freeTextQueryBuilders) //NOSONAR
		{
			freeTextQueryBuildersList.add(freeTextQueryBuilder);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Adding free text builder " + freeTextQueryBuilder + " commerce search text populator");
			}
		}
		searchTextPopulator.setFreeTextQueryBuilders(freeTextQueryBuildersList);
	}

}

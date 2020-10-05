/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Base class for value resolver tests.
 */
@SuppressWarnings("javadoc")
public class BaseValueResolverTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private LanguageModel languageModelEN;

	@Mock
	private LanguageModel languageModelDE;

	private final Locale localeEN = new Locale("en");

	private final Locale localeDE = new Locale("de");

	@Mock
	private InputDocument inputDocument;

	@Mock
	private IndexerBatchContext batchContext;

	@Mock
	private IndexConfig indexConfig;

	private FacetSearchConfig facetSearchConfig;

	private IndexedType indexedType;

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	protected InputDocument getInputDocument()
	{
		return inputDocument;
	}

	protected IndexerBatchContext getBatchContext()
	{
		return batchContext;
	}

	protected FacetSearchConfig getFacetSearchConfig()
	{
		return facetSearchConfig;
	}

	protected IndexConfig getIndexConfig()
	{
		return indexConfig;
	}

	protected IndexedType getIndexedType()
	{
		return indexedType;
	}

	protected LanguageModel getLanguageModelEN()
	{
		return languageModelEN;
	}

	protected LanguageModel getLanguageModelDE()
	{
		return languageModelDE;
	}

	protected Locale getLocaleEN()
	{
		return localeEN;
	}

	protected Locale getLocaleDE()
	{
		return localeDE;
	}

	protected List<IndexedProperty> getIndexedProperties(final List<String> propertyNames, final boolean multivalue,
			final boolean localized)
	{
		final List<IndexedProperty> indexedProperties = new ArrayList<IndexedProperty>();
		for (final String propertyName : propertyNames)
		{
			final IndexedProperty indexedProperty = new IndexedProperty();
			indexedProperty.setName(propertyName);
			indexedProperty.setValueProviderParameters(new HashMap<String, String>());
			indexedProperty.setMultiValue(multivalue);
			indexedProperty.setLocalized(localized);
			indexedProperties.add(indexedProperty);
		}
		return indexedProperties;
	}

	protected List<IndexedProperty> getIndexedProperties()
	{
		return Collections.EMPTY_LIST;
	}

	@Test
	public void setUpBaseValueResolverTest()
	{
		MockitoAnnotations.initMocks(this);

		facetSearchConfig = new FacetSearchConfig();
		indexedType = new IndexedType();
		facetSearchConfig.setIndexConfig(indexConfig);

		when(batchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
		when(batchContext.getIndexedType()).thenReturn(indexedType);

		final List<LanguageModel> languageModels = Arrays.asList(languageModelEN, languageModelDE);
		when(indexConfig.getLanguages()).thenReturn(languageModels);
		when(commonI18NService.getLocaleForLanguage(languageModelEN)).thenReturn(localeEN);
		when(commonI18NService.getLocaleForLanguage(languageModelDE)).thenReturn(localeDE);
		when(commonI18NService.getCurrentLanguage()).thenReturn(languageModelEN);
	}
}

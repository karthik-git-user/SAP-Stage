/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;


/**
 * Tests the units of class {@link StructuredArticleComponentKeywordsValueResolver}.
 */
@UnitTest
@SuppressWarnings("javadoc")
public class StructuredArticleComponentKeywordsValueResolverTest extends BaseStructuredArticleComponentValueResolverTest
{
	protected static final String KEYWORD_1 = "keyword1";
	protected static final String KEYWORD_2 = "keyword2";
	protected static final String KEYWORD_3 = "keyword3";
	protected static final String KEYWORD_1_DE = "keyword1_de";
	protected static final String KEYWORD_2_DE = "keyword2_de";
	protected static final String KEYWORD_3_DE = "keyword3_de";
	protected static final String KEYWORD_1_EN = "keyword1_en";
	protected static final String KEYWORD_2_EN = "keyword2_en";
	protected static final String KEYWORD_3_EN = "keyword3_en";

	private StructuredArticleComponentKeywordsValueResolver classUnderTest;

	@Mock
	private KeywordModel keyword1;

	@Mock
	private KeywordModel keyword2;

	@Mock
	private KeywordModel keyword3;

	@Mock
	private KeywordModel keyword1EN;

	@Mock
	private KeywordModel keyword2EN;

	@Mock
	private KeywordModel keyword3EN;

	@Mock
	private KeywordModel keyword1DE;

	@Mock
	private KeywordModel keyword2DE;

	@Mock
	private KeywordModel keyword3DE;

	@Before
	public void setUp()
	{
		when(keyword1.getKeyword()).thenReturn(KEYWORD_1);
		when(keyword1.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword2.getKeyword()).thenReturn(KEYWORD_2);
		when(keyword2.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword3.getKeyword()).thenReturn(KEYWORD_3);
		when(keyword3.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword1EN.getKeyword()).thenReturn(KEYWORD_1_EN);
		when(keyword1EN.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword2EN.getKeyword()).thenReturn(KEYWORD_2_EN);
		when(keyword2EN.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword3EN.getKeyword()).thenReturn(KEYWORD_3_EN);
		when(keyword3EN.getLanguage()).thenReturn(getLanguageModelEN());
		when(keyword1DE.getKeyword()).thenReturn(KEYWORD_1_DE);
		when(keyword1DE.getLanguage()).thenReturn(getLanguageModelDE());
		when(keyword2DE.getKeyword()).thenReturn(KEYWORD_2_DE);
		when(keyword2DE.getLanguage()).thenReturn(getLanguageModelDE());
		when(keyword3DE.getKeyword()).thenReturn(KEYWORD_3_DE);
		when(keyword3DE.getLanguage()).thenReturn(getLanguageModelDE());

		when(componentProduct1.getKeywords()).thenReturn(Arrays.asList(keyword1, keyword2));
		when(componentProduct1.getKeywords(getLocaleEN())).thenReturn(Arrays.asList(keyword1EN, keyword2EN));
		when(componentProduct1.getKeywords(getLocaleDE())).thenReturn(Arrays.asList(keyword1DE, keyword2DE));

		when(componentProduct2.getKeywords()).thenReturn(Arrays.asList(keyword3));
		when(componentProduct2.getKeywords(getLocaleEN())).thenReturn(Arrays.asList(keyword3EN));
		when(componentProduct2.getKeywords(getLocaleDE())).thenReturn(Arrays.asList(keyword3DE));

		classUnderTest = new StructuredArticleComponentKeywordsValueResolver();
		classUnderTest.setCommonI18NService(getCommonI18NService());
	}

	@Test
	public void resolveNoKeyword() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties();

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
	}

	@Test
	public void resolveNoKeywordForNotStructuredArticle() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentKeywords"), true, false);
		when(product.getStructuredArticleType()).thenReturn(null);

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
	}

	@Test
	public void resolveKeywordsMultivalueNoLocale() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentKeywords"), true, false);

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		final IndexedProperty[] indexedPropertiesArray = indexedProperties.toArray(new IndexedProperty[indexedProperties.size()]);
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword1.getKeyword() + " " + keyword2.getKeyword(), null);
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword3.getKeyword(), null);
	}

	@Test
	public void resolveKeywordsSinglevalueNoLocale() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentKeywords"), false, false);

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		final IndexedProperty[] indexedPropertiesArray = indexedProperties.toArray(new IndexedProperty[indexedProperties.size()]);
		verify(getInputDocument()).addField(indexedPropertiesArray[0],
				keyword1.getKeyword() + " " + keyword2.getKeyword() + " " + keyword3.getKeyword(), null);
	}

	@Test
	public void resolveKeywordsMultivalueLocale() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentKeywords"), true, true);

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		final IndexedProperty[] indexedPropertiesArray = indexedProperties.toArray(new IndexedProperty[indexedProperties.size()]);
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword1EN.getKeyword() + " " + keyword2EN.getKeyword(),
				getLanguageModelEN().getIsocode());
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword1DE.getKeyword() + " " + keyword2DE.getKeyword(),
				getLanguageModelDE().getIsocode());
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword3EN.getKeyword(), getLanguageModelEN().getIsocode());
		verify(getInputDocument()).addField(indexedPropertiesArray[0], keyword3DE.getKeyword(), getLanguageModelDE().getIsocode());
		verify(getInputDocument(), VerificationModeFactory.times(0)).addField(indexedPropertiesArray[0], "",
				getLanguageModelEN().getIsocode());
		verify(getInputDocument(), VerificationModeFactory.times(0)).addField(indexedPropertiesArray[0], "",
				getLanguageModelDE().getIsocode());
	}

	@Test
	public void resolveKeywordsSinglevalueLocale() throws Exception
	{
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentKeywords"), false, true);

		// when
		classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		// then
		final IndexedProperty[] indexedPropertiesArray = indexedProperties.toArray(new IndexedProperty[indexedProperties.size()]);
		verify(getInputDocument()).addField(indexedPropertiesArray[0],
				keyword1EN.getKeyword() + " " + keyword2EN.getKeyword() + " " + keyword3EN.getKeyword(),
				getLanguageModelEN().getIsocode());
		verify(getInputDocument()).addField(indexedPropertiesArray[0],
				keyword1DE.getKeyword() + " " + keyword2DE.getKeyword() + " " + keyword3DE.getKeyword(),
				getLanguageModelDE().getIsocode());
	}

}

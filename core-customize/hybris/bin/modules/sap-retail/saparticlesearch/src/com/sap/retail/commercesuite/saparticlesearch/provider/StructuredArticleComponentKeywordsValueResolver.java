/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;


/**
 * This ValueProvider provides the product keywords of the component articles.
 */
public class StructuredArticleComponentKeywordsValueResolver extends BaseStructuredArticleComponentValueResolver
{
	private static final Logger LOGGER = Logger.getLogger(StructuredArticleComponentKeywordsValueResolver.class);

	@Override
	protected Object getComponentPropertyValue(final ArticleComponentModel articleComponent, final LanguageModel language)
	{
		List<KeywordModel> keywordModels;
		if (language != null)
		{
			keywordModels = articleComponent.getComponent().getKeywords(getCommonI18NService().getLocaleForLanguage(language));
		}
		else
		{
			keywordModels = articleComponent.getComponent().getKeywords();
		}
		final List<String> keywords = new ArrayList<String>();
		for (final KeywordModel keywordModel : keywordModels)
		{
			if (language == null || language.getIsocode().equals(keywordModel.getLanguage().getIsocode()))
			{
				keywords.add(keywordModel.getKeyword());
			}
		}
		return keywords;
	}

	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.ValueResolver;
import de.hybris.platform.solrfacetsearch.provider.impl.ValueProviderParameterUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;


/**
 * This is the base ValueResolver for adding article component data to the structured article.
 */
/**
 *
 */
public abstract class BaseStructuredArticleComponentValueResolver implements ValueResolver<ProductModel>
{

	/**
	 * Optional parameter.
	 */
	public static final String OPTIONAL_PARAM = "optional";

	/**
	 * Optional parameter default value.
	 */
	public static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	/**
	 * Split parameter.
	 */
	public static final String SPLIT_PARAM = "split";

	/**
	 * Split parameter default value.
	 */
	public static final boolean SPLIT_PARAM_DEFAULT_VALUE = true;

	/**
	 * Separator parameter.
	 */
	public static final String SEPARATOR_PARAM = "separator";

	/**
	 * Separator parameter default value.
	 */
	public static final String SEPARATOR_PARAM_DEFAULT_VALUE = " ";

	private CommonI18NService commonI18NService;

	/**
	 * Returns the {@link CommonI18NService}.
	 *
	 * @return {@link CommonI18NService}
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Injection setter for {@link CommonI18NService}.
	 *
	 * @param commonI18NService
	 *           {@link CommonI18NService}
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Override
	public void resolve(final InputDocument document, final IndexerBatchContext batchContext,
			final Collection<IndexedProperty> indexedProperties, final ProductModel model) throws FieldValueProviderException
	{
		if (model.getStructuredArticleType() != null)
		{

			// Index non localized properties
			for (final IndexedProperty indexedProperty : indexedProperties)
			{
				if (!indexedProperty.isLocalized())
				{
					addFieldValues(document, indexedProperty, model, null);
				}
			}

			// Index localized properties
			final LanguageModel currentLanguage = commonI18NService.getCurrentLanguage();
			try
			{
				final FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();
				final IndexConfig indexConfig = facetSearchConfig.getIndexConfig();
				final Collection<LanguageModel> languages = indexConfig.getLanguages();

				for (final LanguageModel language : languages)
				{
					commonI18NService.setCurrentLanguage(language);

					processIndexedProperty(document, indexedProperties, model, language);
				}
			}
			finally
			{
				commonI18NService.setCurrentLanguage(currentLanguage);
			}

		}
	}

	private void processIndexedProperty(final InputDocument document,
			final Collection<IndexedProperty> indexedProperties, final ProductModel model, final LanguageModel language)
			throws FieldValueProviderException {
		for (final IndexedProperty indexedProperty : indexedProperties)
		{
			if (indexedProperty.isLocalized())
			{
				addFieldValues(document, indexedProperty, model, language);
			}
		}
	}

	/**
	 * Adds the field value depending from the configuration (multivalue, localized).
	 *
	 * @param document
	 *           solr document to be enhanced
	 * @param indexedProperty
	 *           indexed property
	 * @param product
	 *           product model
	 * @param language
	 *           language
	 * @throws FieldValueProviderException
	 *            exception if field value cannot be provided
	 */
	protected void addFieldValues(final InputDocument document, final IndexedProperty indexedProperty, final ProductModel product,
			final LanguageModel language) throws FieldValueProviderException
	{
		boolean hasValue = false;
		final String separator = ValueProviderParameterUtils.getString(indexedProperty, SEPARATOR_PARAM,
				SEPARATOR_PARAM_DEFAULT_VALUE);
		if (indexedProperty.isMultiValue())
		{
			final List<Object> componentPropertyValues = getComponentPropertyValues(product, language, separator);
			for (final Object componentPropertyValue : componentPropertyValues)
			{
				hasValue = addPropertyValueToDocument(document, indexedProperty, language, product, componentPropertyValue);
			}
		}
		else
		{
			final String componentPropertyValue = getComponentPropertyValuesAsString(product, language, separator);
			hasValue = addPropertyValueToDocument(document, indexedProperty, language, product, componentPropertyValue);
		}
		if (!hasValue)
		{
			final boolean isOptional = ValueProviderParameterUtils.getBoolean(indexedProperty, OPTIONAL_PARAM,
					OPTIONAL_PARAM_DEFAULT_VALUE);
			if (!isOptional)
			{
				getLogger().error("No value resolved for indexed property " + indexedProperty.getName());
			}
		}
	}

	/**
	 * Add the component property value to product solr index document.
	 *
	 * @param document
	 *           solr document
	 * @param indexedProperty
	 *           indexed property
	 * @param language
	 *           language
	 * @param componentPropertyValue
	 *           component property value
	 * @return true, if value has been added
	 * @throws FieldValueProviderException
	 *            exception if field value cannot be provided
	 */
	private boolean addPropertyValueToDocument(final InputDocument document, final IndexedProperty indexedProperty,
			final LanguageModel language, final ProductModel product, final Object componentPropertyValue)
			throws FieldValueProviderException
	{
		if (componentPropertyValue != null && !componentPropertyValue.toString().isEmpty())
		{
			document.addField(indexedProperty, componentPropertyValue, language == null ? null : language.getIsocode());
			if (getLogger().isDebugEnabled())
			{
				String debugString = "Fill Solr index for product " + product.getCode() + " - property " + indexedProperty.getName()
						+ ": " + componentPropertyValue;
				if (language != null)
				{
					debugString = debugString + " (for language " + language.getIsocode() + ")";
				}
				getLogger().debug(debugString);
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the values of the requested article component properties as string.
	 *
	 * @param product
	 *           product model
	 * @param language
	 *           language
	 * @param separator
	 *           value separator
	 * @return component property values as string
	 */
	protected String getComponentPropertyValuesAsString(final ProductModel product, final LanguageModel language,
			final String separator)
	{
		final List<Object> componentPropertyValues = getComponentPropertyValues(product, language, separator);
		return convertListToString(componentPropertyValues, separator);
	}

	/**
	 * Returns the values of the requested article component properties.
	 *
	 * @param product
	 *           product model
	 * @param language
	 *           language
	 * @param separator
	 *           value separator
	 * @return component property values as string
	 */
	protected List<Object> getComponentPropertyValues(final ProductModel product, final LanguageModel language,
			final String separator)
	{
		final Collection<ArticleComponentModel> articleComponents = product.getComponent();
		final List<Object> propertyValues = new ArrayList<Object>(articleComponents.size());
		for (final ArticleComponentModel articleComponent : articleComponents)
		{
			Object propertyValue = getComponentPropertyValue(articleComponent, language);
			if (propertyValue instanceof List)
			{
				propertyValue = convertListToString((List<Object>) propertyValue, separator);
			}
			if (propertyValue != null)
			{
				propertyValues.add(propertyValue);
			}
		}
		return propertyValues;
	}

	/**
	 * Converts a list to a string.
	 *
	 * @param listValues
	 *           values as list
	 * @param separator
	 *           value separator
	 * @return values as string
	 */
	private String convertListToString(final List<Object> listValues, final String separator)
	{
		final StringBuilder stringBuffer = new StringBuilder();
		for (final Object value : listValues)
		{
			if (value == null || value.toString().isEmpty())
			{
				continue;
			}
			final String stringValue = value.toString();
			if (stringBuffer.length() > 0)
			{
				stringBuffer.append(separator);
			}
			stringBuffer.append(stringValue);
		}
		return stringBuffer.length() > 0 ? stringBuffer.toString() : null;
	}

	/**
	 * Gets the property value of the article component.
	 *
	 * @param articleComponent
	 *           article component model
	 * @param language
	 *           language model
	 * @return property value
	 */
	protected abstract Object getComponentPropertyValue(ArticleComponentModel articleComponent, final LanguageModel language);

	/**
	 * Gets the logger of the current implementation class.
	 *
	 * @return logger
	 */
	protected abstract Logger getLogger();

}

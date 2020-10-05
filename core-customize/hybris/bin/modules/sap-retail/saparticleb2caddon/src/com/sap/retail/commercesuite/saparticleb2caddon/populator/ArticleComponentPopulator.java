/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticleb2caddon.populator;

import java.util.ArrayList;
import java.util.List;

import com.sap.retail.commercesuite.saparticleb2caddon.articlecomponent.data.ArticleComponentData;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Populator to enrich product data with article component data.
 *
 * The populator class retrieves for articles that are structured articles their article components. For each article
 * component an article component data object is created that contains the component product, the quantity and the unit
 * of measure for the component. The determined product model of the component article is converted by the product
 * converter instance to get a full product data object of the component article.
 * 
 * @param <SOURCE>
 *           the product model
 * @param <TARGET>
 *           the product data
 */
public class ArticleComponentPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private Converter<ProductModel, ProductData> productConverter;
	private ArticleComponentService articleComponentService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SOURCE productModel, final TARGET productData)
	{

		// Do this only for articles that are structured articles
		// i.e. the structured article type must be not empty
		if (productModel.getStructuredArticleType() != null && !productModel.getStructuredArticleType().toString().isEmpty())
		{
			final List<ArticleComponentModel> articleComponentModels = articleComponentService
					.getComponentsOfStructuredArticle(productModel);

			final List<ArticleComponentData> articleComponents = new ArrayList<ArticleComponentData>();

			for (final ArticleComponentModel articleComponentModel : articleComponentModels)
			{
				final ArticleComponentData articleComponent = new ArticleComponentData();
				articleComponent.setComponent(productConverter.convert(articleComponentModel.getComponent()));
				articleComponent.setQuantity(articleComponentModel.getQuantity());
				articleComponent.setUnit(articleComponentModel.getUnit().getName());
				articleComponents.add(articleComponent);
			}

			productData.setArticleComponents(articleComponents);
		}
	}

	/**
	 * Sets the article component service to the actual article component service instance.
	 *
	 * @param articleComponentService
	 *           the article component service to use
	 */
	public void setArticleComponentService(final ArticleComponentService articleComponentService)
	{
		this.articleComponentService = articleComponentService;
	}

	/**
	 * Sets the product converter to the actual product converter instance.
	 *
	 * @param productConverter
	 *           the product converter instance to use
	 */
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}
}

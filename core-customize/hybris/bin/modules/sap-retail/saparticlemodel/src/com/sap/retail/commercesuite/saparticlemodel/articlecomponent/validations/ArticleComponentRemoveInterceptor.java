/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.validations;

import java.util.List;

import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.util.localization.Localization;


/**
 * Perform product component validations if a product is removed.
 *
 */
public class ArticleComponentRemoveInterceptor implements RemoveInterceptor<ProductModel>
{

	/**
	 * The article component service
	 */
	private ArticleComponentService articleComponentService;


	/**
	 * Set the article component service bean.
	 *
	 * @param articleComponentService
	 *           the articleComponentService to set
	 */
	public void setArticleComponentService(final ArticleComponentService articleComponentService)
	{
		this.articleComponentService = articleComponentService;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onRemove(final ProductModel product, final InterceptorContext context) throws InterceptorException
	{

			// Check if the product that shall be removed is currently part of
			// at least one component list of a structured product
			if (product != null && articleComponentService.isStructuredArticleComponent(product))
			{
				// if it is a component of a structured article the article cannot be removed
				// before the component entry is removed => throw an exception to
				// stop removal. Additionally determine the structured articles for the
				// exception message text
				final StringBuilder msgBuf = new StringBuilder();
				final List<ProductModel> structuredArticles = articleComponentService.getStructuredArticlesOfComponent(product);
				int i = 0;
				for (final ProductModel structuredArticle : structuredArticles)
				{
					i++;
					msgBuf.append(structuredArticle.getCode());
					if (i < structuredArticles.size())
					{
						msgBuf.append(", ");
					}
				}

				throw new InterceptorException(Localization.getLocalizedString("validation.ArticleComponent.isComponent",
						new Object[]
						{ msgBuf }));
			}
		}
	

}

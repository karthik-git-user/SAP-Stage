/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.impl;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;


/**
 * Service implementation to get product component data.
 *
 */
/**
 * 
 */
public class ArticleComponentServiceImpl implements ArticleComponentService
{

	private ArticleComponentDAO articleComponentDAO;

	/**
	 * Setter for structured article component dao bean.
	 *
	 * @param articleComponentDAO
	 *           the articleComponentDAO to set
	 */
	public void setArticleComponentDAO(final ArticleComponentDAO articleComponentDAO)
	{
		this.articleComponentDAO = articleComponentDAO;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getComponentsOfStructuredArticle(de.hybris
	 * .platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ArticleComponentModel> getComponentsOfStructuredArticle(final ProductModel product)
	{
		final List<ArticleComponentModel> articleComponents = articleComponentDAO.findComponentsOfStructuredArticle(product);
		return articleComponents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getComponentsOfStructuredArticle(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ArticleComponentModel> getComponentsOfStructuredArticle(final String productCode, final String catalog,
			final String catalogVersion)
	{
		final List<ArticleComponentModel> articleComponents = articleComponentDAO.findComponentsOfStructuredArticle(productCode,
				catalog, catalogVersion);
		return articleComponents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getStructuredArticlesOfComponent(de.hybris
	 * .platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ProductModel> getStructuredArticlesOfComponent(final ProductModel componentProduct)
	{
		final List<ProductModel> structuredProducts = articleComponentDAO.findStructuredArticlesOfComponent(componentProduct);
		return structuredProducts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getStructuredArticlesOfComponent(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public java.util.List<ProductModel> getStructuredArticlesOfComponent(String componentArticleCode, String catalog,
			String catalogVersion)
	{
		List<ProductModel> structuredArticles = articleComponentDAO.findStructuredArticlesOfComponent(componentArticleCode,
				catalog, catalogVersion);
		return structuredArticles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#isStructuredArticleComponent(de.hybris.
	 * platform.core.model.product.ProductModel)
	 */
	@Override
	public boolean isStructuredArticleComponent(final ProductModel componentProduct)
	{
		return articleComponentDAO.isComponentArticle(componentProduct);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#isStructuredArticleComponent(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isStructuredArticleComponent(final String componentProductCode, final String catalog,
			final String catalogVersion)
	{
		return articleComponentDAO.isComponentArticle(componentProductCode, catalog, catalogVersion);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getComponentArticlesOfStructuredArticles
	 * (de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ProductModel> getComponentArticlesOfStructuredArticles(final ProductModel product)
	{
		return articleComponentDAO.findComponentArticlesOfStructuredArticle(product);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService#getComponentArticlesOfStructuredArticles
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ProductModel> getComponentArticlesOfStructuredArticles(final String productCode, final String catalog,
			final String catalogVersion)
	{
		return articleComponentDAO.findComponentArticlesOfStructuredArticle(productCode, catalog, catalogVersion);
	}

}

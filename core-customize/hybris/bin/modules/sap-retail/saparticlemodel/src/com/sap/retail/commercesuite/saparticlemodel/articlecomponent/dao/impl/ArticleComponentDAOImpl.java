/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * Data access object for structured article components.
 *
 */
public class ArticleComponentDAOImpl extends AbstractItemDao implements ArticleComponentDAO
{

	private static final String PRODUCT = "product";
	private static final String ARTICLE_COMPONENT = "ArticleComponent";
	private ProductService productService;
	private CatalogVersionService catalogVersionService;

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}


	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findComponentsOfStructuredArticle(de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ArticleComponentModel> findComponentsOfStructuredArticle(final ProductModel product)
	{
		final String queryString = String.format("SELECT {%s} FROM {%s} WHERE {%s} = ?product", ArticleComponentModel.PK,
				ARTICLE_COMPONENT, ArticleComponentModel.STRUCTUREDARTICLE);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter(PRODUCT, product);
		final SearchResult<ArticleComponentModel> result = search(query);
		return result.getResult();
	}


	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findComponentsOfStructuredArticle(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ArticleComponentModel> findComponentsOfStructuredArticle(final String productCode, final String catalog,
			final String catalogVersion)
	{
		final ProductModel product = getArticleModel(productCode, catalog, catalogVersion);
		if (product != null)
		{
			return findComponentsOfStructuredArticle(product);
		}

		return new ArrayList<ArticleComponentModel>();
	}

	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findStructuredArticlesOfComponent(de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ProductModel> findStructuredArticlesOfComponent(final ProductModel componentArticle)
	{
		final String queryString = String.format("SELECT {%s} FROM {%s} WHERE {%s} = ?product", ArticleComponentModel.PK,
				ARTICLE_COMPONENT, ArticleComponentModel.COMPONENT);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter(PRODUCT, componentArticle);
		final SearchResult<ArticleComponentModel> result = search(query);
		return getStructuredArticles(result.getResult());
	}

	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findStructuredArticlesOfComponent(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ProductModel> findStructuredArticlesOfComponent(final String componentProductCode, final String catalog,
			final String catalogVersion)
	{
		final ProductModel componentProduct = getArticleModel(componentProductCode, catalog, catalogVersion);
		if (componentProduct != null)
		{
			return findStructuredArticlesOfComponent(componentProduct);
		}

		return new ArrayList<ProductModel>();
	}

	/**
	 * Determine the model for the given product code, catalog and catalog version.
	 *
	 * @param articleCode
	 *           product code of product
	 * @param catalog
	 *           catalog id of the catalog in which the product resides
	 * @param catalogVersion
	 *           catalog version of the catalog in which the product resides
	 * @return the article with the given code, catalog ID and catalog version
	 */
	private ProductModel getArticleModel(final String articleCode, final String catalog, final String catalogVersion)
	{
		ProductModel articleModel = null;

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(catalog, catalogVersion);
		if (catalogVersionModel != null)
		{
			articleModel = productService.getProductForCode(catalogVersionModel, articleCode);
		}

		return articleModel;
	}


	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#isComponentArticle(de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public boolean isComponentArticle(final ProductModel componentProduct)
	{
		final String queryString = String.format("SELECT count({%s}) FROM {%s} WHERE {%s} = ?product", ArticleComponentModel.PK,
				ARTICLE_COMPONENT, ArticleComponentModel.COMPONENT);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.setResultClassList(Arrays.asList(Integer.class));
		query.addQueryParameter(PRODUCT, componentProduct);
		final SearchResult<Integer> result = search(query);
		return Integer.parseInt(result.getResult().iterator().next().toString()) > 0;
	}


	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#isComponentArticle(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isComponentArticle(final String componentProductCode, final String catalog, final String catalogVersion)
	{
		final ProductModel componentProduct = getArticleModel(componentProductCode, catalog, catalogVersion);
		if (componentProduct != null)
		{
			return isComponentArticle(componentProduct);
		}

		return false;
	}



	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findComponentArticlesOfStructuredArticle(de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ProductModel> findComponentArticlesOfStructuredArticle(final ProductModel product)
	{
		final List<ArticleComponentModel> articleComponents = findComponentsOfStructuredArticle(product);
		return getComponentArticles(articleComponents);

	}



	/* (non-Javadoc)
	 * @see com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO#findComponentArticlesOfStructuredArticle(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ProductModel> findComponentArticlesOfStructuredArticle(final String articleCode, final String catalog, final String catalogVersion)
	{
		final List<ArticleComponentModel> structuredArticleComponents = findComponentsOfStructuredArticle(articleCode, catalog, catalogVersion);
		return getComponentArticles(structuredArticleComponents);

	}

	/**
	 * Get the models of the structured articles of the given components.
	 *
	 * @param structuredArticleComponents
	 *
	 * @return list of the component product models
	 */
	private List<ProductModel> getComponentArticles(final List<ArticleComponentModel> structuredArticleComponents)
	{
		if (!structuredArticleComponents.isEmpty())
		{
			final List<ProductModel> componentArticles = new ArrayList<ProductModel>();
			for (final ArticleComponentModel currentComponent : structuredArticleComponents)
			{
				componentArticles.add(currentComponent.getComponent());
			}
			return componentArticles;
		}

		return new ArrayList<ProductModel>();
	}

	/**
	 * Get the models of the structured articles of the given components.
	 *
	 * @param articleComponents
	 *
	 * @return list of the structured product models
	 */
	private List<ProductModel> getStructuredArticles(final List<ArticleComponentModel> articleComponents)
	{
		if (!articleComponents.isEmpty())
		{
			final List<ProductModel> articles = new ArrayList<ProductModel>();
			for (final ArticleComponentModel structuredArticleComponent : articleComponents)
			{
				articles.add(structuredArticleComponent.getStructuredArticle());
			}
			return articles;
		}

		return new ArrayList<ProductModel>();
	}
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;



/**
 * Service interface to get product component related data.
 *
 */
public interface ArticleComponentService
{

	/**
	 * Get article components by product model of the structured article.
	 *
	 * @param article
	 *           the article model
	 * @return article components of given article
	 */
	public List<ArticleComponentModel> getComponentsOfStructuredArticle(ProductModel article);

	/**
	 * Get article components by article code, catalog ID and catalog version.
	 *
	 * @param articleCode
	 *           the article code
	 * @param catalog
	 *           the catalog ID
	 * @param catalogVersion
	 *           the catalog version
	 * @return article components of given article
	 */
	public List<ArticleComponentModel> getComponentsOfStructuredArticle(String articleCode, String catalog, String catalogVersion);

	/**
	 * Get Articles that include the given article as component.
	 *
	 * @param componentArticle
	 *           the component article model
	 * @return the structured articles that contain the component article 
	 */
	public List<ProductModel> getStructuredArticlesOfComponent(ProductModel componentArticle);

	/**
	 * Get Articles that include the given article as component.
	 *
	 * @param componentArticleCode
	 *           the component article code
	 * @param catalog
	 *           the catalog ID
	 * @param catalogVersion
	 *           the catalog version
	 * @return the structured articles that contain the component article
	 */
	public List<ProductModel> getStructuredArticlesOfComponent(String componentArticleCode, String catalog, String catalogVersion);

	/**
	 * Check if a given article is part of a component list.
	 *
	 * @param componentArticle
	 *           the article that shall be checked to be part of a component list
	 * @return true if the article is a component, false if not
	 */
	public boolean isStructuredArticleComponent(ProductModel componentArticle);

	/**
	 * Check if a given article is part of a component list.
	 *
	 * @param componentArticleCode
	 *           the article that shall be checked to be part of a component list
	 * @param catalog
	 *           the catalog id of the catalog the article belongs to
	 * @param catalogVersion
	 *           the catalog version of the catalog
	 * @return true if the article is a component, false if not
	 */
	public boolean isStructuredArticleComponent(String componentArticleCode, String catalog, String catalogVersion);


	/**
	 * Get the component article models of the components that are part of the structured article identified by
	 * its model.
	 *
	 * @param article
	 *           the article model of the structured article
	 * @return list of the article models of the components of the structured article
	 */
	public List<ProductModel> getComponentArticlesOfStructuredArticles(ProductModel article);

	/**
	 * Get the component article models of the components that are part of the structured article identified by
	 * its article code, catalog and catalog version.
	 *
	 * @param articleCode
	 *           the article code of the structured article
	 * @param catalog
	 *           the catalog the structured article belongs to
	 * @param catalogVersion
	 *           the catalog version of the catalog
	 * @return list of the article models of the components of the structured article
	 */
	public List<ProductModel> getComponentArticlesOfStructuredArticles(String articleCode, String catalog, String catalogVersion);
}

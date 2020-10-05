/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao;


import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


/**
 * Data access object for structured article components.
 *
 */
public interface ArticleComponentDAO extends Dao
{
	/**
	 * Find components of the structured article identified by code, catalog and catalog version.
	 *
	 * @param articleCode
	 *           the code of the structured article
	 * @param catalog
	 *           the catalog id of the catalog the article belongs to
	 * @param catalogVersion 
	 *           the catalog version of the catalog
	 *
	 * @return All components of the given article
	 */
	public List<ArticleComponentModel> findComponentsOfStructuredArticle(String articleCode, String catalog, String catalogVersion);

	/**
	 * Find components of the structured article identified by its model.
	 *
	 * @param article
	 *           the structured article
	 *
	 * @return All components of the given article.
	 */
	public List<ArticleComponentModel> findComponentsOfStructuredArticle(ProductModel article);

	/**
	 * Find articles having the given article as component. The component is identified by its code, the catalog
	 * and catalog version.
	 *
	 * @param componentArticleCode
	 * 			the structured article component id
	 * @param catalog
	 *           the catalog id of the catalog the article belongs to
	 * @param catalogVersion
	 *           the catalog version of the catalog
	 * @return list of articles containing the given component
	 */
	public List<ProductModel> findStructuredArticlesOfComponent(String componentArticleCode, String catalog, String catalogVersion);

	/**
	 * Find articles having the given article as component. The component is identified by its model.
	 *
	 * @param componentArticle
	 * 			the structured article component
	 * @return list of articles containing the given component
	 */
	public List<ProductModel> findStructuredArticlesOfComponent(ProductModel componentArticle);

	/**
	 * Check if a given article is part of a component list.
	 *
	 * @param componentArticle
	 *           the article that shall be checked to be part of a component list
	 * @return true if the article is a component, false if not
	 */
	public boolean isComponentArticle(ProductModel componentArticle);

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
	public boolean isComponentArticle(String componentArticleCode, String catalog, String catalogVersion);

	/**
	 * Find the component article models of the article components that are part of the structured article identified by
	 * its model.
	 *
	 * @param article
	 *           the model of the structured article
	 * @return list of the article models of the components of the structured article
	 */
	public List<ProductModel> findComponentArticlesOfStructuredArticle(ProductModel article);

	/**
	 * Find the component article models of the article components that are part of the structured article identified by
	 * its code, catalog and catalog version.
	 *
	 * @param articleCode
	 *           the article code of the structured article
	 * @param catalog
	 *           the catalog the structured article belongs to
	 * @param catalogVersion
	 *           the catalog version of the catalog
	 * @return list of the article models of the components of the structured article
	 */
	public List<ProductModel> findComponentArticlesOfStructuredArticle(String articleCode, String catalog, String catalogVersion);
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentTestBase;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.dao.ArticleComponentDAO;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;



/**
 * Test class for ArticleComponentDAOImpl.
 */
@SuppressWarnings("javadoc")
@UnitTest
public class ArticleComponentDAOTest extends ArticleComponentTestBase
{
	@Resource
	private ArticleComponentDAO sapArticleComponentDao;


	@Test
	public void testEmptyArticleComponents()
	{
		final List<ArticleComponentModel> structuredArticleComponents = sapArticleComponentDao.findComponentsOfStructuredArticle(structuredArticleWithNoComponent);
		assertTrue("No product components be returned", structuredArticleComponents.isEmpty());
	}

	@Test
	public void testFindArticleComponentsOfMainProduct()
	{
		final List<ArticleComponentModel> structuredArticleComponents = sapArticleComponentDao.findComponentsOfStructuredArticle(structuredArticle);
		assertEquals(2, structuredArticleComponents.size());
	}

	@Test
	public void testFindArticleComponentsOfMainProductWithProductCode()
	{
		final List<ArticleComponentModel> structuredArticleComponents = sapArticleComponentDao.findComponentsOfStructuredArticle(structuredArticle.getCode(),
				structuredArticle.getCatalogVersion().getCatalog().getId(), structuredArticle.getCatalogVersion().getVersion());
		assertEquals(2, structuredArticleComponents.size());
	}

	@Test
	public void testFindMainProductsOfComponent()
	{
		final List<ProductModel> structuredArticles = sapArticleComponentDao.findStructuredArticlesOfComponent(componentArticle1);
		assertEquals(1, structuredArticles.size());
	}

	@Test
	public void testFindMainProductsOfComponentWithProductCode()
	{
		final List<ProductModel> structuredArticles = sapArticleComponentDao.findStructuredArticlesOfComponent(componentArticle1.getCode(), componentArticle1
				.getCatalogVersion().getCatalog().getId(), componentArticle1.getCatalogVersion().getVersion());
		assertEquals(1, structuredArticles.size());
	}
	
	@Test
	public void testIsComponentArticle()
	{
		boolean isComponent = sapArticleComponentDao.isComponentArticle(componentArticle1.getCode(), componentArticle1.getCatalogVersion().getCatalog().getId(), componentArticle1.getCatalogVersion().getVersion());
		assertTrue("Component article is not identified as component of structured article.", isComponent);
	}
	
	@Test
	public void testIsNotComponentArticle()
	{
		boolean isComponent = sapArticleComponentDao.isComponentArticle(structuredArticle.getCode(), structuredArticle.getCatalogVersion().getCatalog().getId(), structuredArticle.getCatalogVersion().getVersion());
		assertFalse("Non component article is wrongly identified to be a component article of a structured article.", isComponent);
	}
}

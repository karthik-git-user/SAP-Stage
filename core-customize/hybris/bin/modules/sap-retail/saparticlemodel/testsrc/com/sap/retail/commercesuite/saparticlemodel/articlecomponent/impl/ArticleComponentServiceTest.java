/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentService;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentTestBase;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;

/**
 * Test class for ArticleComponentServiceImpl.
 */
@SuppressWarnings("javadoc")
@UnitTest
public class ArticleComponentServiceTest extends ArticleComponentTestBase
{
	@Resource
	private ArticleComponentService sapArticleComponentService;


	@Test
	public void testGetComponentsOfStructuredArticle()
	{
		final List<ArticleComponentModel> articleComponents = sapArticleComponentService.getComponentsOfStructuredArticle(structuredArticle);
		assertEquals(2, articleComponents.size());
		assertTrue(articleComponents.contains(structuredArticleComponent1));
		assertTrue(articleComponents.contains(structuredArticleComponent2));
	}

	@Test
	public void testGetComponentsOfStructuredArticleWithCode()
	{
		final List<ArticleComponentModel> articleComponents = sapArticleComponentService.getComponentsOfStructuredArticle(
				structuredArticle.getCode(), structuredArticle.getCatalogVersion().getCatalog().getId(), structuredArticle.getCatalogVersion()
						.getVersion());
		assertEquals(2, articleComponents.size());
		assertTrue(articleComponents.contains(structuredArticleComponent1));
		assertTrue(articleComponents.contains(structuredArticleComponent2));
	}

	@Test
	public void testGetStructuredArticlesOfComponent()
	{
		final List<ProductModel> products = sapArticleComponentService.getStructuredArticlesOfComponent(componentArticle1);
		assertEquals(1, products.size());
		assertTrue(products.contains(structuredArticle));
	}

	@Test
	public void testGetStructuredArticlesOfComponentWithCode()
	{
		final List<ProductModel> products = sapArticleComponentService.getStructuredArticlesOfComponent(componentArticle1.getCode(), componentArticle1
				.getCatalogVersion().getCatalog().getId(), componentArticle1.getCatalogVersion().getVersion());
		assertEquals(1, products.size());
		assertTrue(products.contains(structuredArticle));
	}

	@Test
	public void testGetComponentArticlesOfStructuredArticle()
	{
		final List<ProductModel> products = sapArticleComponentService.getComponentArticlesOfStructuredArticles(structuredArticle);
		assertEquals(2, products.size());
		assertTrue(products.contains(componentArticle1));
		assertTrue(products.contains(componentArticle2));
	}

	@Test
	public void testGetComponentArticlesWithCode()
	{
		final List<ProductModel> products = sapArticleComponentService.getComponentArticlesOfStructuredArticles(structuredArticle.getCode(), structuredArticle
				.getCatalogVersion().getCatalog().getId(), structuredArticle.getCatalogVersion().getVersion());
		assertEquals(2, products.size());
		assertTrue(products.contains(componentArticle1));
		assertTrue(products.contains(componentArticle2));
	}
	
	@Test
	public void testIsStructuredArticlComponent()
	{
		boolean isComponent = sapArticleComponentService.isStructuredArticleComponent(componentArticle1.getCode(), componentArticle1.getCatalogVersion().getCatalog().getId(), componentArticle1.getCatalogVersion().getVersion());
		assertTrue("Component article is not identified as component of structured article.", isComponent);
	}
	
	@Test
	public void testIsNotStructuredArticleComponent()
	{
		boolean isComponent = sapArticleComponentService.isStructuredArticleComponent(structuredArticle.getCode(), structuredArticle.getCatalogVersion().getCatalog().getId(), structuredArticle.getCatalogVersion().getVersion());
		assertFalse("Non component article is wrongly identified to be a component article of a structured article.", isComponent);
	}
	

}

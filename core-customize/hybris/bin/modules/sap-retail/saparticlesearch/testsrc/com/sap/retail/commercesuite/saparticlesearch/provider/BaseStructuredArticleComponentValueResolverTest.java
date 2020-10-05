/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.mockito.Mock;

import com.sap.retail.commercesuite.saparticlemodel.enums.StructuredArticleType;
import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;


/**
 * Base test class for testing value resolver for structured articles.
 */
@SuppressWarnings("javadoc")
public abstract class BaseStructuredArticleComponentValueResolverTest extends BaseValueResolverTest
{
	@Mock
	protected ProductModel product; //NOPMD

	protected Collection<ArticleComponentModel> articleComponentList; //NOPMD

	@Mock
	protected ArticleComponentModel articleComponent1; //NOPMD

	@Mock
	protected ProductModel componentProduct1; //NOPMD

	@Mock
	protected ArticleComponentModel articleComponent2; //NOPMD

	@Mock
	protected ProductModel componentProduct2; //NOPMD

	@Mock
	protected ArticleComponentModel articleComponent3; //NOPMD

	@Mock
	protected ProductModel componentProduct3; //NOPMD

	@Before
	public void setUpBaseStructuredArticleComponentValueResolverTest()
	{
		when(product.getStructuredArticleType()).thenReturn(StructuredArticleType.SALES_SET);

		when(articleComponent1.getComponent()).thenReturn(componentProduct1);
		when(articleComponent2.getComponent()).thenReturn(componentProduct2);
		when(articleComponent3.getComponent()).thenReturn(componentProduct3);

		articleComponentList = new ArrayList<ArticleComponentModel>();
		articleComponentList.add(articleComponent1);
		articleComponentList.add(articleComponent2);
		articleComponentList.add(articleComponent3);
		when(product.getComponent()).thenReturn(articleComponentList);
	}

	public ProductModel getProduct()
	{
		return product;
	}

	public Collection<ArticleComponentModel> getArticleComponentList()
	{
		return articleComponentList;
	}

	public ArticleComponentModel getArticleComponent1()
	{
		return articleComponent1;
	}

	public ProductModel getComponentProduct1()
	{
		return componentProduct1;
	}

	public ArticleComponentModel getArticleComponent2()
	{
		return articleComponent2;
	}

	public ProductModel getComponentProduct2()
	{
		return componentProduct2;
	}

}

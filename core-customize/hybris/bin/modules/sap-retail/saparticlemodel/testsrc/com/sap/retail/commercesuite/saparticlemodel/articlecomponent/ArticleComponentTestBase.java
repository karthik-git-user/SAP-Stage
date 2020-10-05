/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

import com.sap.retail.commercesuite.saparticlemodel.model.ArticleComponentModel;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Base test class for article components unit tests.
 */
@SuppressWarnings("javadoc")
public class ArticleComponentTestBase extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Mock
	protected ProductModel structuredArticle;                    //NOPMD
	
	@Mock
	protected ProductModel structuredArticleWithNoComponent;     //NOPMD
	
	@Mock
	protected ProductModel componentArticle1;                    //NOPMD
	
	@Mock
	protected ProductModel componentArticle2;                    //NOPMD    

	@Mock
	protected ArticleComponentModel structuredArticleComponent1; //NOPMD
	
	@Mock
	protected ArticleComponentModel structuredArticleComponent2; //NOPMD

	private static final String mainProductCode = "DISPLAY";
	private static final String noCompsProductCode = "NoComp";

	/**
	 * Create model objects and add them to the model service.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		final CatalogVersionModel catalogVersion;
		
	    // handle catalog version
	    final CatalogModel defaultcatalog = modelService.create(CatalogModel.class);
		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setActive(Boolean.TRUE);
		catalogVersion.setVersion("Staged");
		catalogVersion.setCatalog(defaultcatalog);
		defaultcatalog.setId("Default");
		modelService.saveAll();

        // create a structured product containing no components
        structuredArticleWithNoComponent = new ProductModel();
        structuredArticleWithNoComponent.setCode(noCompsProductCode);
        structuredArticleWithNoComponent.setCatalogVersion(catalogVersion);
        modelService.save(structuredArticleWithNoComponent);

		// create a structured product
		structuredArticle = new ProductModel();
		structuredArticle.setCode(mainProductCode);
		structuredArticle.setCatalogVersion(catalogVersion);
		modelService.save(structuredArticle);

		// create component products
		componentArticle1 = new ProductModel();
		componentArticle1.setCode("Component-1");
		componentArticle1.setCatalogVersion(catalogVersion);
		modelService.save(componentArticle1);

		componentArticle2 = new ProductModel();
		componentArticle2.setCode("Component-2");
		componentArticle2.setCatalogVersion(catalogVersion);
		modelService.save(componentArticle2);

		// create product component data for structured product
		structuredArticleComponent1 = new ArticleComponentModel();
		structuredArticleComponent1.setComponent(componentArticle1);
		structuredArticleComponent1.setStructuredArticle(structuredArticle);
		modelService.save(structuredArticleComponent1);

		structuredArticleComponent2 = new ArticleComponentModel();
		structuredArticleComponent2.setComponent(componentArticle2);
		structuredArticleComponent2.setStructuredArticle(structuredArticle);
		modelService.save(structuredArticleComponent2);
	}

	/**
	 * Remove model objects from model service.
	 */
	@After
	public void tearDown()
	{
		modelService.remove(structuredArticleComponent1);
		modelService.remove(structuredArticleComponent2);
		modelService.remove(componentArticle1);
		modelService.remove(componentArticle2);
		modelService.remove(structuredArticle);
		modelService.remove(structuredArticleWithNoComponent);
	}

}

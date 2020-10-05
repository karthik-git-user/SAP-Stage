/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.articlecomponent.validations;

import static org.junit.Assert.assertTrue;
import de.hybris.bootstrap.annotations.UnitTest;
import com.sap.retail.commercesuite.saparticlemodel.articlecomponent.ArticleComponentTestBase;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Test class for ArticleComponentRemoveInterceptor.
 *
 */
@SuppressWarnings("javadoc")
@UnitTest
public class ArticleComponentValidationsTest extends ArticleComponentTestBase
{

	@Resource
	private ModelService modelService;


	@Test
	public void testRemoveOfComponent()
	{
		try
		{
			modelService.remove(componentArticle1);
			assert false;
		}
		catch (final Exception e)
		{
			assertTrue(e.getCause() instanceof InterceptorException);
		}

		assert false;
	}
}

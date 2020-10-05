/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendationbuffer.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultRecommendationBufferDaoTest
{
	DefaultRecommendationBufferDao buffer = new DefaultRecommendationBufferDao();

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test_buildFlexibleSearchQuery()
	{
		Assert.assertEquals("SELECT {pk} FROM {code9}", buffer.buildFlexibleSearchQuery("code9").getQuery());

		Assert.assertEquals("SELECT {pk} FROM {code9} WHERE clause1",
				buffer.buildFlexibleSearchQuery("code9", "clause1").getQuery());

		Assert.assertEquals("SELECT {pk} FROM {code9} WHERE clause1 AND clause2",
				buffer.buildFlexibleSearchQuery("code9", "clause1", "clause2").getQuery());
	}

}

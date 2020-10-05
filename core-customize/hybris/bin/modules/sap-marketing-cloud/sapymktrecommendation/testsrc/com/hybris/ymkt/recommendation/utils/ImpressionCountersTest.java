/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.utils;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ImpressionCountersTest
{
	private final ImpressionCounters impressionCounters = new ImpressionCounters();
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testImpressionCounter()
	{
		impressionCounters.addToImpressionCount(2);
		impressionCounters.addToImpressionCount(3);
		assertEquals(5, impressionCounters.getImpressionCount());
	}

	@Test
	public void testItemCounter()
	{
		impressionCounters.addToItemCount(2);
		impressionCounters.addToItemCount(3);
		assertEquals(5, impressionCounters.getItemCount());
	}
}

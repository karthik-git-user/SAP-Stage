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
public class RecentViewedItemsCollectionTest
{
	private final RecentViewedItemsCollection recentViewedItemsCollection = new RecentViewedItemsCollection(3);
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testAddCode_empty()
	{
		recentViewedItemsCollection.addCode(null);
		assertEquals(0, recentViewedItemsCollection.getCodes().size());
	}
	
	@Test
	public void testAddCode_unique()
	{
		recentViewedItemsCollection.addCode("111111");
		recentViewedItemsCollection.addCode("222222");
		recentViewedItemsCollection.addCode("111111");
		recentViewedItemsCollection.addCode("222222");
		assertEquals(2, recentViewedItemsCollection.getCodes().size());
		assertEquals("222222", recentViewedItemsCollection.getCodes().get(1));
		assertEquals("111111", recentViewedItemsCollection.getCodes().get(0));
	}
	
	@Test
	public void testAddCode_poll()
	{
		//Add 4 entries. Expect code 111111 to be removed since maxEntries=3
		recentViewedItemsCollection.addCode("111111");
		recentViewedItemsCollection.addCode("222222");
		recentViewedItemsCollection.addCode("333333");
		recentViewedItemsCollection.addCode("444444");
		assertEquals("222222", recentViewedItemsCollection.getCodes().get(0));
	}
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * 
 */
package com.hybris.ymkt.segmentation.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.segmentation.model.CMSYmktCampaignRestrictionModel;


/**
 * 
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CampaignRestrictionDescriptionHandlerTest
{

	private static CampaignRestrictionDescriptionHandler handler = new CampaignRestrictionDescriptionHandler();
	private static CMSYmktCampaignRestrictionModel model = new CMSYmktCampaignRestrictionModel();

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void getDescriptionTest_noText()
	{
		Assert.assertEquals("SAP Marketing Campaign Restriction", handler.get(model));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setTest()
	{
		handler.set(model, "");
	}

}

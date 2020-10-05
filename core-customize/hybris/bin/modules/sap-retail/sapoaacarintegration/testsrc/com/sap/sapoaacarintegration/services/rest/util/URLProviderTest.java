/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultURLProvider;




/**
 *
 */
@UnitTest
public class URLProviderTest
{

	private DefaultURLProvider classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultURLProvider();
	}

	@Test
	public void testCompileURI()
	{

		URI uri = null;
		try
		{
			uri = classUnderTest.compileURI("http://www.sap.com", "/bc/sap/", "800");
			Assert.assertEquals("www.sap.com", uri.getHost());
		}
		catch (final URISyntaxException e)
		{
			Assert.fail("No exception expected, but it was: " + e.getMessage());
		}
	}

	@Test
	public void testCompileURIException()
	{
		URI uri = null;
		try
		{
			uri = classUnderTest.compileURI("wrong input", "path", "800");
			Assert.fail("Exception expected");
		}
		catch (final URISyntaxException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(uri);
		}
	}

}

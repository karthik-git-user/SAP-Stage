/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpHeaderProvider;


/**
 *
 */

@UnitTest
public class HttpHeaderProviderTest
{

	private DefaultHttpHeaderProvider classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultHttpHeaderProvider();
	}

	@Test
	public void testCompileHttpHeader()
	{
		final HttpHeaders httpHeader = classUnderTest.compileHttpHeader("user", "pass");
		Assert.assertTrue(httpHeader.containsKey("authorization"));
	}

	@Test
	public void appendCsrfToHeaderTest()
	{
		final String xCSRFToken = "X-CSRF-Token";
		final String xCSRFToken_value = "Test123";

		HttpHeaders header = new HttpHeaders();
		final HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.add(xCSRFToken, xCSRFToken_value);
		header = classUnderTest.appendCsrfToHeader(header, responseHeader);
		Assert.assertNotNull(header);
		Assert.assertEquals(xCSRFToken_value, header.get(xCSRFToken).get(0));
	}

	@Test
	public void appendCookieToHeaderTest()
	{
		HttpHeaders header = new HttpHeaders();
		final HttpHeaders responseHeader = new HttpHeaders();

		final String SET_COOKIE = "set-cookie";
		final String COOKIE = "cookie";
		final String SET_COOKIE_VALUE = "Test123";

		final List<String> cookies = new ArrayList<>();
		cookies.add(SET_COOKIE_VALUE);
		cookies.add(SET_COOKIE_VALUE);
		cookies.add(SET_COOKIE_VALUE);

		responseHeader.put(SET_COOKIE, cookies);

		header = classUnderTest.appendCookieToHeader(header, responseHeader);
		Assert.assertNotNull(header);
		Assert.assertEquals(cookies.size(), header.get(COOKIE).size());
		Assert.assertEquals(SET_COOKIE_VALUE, header.get(COOKIE).get(0));
		Assert.assertEquals(SET_COOKIE_VALUE, header.get(COOKIE).get(1));
		Assert.assertEquals(SET_COOKIE_VALUE, header.get(COOKIE).get(2));
	}

}

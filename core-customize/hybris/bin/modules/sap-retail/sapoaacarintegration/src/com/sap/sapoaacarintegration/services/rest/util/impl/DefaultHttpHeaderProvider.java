/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpHeaders;

import com.sap.sapoaacarintegration.services.rest.util.HttpHeaderProvider;


/**
 * Default implementation of HttpHeaderProvider.
 */
public class DefaultHttpHeaderProvider implements HttpHeaderProvider
{
	/** HTTP header X-CSRF-Token key */
	private static final String HTTP_PARAM_X_CSRF_TOKEN = "X-CSRF-Token";
	/** HTTP header Cookie key */
	private static final String HTTP_PARAM_COOKIE = "Cookie";

	@Override
	public HttpHeaders compileHttpHeader(final String user, final String password)
	{
		final String auth = user + ":" + password;
		final HttpHeaders header = new HttpHeaders();
		header.set("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII)),StandardCharsets.US_ASCII));
		return header;

	}

	@Override
	public HttpHeaders appendCookieToHeader(final HttpHeaders header, final HttpHeaders responseHeader)
	{

		final List<String> cookies = responseHeader.get("set-cookie");

		for (final String cookie : cookies)
		{
			if (!cookie.contains("\n") && !cookie.contains("\r"))
			{
				header.add(HTTP_PARAM_COOKIE, cookie);
			}
		}

		return header;
	}

	@Override
	public HttpHeaders appendCsrfToHeader(final HttpHeaders header, final HttpHeaders responseHeader)
	{
		final String csrfToken = responseHeader.get(HTTP_PARAM_X_CSRF_TOKEN).get(0);
		if (!csrfToken.contains("\n") && !csrfToken.contains("\r"))
		{
			header.add(HTTP_PARAM_X_CSRF_TOKEN, csrfToken);
		}
		return header;
	}

}

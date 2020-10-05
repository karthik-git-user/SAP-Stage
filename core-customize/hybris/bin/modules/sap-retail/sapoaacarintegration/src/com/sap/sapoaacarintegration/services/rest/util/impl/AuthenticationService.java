/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util.impl;

import java.net.URISyntaxException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultAbstractRestService;
import com.sap.sapoaacarintegration.services.rest.util.HttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.HttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.URLProvider;


/**
 * Authentication Service calls Service and Fetch x-CSRF token and cookies
 */
public class AuthenticationService extends DefaultAbstractRestService
{
	private HttpHeaderProvider httpHeaderProvider;
	private URLProvider urlProvider;
	private HttpEntityBuilder httpEntityBuilder;

	/**
	 * Fetch X-csrf Token and retrieve the authentication results.
	 *
	 * @param user
	 * @param password
	 * @param url
	 * @param servicePath
	 * @param client
	 * @return AuthenticationResult
	 * @throws AuthenticationServiceException
	 */
	public AuthenticationResult execute(final String user, final String password, final String url, final String servicePath,
			final String client) throws AuthenticationServiceException, BackendDownException
	{
		AuthenticationResult authenticationResult = null;

		final HttpHeaders httpHeader = httpHeaderProvider.compileHttpHeader(user, password);
		httpHeader.add("X-CSRF-TOKEN", "Fetch");
		httpHeader.add("ACCEPT", "application/json");

		final HttpEntity<String> entity = httpEntityBuilder.createHttpEntity(httpHeader, "");
		ResponseEntity<String> response;

		try
		{
			initialize();
			beforeRestCall();

			response = getRestTemplate().exchange(urlProvider.compileURI(url, servicePath, client), HttpMethod.GET, entity,
					String.class);
			authenticationResult = new AuthenticationResult();
			authenticationResult.setResponseHeader(response.getHeaders());
		}
		catch (final URISyntaxException | RestInitializationException e)
		{
			throw new AuthenticationServiceException(e);
		}
		catch (final HttpClientErrorException e)
		{
			checkHttpStatusCode(e);
			throw new AuthenticationServiceException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}
		return authenticationResult;
	}

	/**
	 * @param httpHeaderProvider
	 *           the httpHeaderProvider to set
	 */
	public void setHttpHeaderProvider(final HttpHeaderProvider httpHeaderProvider)
	{
		this.httpHeaderProvider = httpHeaderProvider;
	}

	/**
	 * @return the httpHeaderProvider
	 */
	protected HttpHeaderProvider getHttpHeaderProvider()
	{
		return httpHeaderProvider;
	}

	/**
	 * @param httpEntityBuilder
	 *           the httpEntityBuilder to set
	 */
	public void setHttpEntityBuilder(final HttpEntityBuilder httpEntityBuilder)
	{
		this.httpEntityBuilder = httpEntityBuilder;
	}

	/**
	 * @return the httpEntityBuilder
	 */
	protected HttpEntityBuilder getHttpEntityBuilder()
	{
		return httpEntityBuilder;
	}

	/**
	 * @param urlProvider
	 *           the urlProvider to set
	 */
	public void setUrlProvider(final URLProvider urlProvider)
	{
		this.urlProvider = urlProvider;
	}

	/**
	 * @return the urlProvider
	 */
	protected URLProvider getUrlProvider()
	{
		return urlProvider;
	}
}
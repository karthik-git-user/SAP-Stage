/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.rest.OaaRestService;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;

import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Base Class for OAA REST Service classes.
 */
public abstract class DefaultAbstractRestService implements OaaRestService
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractRestService.class);

	private RestServiceConfiguration restServiceConfiguration;
	private RestTemplate restTemplate;
	private SessionService sessionService;
	private boolean isInitialized;

	/**
	 * Will be called before the REST call is made
	 */
	protected void beforeRestCall()
	{
		setBackendDown(false);
	}

	protected void initialize()
	{
		//Rest Service Configuration will be read, even when already initialized
		try
		{
			getRestServiceConfiguration().initializeConfiguration();
		}
		catch (final RestInitializationException e)
		{
			LOG.error("Error during initialization of rest call", e);
			throw new RestInitializationException(e);
		}

		if (isInitialized)
		{
			return;
		}

		final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());

		final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(getRestServiceConfiguration().getConnectTimeout());
		clientHttpRequestFactory.setReadTimeout(getRestServiceConfiguration().getReadTimeout());

		restTemplate = new RestTemplate(messageConverters);
		restTemplate.setRequestFactory(clientHttpRequestFactory);

		isInitialized = true;
	}

	/**
	 * Checks HTTP Status Codes for back end down
	 *
	 * @param e
	 * @throws BackendDownException
	 *            when status code for back end down is given
	 */
	protected void checkHttpStatusCode(final HttpClientErrorException e)
	{
		if (e.getStatusCode().equals(HttpStatus.BAD_GATEWAY) || e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)
				|| e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE) || e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS))
		{
			setBackendDown(true);
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}
	}

	/**
	 * Set the REST service configuration
	 *
	 * @param restServiceConfiguration
	 *           the restServiceConfiguration to set
	 */
	@Override
	public void setRestServiceConfiguration(final RestServiceConfiguration restServiceConfiguration)
	{
		this.restServiceConfiguration = restServiceConfiguration;
	}

	/**
	 * Set the Attribute for BackendDown in the Session
	 *
	 * @param backendDown
	 *           the value to set in the session
	 */
	@Override
	public void setBackendDown(final boolean backendDown)
	{
		this.sessionService.setAttribute("CARBackendDown", Boolean.valueOf(backendDown));
	}

	/**
	 * Set the REST template
	 *
	 * @param restTemplate
	 *           the restTemplate to set
	 */
	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}

	/**
	 * Set the Session service
	 *
	 * @param sessionService
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Get the REST template
	 *
	 * @return restTemplate
	 */
	public RestTemplate getRestTemplate()
	{
		return this.restTemplate;
	}

	/**
	 * Get the REST service configuration
	 *
	 * @return restServiceConfiguration
	 */
	public RestServiceConfiguration getRestServiceConfiguration()
	{
		return this.restServiceConfiguration;
	}

	/**
	 * Get the Session service
	 *
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}
}

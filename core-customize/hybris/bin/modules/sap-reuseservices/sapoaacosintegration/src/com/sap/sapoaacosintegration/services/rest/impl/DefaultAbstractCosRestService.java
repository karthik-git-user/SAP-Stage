/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.rest.impl;

import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacosintegration.services.rest.CosRestService;


/**
 * Rest service provider for COS
 */
public abstract class DefaultAbstractCosRestService implements CosRestService
{

	private SessionService sessionService;

	@Override
	public void setBackendDown(final boolean backendDown)
	{
		this.sessionService.setAttribute("CARBackendDown", Boolean.valueOf(backendDown));
	}

	/**
	 * Checks HTTP Status Codes for back end down
	 *
	 * @param e
	 *           the value of exception
	 */
	@Override
	public void checkHttpStatusCode(final HttpClientErrorException e)
	{
		if (e.getStatusCode().equals(HttpStatus.BAD_GATEWAY) || e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)
				|| e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE) || e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS))
		{
			setBackendDown(true);
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


}

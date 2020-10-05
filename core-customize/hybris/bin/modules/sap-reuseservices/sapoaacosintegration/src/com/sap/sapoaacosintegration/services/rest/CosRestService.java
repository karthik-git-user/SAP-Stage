/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.rest;

import org.springframework.web.client.HttpClientErrorException;


/**
 * Rest service for COS
 */
public interface CosRestService
{

	/**
	 * Set the Attribute for BackendDown in the Session
	 *
	 * @param backendDown
	 *           the value to set in the session
	 */
	void setBackendDown(final boolean backendDown);


	/**
	 * Checks HTTP Status Codes for back end down
	 *
	 * @param exception
	 *           the value of exception
	 */
	void checkHttpStatusCode(final HttpClientErrorException exception);
}

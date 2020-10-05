/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.rest;


/**
 *  Service for Oaa rest functionalities
 */
public interface OaaRestService
{

	/**
	 * Set the REST service configuration
	 *
	 * @param restServiceConfiguration
	 *           the restServiceConfiguration to set
	 */
	void setRestServiceConfiguration(final RestServiceConfiguration restServiceConfiguration);

	/**
	 * Set the Attribute for BackendDown in the Session
	 *
	 * @param backendDown
	 *           the value to set in the session
	 */
	void setBackendDown(final boolean backendDown);

}

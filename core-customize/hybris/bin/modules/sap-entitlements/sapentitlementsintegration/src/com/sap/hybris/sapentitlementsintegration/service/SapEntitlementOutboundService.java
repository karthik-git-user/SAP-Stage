/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service;

import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.GetEntitlementRequest;


/**
 * Outbound Service for communication with SAP EMS
 */
public interface SapEntitlementOutboundService
{
	/**
	 * Sends request to SAP Entitlements.
	 * 
	 * @param <T>
	 *           Type of the response object
	 * @param request
	 *           Request object
	 * @param responseClass
	 *           Type to map with API response
	 * @param destinationID
	 *           Destination ID for target destination
	 * @return The response of the request
	 */
	public <T> T sendRequest(Object request, Class<T> responseClass, String destinationID);

	/**
	 * @deprecated not used anymore.
	 */
	@Deprecated(since = "1905.2003-CEP", forRemoval = true)
	public Entitlements sendRequest(final GetEntitlementRequest request);

}

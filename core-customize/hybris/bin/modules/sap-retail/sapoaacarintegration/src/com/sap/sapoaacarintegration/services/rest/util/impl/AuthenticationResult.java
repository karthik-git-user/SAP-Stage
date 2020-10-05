/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util.impl;

import org.springframework.http.HttpHeaders;


/**
 * Container for Http Header
 */
public class AuthenticationResult
{


	private HttpHeaders responseHeader;

	/**
	 * @return the responseHeader
	 */
	public HttpHeaders getResponseHeader()
	{
		return responseHeader;
	}

	/**
	 * @param responseHeader
	 *           the responseHeader to set
	 */
	public void setResponseHeader(final HttpHeaders responseHeader)
	{
		this.responseHeader = responseHeader;
	}

}

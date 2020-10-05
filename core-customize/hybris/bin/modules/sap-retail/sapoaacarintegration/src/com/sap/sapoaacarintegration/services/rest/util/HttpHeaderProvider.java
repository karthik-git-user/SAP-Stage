/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import org.springframework.http.HttpHeaders;


/**
 * Provides HTTP header for REST Service calls. Takes care of cookies, CSRF token and validation.
 */
public interface HttpHeaderProvider
{

	/**
	 * Get Header with User and Password
	 *
	 * @param user
	 * @param password
	 * @return HttpHeaders
	 */
	HttpHeaders compileHttpHeader(String user, String password);

	/**
	 * Add Cookie to HTTP Header
	 *
	 * @param header
	 * @param responseHeader
	 * @return HttpHeaders
	 */
	HttpHeaders appendCookieToHeader(HttpHeaders header, HttpHeaders responseHeader);

	/**
	 * Add CSRF Token to HTTP Header
	 *
	 * @param header
	 * @param responseHeader
	 * @return HttpHeaders
	 */
	HttpHeaders appendCsrfToHeader(HttpHeaders header, HttpHeaders responseHeader);

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Provider class for Target URL used for REST Service calls.
 */
public interface URLProvider
{

	/**
	 * @param urlString
	 * @param path
	 * @param client
	 * @return URI
	 * @throws URISyntaxException
	 */
	URI compileURI(String urlString, String path, String client) throws URISyntaxException;

}

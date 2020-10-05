/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.util;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility class to carry-out Cookie specific functionality
 */
public final class CookieUtils
{

	private static final String HEADER_COOKIE = "Set-Cookie";

	private CookieUtils()
	{
		// To suppress creation of object of utility class
	}

	/**
	 * Method to set 'SameSite' attribute value in cookie
	 * 
	 * @param response
	 * @param cookieName
	 * @param sameSiteValue
	 */
	public static void setSameSiteAttributeInCookie(final HttpServletResponse response, String cookieName,
			final String sameSiteValue)
	{
		response.getHeaders(HEADER_COOKIE).stream().filter(header -> header.startsWith(cookieName))
				.forEach(header -> response.addHeader(HEADER_COOKIE, header.concat(sameSiteValue)));
	}

}

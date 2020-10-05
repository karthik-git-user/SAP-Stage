/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimwebservices.constants;

/**
 * Global class for all Scimwebservices constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })
public final class ScimwebservicesConstants extends GeneratedScimwebservicesConstants
{
	public static final String EXTENSIONNAME = "scimwebservices";
	public static final String AUTHORIZATION_SCOPE_PROPERTY = EXTENSIONNAME + ".oauth.scope";
	public static final String LICENSE_URL_PROPERTY = EXTENSIONNAME + ".license.url";
	public static final String TERMS_OF_SERVICE_URL_PROPERTY = EXTENSIONNAME + ".terms.of.service.url";
	public static final String LICENSE_PROPERTY = EXTENSIONNAME + ".licence";
	public static final String DOCUMENTATION_DESC_PROPERTY = EXTENSIONNAME + ".documentation.desc";
	public static final String DOCUMENTATION_TITLE_PROPERTY = EXTENSIONNAME + ".documentation.title";
	public static final String API_VERSION = "2.0.0";

	public static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";
	public static final String AUTHORIZATION_NAME = "oauth2_password";
	public static final String CLIENT_CREDENTIAL_AUTHORIZATION_NAME = "oauth2_client_credentials";

	private ScimwebservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.token;

/**
 * Interface to carry out Token related actions
 */
public interface GigyaTokenGenerator
{

	/**
	 * Generate JWT token to fetch authorizations from SAP CDC
	 *
	 * @param cliendId
	 * @param secret
	 * @param timeoutInSeconds
	 */
	String generate(String cliendId, String secret, int timeoutInSeconds);

}

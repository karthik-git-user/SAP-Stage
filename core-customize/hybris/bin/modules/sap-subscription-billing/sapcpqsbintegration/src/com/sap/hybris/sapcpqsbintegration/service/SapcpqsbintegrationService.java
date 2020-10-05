/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service;

/**
 * Generated {@link SapcpqsbintegrationService}
 */
public interface SapcpqsbintegrationService
{
	/**
	 * Get hybris logo url
	 * @param logoCode logo code
	 * @return url of logo
	 */
	String getHybrisLogoUrl(String logoCode);

	/**
	 * create logo
	 * @param logoCode logo code
	 */
	void createLogo(String logoCode);
}

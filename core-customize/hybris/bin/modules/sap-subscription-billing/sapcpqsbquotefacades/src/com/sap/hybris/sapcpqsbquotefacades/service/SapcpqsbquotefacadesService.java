/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.service;

/**
 * Generated {@link SapcpqsbquotefacadesService} file
 */
public interface SapcpqsbquotefacadesService
{
	/**
	 * Get hybris logo url
	 * @param logoCode logo code
	 * @return url of hybris logo
	 */
	String getHybrisLogoUrl(String logoCode);

	/**
	 * create logo
	 * @param logoCode logo code
	 */
	void createLogo(String logoCode);
}

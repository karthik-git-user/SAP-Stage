/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.service;

/**
 * SapcpiorderexchangeService
 */
public interface SapcpiorderexchangeService
{
	/**
	 * getHybrisLogoUrl
	 * @param logoCode String
	 * @return String
	 */
	String getHybrisLogoUrl(String logoCode);

	/**
	 * createLogo
	 * @param logoCode String
	 */
	void createLogo(String logoCode);
}

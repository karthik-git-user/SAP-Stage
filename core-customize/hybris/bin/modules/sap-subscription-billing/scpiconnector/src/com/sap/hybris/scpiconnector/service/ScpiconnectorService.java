/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.scpiconnector.service;

/**
 * SCPI Connector service
 */
public interface ScpiconnectorService
{
	/**
	 * Get hybris logo url
	 * @param logoCode logo code
	 * @return hybris url
	 */
	String getHybrisLogoUrl(String logoCode);

	/**
	 * Create logo using logo code
	 * @param logoCode logo code
	 */
	void createLogo(String logoCode);
}

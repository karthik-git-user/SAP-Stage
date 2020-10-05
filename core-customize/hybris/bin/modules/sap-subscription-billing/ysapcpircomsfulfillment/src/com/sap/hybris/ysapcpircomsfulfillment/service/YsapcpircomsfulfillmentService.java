/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysapcpircomsfulfillment.service;

/**
 * YsapcpircomsfulfillmentService interface
 */
public interface YsapcpircomsfulfillmentService
{
	/**
	 * Fetches hybris logo url
	 * @param logoCode logo code
	 * @return url of hybris logo
	 */
	String getHybrisLogoUrl(String logoCode);

	/**
	 * Creates logo using logo code
	 * @param logoCode logo code
	 */
	void createLogo(String logoCode);
}

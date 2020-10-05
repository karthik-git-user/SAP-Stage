/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.c4ccpiquote.service;


/**
 * Interface to handle basic calls to commerce
 */
public interface C4CCpiQuoteService
{
	/**
	 * method to get site and store information from commerce database
	 *
	 * @param salesOrganization parameter as a String
	 * @param distributionChannel parameter as a String
	 * @param division parameter as a String
	 * @return Base site id as a String
	 */
	public String getSiteAndStoreFromSalesArea(String salesOrganization, String distributionChannel, String division);
}

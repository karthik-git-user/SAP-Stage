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
package de.hybris.platform.sap.sapsalesordersimulation.service;

/**
 * Sap pricing enablement service. 
 */
public interface SapSimulateSalesOrderEnablementService {
	
	/**
	 * Method to check if cart pricing is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCartPricingEnabled();
	
	/**
	 * Method to check if catalog pricing is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCatalogPricingEnabled();
	
	/**
	 * Method to check if credit check is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isCreditCheckActive();
	
	/**
	 * Method to check if ATP check is enabled. 
	 * 
	 * @return boolean
	 */
	boolean isATPCheckActive();
}

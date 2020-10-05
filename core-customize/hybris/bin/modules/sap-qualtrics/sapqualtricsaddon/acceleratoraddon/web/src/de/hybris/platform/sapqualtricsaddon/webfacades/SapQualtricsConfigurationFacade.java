/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.webfacades;

import java.util.List;

import de.hybris.platform.sapqualtricsaddon.SapQualtricsConfigurationData;


/**
 * For limiting the number of extensions for Qualtrics integration, all the code is made available
 * in this addon extension. To avoid facade layer in the addon extension,  all related classes are moved
 * to webfacades package under  acceleratoraddon/web/src folder. 
 * 
 * Defines methods to retrieve SAP Qualtrics configuration
 *
 */
public interface SapQualtricsConfigurationFacade
{

	/**
	 * Get all SAP Qualtrics configurations
	 * 
	 * @return {@link List<SapQualtricsConfigurationData>}
	 */
	List<SapQualtricsConfigurationData> getQualtricsConfiguration();
	
	/**
	 * Check if the logged in customer has given consent
	 * 
	 * @return customer consent status
	 */
	boolean isLoggedInCustomerConsentGiven();


}

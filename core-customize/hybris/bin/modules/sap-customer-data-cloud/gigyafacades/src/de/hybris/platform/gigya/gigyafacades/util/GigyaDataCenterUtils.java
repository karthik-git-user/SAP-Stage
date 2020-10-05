/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to handle Gigya Data Center related methods
 */
public final class GigyaDataCenterUtils {
	
	private GigyaDataCenterUtils() {
		// To suppress object creation of utility class
	}
	
	/**
	 * Method to prepare Gigya API Domain string
	 * 
	 * @param region
	 * 		Region of user. Eg., eu1, cn1, us1 etc.
	 * 
	 * @param fallbackValue
	 * 		In case region is empty, value to be used.
	 * 
	 * @return 
	 * 		Gigya API Domain string. Eg., eu1.gigya.com
	 */
	public static String getGigyaApiDomain(final String region, final String fallbackValue) {
		if(StringUtils.isNotEmpty(region)) {
			return (StringUtils.equals(region, "cn1") ? region + ".gigya-api.cn" : region + ".gigya.com");				
		}
		else {
			return fallbackValue;
		}
	}

}

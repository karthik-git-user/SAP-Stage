/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.config.impl;

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;

import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;


/**
 * Default implementation for COS configuration service
 */
public class DefaultCosConfigurationService implements CosConfigurationService
{
	private SAPConfigurationService sapCoreConfigurationService;

	/**
	 * Provides CAC strategy ID
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getCosCacStrategyId()
	{
		return sapCoreConfigurationService.getProperty(SapoaacosintegrationConstants.COS_CAC_STRATEGY_ID);
	}

	/**
	 * Provides CAS strategy ID
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getCosCasStrategyId()
	{
		return sapCoreConfigurationService.getProperty(SapoaacosintegrationConstants.COS_CAS_STRATEGY_ID);
	}

	/**
	 * @return the sapCoreConfigurationService
	 */
	public SAPConfigurationService getSapCoreConfigurationService()
	{
		return sapCoreConfigurationService;
	}

	/**
	 * @param sapCoreConfigurationService
	 *           the sapCoreConfigurationService to set
	 */
	public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService)
	{
		this.sapCoreConfigurationService = sapCoreConfigurationService;
	}

}

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
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;


import org.apache.log4j.Logger;

import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;


/**
 * Enable the default sap pricing
 */
public class DefaultSimulateSalesOrderEnablementService implements SapSimulateSalesOrderEnablementService
{
	private static final String CONF_PROP_IS_ACTIVE_CART_PRICING = "saplivecartpricing";
	private static final String CONF_PROP_IS_ACTIVE_CATALOG_PRICING = "saplivecatalogpricing";
	private static final String CONF_PROP_IS_ACTIVE_ATPCHECK = "sapliveatpcheckactive";
	private static final String CONF_PROP_IS_ACTIVE_CREDITCHECK = "saplivecreditcheckactive";
	
	private static final Logger LOGGER = Logger
			.getLogger(DefaultSimulateSalesOrderEnablementService.class);
	private ModuleConfigurationAccess moduleConfigurationAccess;

	@Override
	public boolean isCartPricingEnabled()
	{
		
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isCartPricingEnabled = getModuleConfigurationAccess().getProperty(CONF_PROP_IS_ACTIVE_CART_PRICING );
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Cart Pricing Enabled: " + isCartPricingEnabled);
			}
			return isCartPricingEnabled;
		}
		else
		{
			logNoBaseStoreConfigrued();
			return false;
		}
		
	}

	@Override
	public boolean isCatalogPricingEnabled()
	{
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isCatalogPricingEnabled =  getModuleConfigurationAccess().getProperty(CONF_PROP_IS_ACTIVE_CATALOG_PRICING);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Catalog Pricing Enabled: " + isCatalogPricingEnabled);
			}
			return isCatalogPricingEnabled;
		}
		else
		{
			logNoBaseStoreConfigrued();
			return false;
		}
	}

	

	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}


	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	@Override
	public boolean isCreditCheckActive() {
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isCreditCheckEnabled =  getModuleConfigurationAccess().getProperty(CONF_PROP_IS_ACTIVE_CREDITCHECK);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Catalog credit check Enabled: " + isCreditCheckEnabled);
			}
			return isCreditCheckEnabled;
		}
		else
		{
			logNoBaseStoreConfigrued();
			return false;
		}
	}

	@Override
	public boolean isATPCheckActive() {
		if (getModuleConfigurationAccess().isSAPConfigurationActive())
		{
			boolean isATPCheckEnabled =  getModuleConfigurationAccess().getProperty(CONF_PROP_IS_ACTIVE_ATPCHECK);
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Catalog ATP check Enabled: " + isATPCheckEnabled);
			}
			return isATPCheckEnabled;
		}
		else
		{
			logNoBaseStoreConfigrued();
			return false;
		}
	}
	
	private void logNoBaseStoreConfigrued() {
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("No Base Store Configuration Assigned");
		}
	}



}

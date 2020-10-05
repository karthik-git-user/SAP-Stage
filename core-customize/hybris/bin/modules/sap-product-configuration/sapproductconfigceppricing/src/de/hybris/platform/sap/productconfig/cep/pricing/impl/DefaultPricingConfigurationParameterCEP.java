/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing.impl;

import de.hybris.platform.sap.productconfig.cep.pricing.PricingConfigurationParameterCEP;
import de.hybris.platform.sap.productconfig.cep.pricing.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl.DefaultPricingConfigurationParameterCPS;

import java.util.Collection;


/**
 * Default implementation of {@link PricingConfigurationParameterCEP}
 */
public class DefaultPricingConfigurationParameterCEP extends DefaultPricingConfigurationParameterCPS
		implements PricingConfigurationParameterCEP
{
	@Override
	public SAPProductConfigPricingDetailsMode getPricingDetailsMode()
	{
		return getSAPConfiguration().getSapproductconfig_pricingdetailsmode();
	}

	@Override
	public String getSubTotalForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_subtotal_baseprice_cps();
	}

	@Override
	public String getSubTotalForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_subtotal_selectedoptions_cps();
	}

	@Override
	public Collection<String> getConditionTypesForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_conditiontypes_baseprice_cps();
	}

	@Override
	public Collection<String> getConditionTypesForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_conditiontypes_selectedoptions_cps();
	}

}

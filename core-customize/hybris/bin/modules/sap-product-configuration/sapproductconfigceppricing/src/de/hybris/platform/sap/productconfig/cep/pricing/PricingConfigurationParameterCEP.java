/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing;

import de.hybris.platform.sap.productconfig.cep.pricing.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;

import java.util.Collection;


/**
 * Retrieves CEP specific data relevant for the pricing service.
 */
public interface PricingConfigurationParameterCEP extends PricingConfigurationParameterCPS
{
	/**
	 * Retrieves the pricing details mode used for pricing.
	 *
	 * @return the pricing details mode
	 */
	SAPProductConfigPricingDetailsMode getPricingDetailsMode();

	/**
	 * Retrieves the subTotal name for the base price.
	 *
	 * @return the subTotal name for the base price
	 */
	String getSubTotalForBasePrice();

	/**
	 * Retrieves the subTotal name for the option price.
	 *
	 * @return the subTotal name for the option price
	 */
	String getSubTotalForSelectedOptions();

	/**
	 * Retrieves collection of the condition types relevant for the base price calculation.
	 *
	 * @return collectiont of the condition types for the base price
	 */
	Collection<String> getConditionTypesForBasePrice();

	/**
	 * Retrieves collection of the condition types relevant for the option price calculation.
	 *
	 * @return collection of the condition types for the option price
	 */
	Collection<String> getConditionTypesForSelectedOptions();
}

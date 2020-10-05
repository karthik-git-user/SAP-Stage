/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing.impl;

import de.hybris.platform.sap.productconfig.cep.pricing.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Subtotal;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl.PricingHandlerImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * CEP implementation of {@link PricingHandler}. Override determination logic of the base price and selected options
 */
public class DefaultPricingHandlerCEP extends PricingHandlerImpl
{
	private final DefaultPricingConfigurationParameterCEP pricingConfigurationParameterCEP;


	/**
	 * Default constructor for dependency injection.
	 *
	 * @param pricingConfigurationParameterCEP
	 *           for retrieving the CEP related pricing settings
	 */
	public DefaultPricingHandlerCEP(final DefaultPricingConfigurationParameterCEP pricingConfigurationParameterCEP)
	{
		this.pricingConfigurationParameterCEP = pricingConfigurationParameterCEP;
	}

	@Override
	protected PriceModel getBasePrice(final PricingDocumentResult pricingDocumentResult)
	{
		PriceModel basePrice = null;
		final SAPProductConfigPricingDetailsMode mode = retrievePricingDetailsMode();
		switch (mode)
		{
			case CONDITIONTYPE:
				basePrice = getPriceForConditionTypes(getPricingConfigurationParameterCEP().getConditionTypesForBasePrice(),
						pricingDocumentResult);
				break;
			case PRICINGSUBTOTAL:
				basePrice = getPriceForSubTotal(getPricingConfigurationParameterCEP().getSubTotalForBasePrice(),
						pricingDocumentResult);
				break;
			case CONDITIONFUNCTION:
			default:
				basePrice = super.getBasePrice(pricingDocumentResult);
		}
		return basePrice;
	}

	@Override
	protected PriceModel getSelectedOptionsPrice(final PricingDocumentResult pricingDocumentResult)
	{
		PriceModel selectedOptionsPrice = null;
		final SAPProductConfigPricingDetailsMode mode = retrievePricingDetailsMode();
		switch (mode)
		{
			case CONDITIONTYPE:
				selectedOptionsPrice = getPriceForConditionTypes(
						getPricingConfigurationParameterCEP().getConditionTypesForSelectedOptions(), pricingDocumentResult);
				break;
			case PRICINGSUBTOTAL:
				selectedOptionsPrice = getPriceForSubTotal(getPricingConfigurationParameterCEP().getSubTotalForSelectedOptions(),
						pricingDocumentResult);
				break;
			case CONDITIONFUNCTION:
			default:
				selectedOptionsPrice = super.getSelectedOptionsPrice(pricingDocumentResult);
		}
		return selectedOptionsPrice;
	}

	protected SAPProductConfigPricingDetailsMode retrievePricingDetailsMode()
	{
		return getPricingConfigurationParameterCEP().getPricingDetailsMode();
	}

	protected PriceModel getPriceForConditionTypes(final Collection<String> conditionTypes,
			final PricingDocumentResult pricingDocumentResult)
	{
		if (pricingDocumentResult == null || CollectionUtils.isEmpty(pricingDocumentResult.getConditions())
				|| CollectionUtils.isEmpty(conditionTypes))
		{
			return PriceModel.NO_PRICE;
		}
		else
		{
			boolean conditionTypeFound = false;
			BigDecimal value = BigDecimal.ZERO;
			for (final ConditionResult condition : pricingDocumentResult.getConditions())
			{
				if (!condition.isStatistical() && condition.getInactiveFlag().isBlank()
						&& conditionTypes.contains(condition.getConditionType()))
				{
					value = value.add(BigDecimal.valueOf(condition.getConditionValue()));
					conditionTypeFound = true;
				}
			}
			if (!conditionTypeFound)
			{
				return PriceModel.NO_PRICE;
			}
			return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), value);
		}
	}

	protected PriceModel getPriceForSubTotal(final String subTotalFlag, final PricingDocumentResult pricingDocumentResult)
	{
		Subtotal subTotalFound = null;
		if (pricingDocumentResult == null || CollectionUtils.isEmpty(pricingDocumentResult.getSubTotals())
				|| StringUtils.isEmpty(subTotalFlag))
		{
			return PriceModel.NO_PRICE;
		}
		else
		{
			for (final Subtotal subTotal : pricingDocumentResult.getSubTotals())
			{
				if (subTotalFlag.equals(subTotal.getFlag()))
				{
					subTotalFound = subTotal;
					break;
				}
			}
		}
		if (subTotalFound == null)
		{
			return PriceModel.NO_PRICE;
		}
		return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), new BigDecimal(subTotalFound.getValue()));
	}

	protected PriceModel createPriceModel(final String currency, final BigDecimal valuePrice)
	{
		final PriceModel price = getConfigModelFactory().createInstanceOfPriceModel();
		price.setCurrency(currency);
		price.setPriceValue(valuePrice);
		return price;
	}

	protected DefaultPricingConfigurationParameterCEP getPricingConfigurationParameterCEP()
	{
		return pricingConfigurationParameterCEP;
	}

}

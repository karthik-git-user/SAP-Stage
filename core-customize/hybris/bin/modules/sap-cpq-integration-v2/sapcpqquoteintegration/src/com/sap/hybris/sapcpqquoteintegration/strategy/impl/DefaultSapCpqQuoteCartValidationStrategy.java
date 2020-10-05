/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteCartValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public class DefaultSapCpqQuoteCartValidationStrategy extends DefaultQuoteCartValidationStrategy {

	@Override
	public boolean validate(AbstractOrderModel source, AbstractOrderModel target) {
		
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (source.getSubtotal() == 0 && target.getSubtotal() == 0)
		{
			return false;
		}

		return compareEntries(source.getEntries(), target.getEntries());
	}
	
	@Override
	protected boolean compareEntries(List<AbstractOrderEntryModel> sourceEntries,
			List<AbstractOrderEntryModel> targetEntries) {
		
		if (CollectionUtils.size(sourceEntries) != CollectionUtils.size(targetEntries))
		{
			return false;
		}

		for (int i = 0; i < sourceEntries.size(); i++)
		{
			final AbstractOrderEntryModel sourceEntry = sourceEntries.get(i);
			final AbstractOrderEntryModel targetEntry = targetEntries.get(i);

			if (ObjectUtils.compare(sourceEntry.getEntryNumber(), targetEntry.getEntryNumber()) != 0
					|| !StringUtils.equals(sourceEntry.getProduct().getCode(), targetEntry.getProduct().getCode())
					|| ObjectUtils.compare(sourceEntry.getQuantity(), targetEntry.getQuantity()) != 0)
			{
				return false;
			}
		}

		return true;
	}
	
}

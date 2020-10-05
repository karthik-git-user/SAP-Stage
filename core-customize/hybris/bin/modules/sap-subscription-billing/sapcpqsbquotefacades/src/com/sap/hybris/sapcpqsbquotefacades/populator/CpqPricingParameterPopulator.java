/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.sap.hybris.sapcpqsbintegration.model.CpqPricingParameterModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sapcpqsbintegration.data.CpqPricingParameterData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class CpqPricingParameterPopulator<SOURCE extends CpqPricingParameterModel, TARGET extends CpqPricingParameterData> implements Populator<SOURCE, TARGET>{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		target.setCode(source.getCode());
		target.setItemId(source.getItemId());
		target.setValue(source.getValue());
	}

}

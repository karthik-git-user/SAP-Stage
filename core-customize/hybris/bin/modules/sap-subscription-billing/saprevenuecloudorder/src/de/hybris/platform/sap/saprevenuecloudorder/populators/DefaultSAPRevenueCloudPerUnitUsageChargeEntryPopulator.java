/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.sap.hybris.saprevenuecloudproduct.model.PerUnitUsageChargeEntryModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.AbstractUsageChargeEntryPopulator;
import de.hybris.platform.subscriptionservices.model.TierUsageChargeEntryModel;

/**
 * Populate DTO {@link PerUnitUsageChargeEntryData} with data from {@link TierUsageChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudPerUnitUsageChargeEntryPopulator<SOURCE extends PerUnitUsageChargeEntryModel, TARGET extends PerUnitUsageChargeEntryData>
		extends AbstractUsageChargeEntryPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		super.populate(source, target);
	}
}

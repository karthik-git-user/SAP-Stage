/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.saprevenuecloudproduct.model.PerUnitUsageChargeEntryModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.PerUnitUsageChargePopulator;
import de.hybris.platform.subscriptionservices.model.PerUnitUsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;

/**
 * Populate DTO {@link PerUnitUsageChargeData} with data from {@link PerUnitUsageChargeModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudPerUnitUsageChargePopulator<SOURCE extends PerUnitUsageChargeModel,TARGET extends PerUnitUsageChargeData> extends PerUnitUsageChargePopulator<SOURCE, TARGET> {
	

	private Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> perUnitUsageChargeEntryConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		super.populate(source, target);
		
		final List<UsageChargeEntryData> usageChargeEntries = new ArrayList<>();

		for (final UsageChargeEntryModel usageChargeEntry : source.getUsageChargeEntries())
		{
			if (usageChargeEntry instanceof PerUnitUsageChargeEntryModel)
			{
				usageChargeEntries.add(getPerUnitUsageChargeEntryConverter().convert((PerUnitUsageChargeEntryModel)usageChargeEntry));
			}
		}

		if(target.getUsageChargeEntries().isEmpty()) {
			target.setUsageChargeEntries(usageChargeEntries);
		}

		target.setBlockSize(source.getBlockSize());
		target.setIncludedQty(source.getIncludedQty());
		target.setMinBlocks(source.getMinBlocks());
		target.setRatio(source.getRatio());
		target.setSubscriptionBillingId(source.getSubscriptionBillingId());
	}


	public Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> getPerUnitUsageChargeEntryConverter()
	{
		return perUnitUsageChargeEntryConverter;
	}

	public void setPerUnitUsageChargeEntryConverter(
			Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> perUnitUsageChargeEntryConverter) 
	{
		this.perUnitUsageChargeEntryConverter = perUnitUsageChargeEntryConverter;
	}
}

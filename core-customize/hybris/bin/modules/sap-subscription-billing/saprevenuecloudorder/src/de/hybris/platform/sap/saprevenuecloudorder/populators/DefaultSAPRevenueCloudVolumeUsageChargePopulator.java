/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import de.hybris.platform.subscriptionfacades.data.VolumeUsageChargeData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.VolumeUsageChargePopulator;

import de.hybris.platform.subscriptionservices.model.VolumeUsageChargeModel;

/**
 * Populate DTO {@link VolumeUsageChargeData} with data from {@link VolumeUsageChargeModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudVolumeUsageChargePopulator<SOURCE extends VolumeUsageChargeModel,TARGET extends VolumeUsageChargeData> extends VolumeUsageChargePopulator<SOURCE, TARGET> {
	

	

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		super.populate(source, target);
		target.setMinBlocks(source.getMinBlocks());
		target.setSubscriptionBillingId(source.getSubscriptionBillingId());
	}

}


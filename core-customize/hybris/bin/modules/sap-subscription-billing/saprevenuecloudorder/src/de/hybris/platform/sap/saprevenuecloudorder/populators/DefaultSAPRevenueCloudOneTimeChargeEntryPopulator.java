/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.OneTimeChargeEntryData;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionfacades.product.converters.populator.OneTimeChargeEntryPopulator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Populate DTO {@link OneTimeChargeEntryData} with data from {@link OneTimeChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudOneTimeChargeEntryPopulator<SOURCE extends OneTimeChargeEntryModel,TARGET extends OneTimeChargeEntryData> extends OneTimeChargeEntryPopulator<SOURCE,TARGET> {


    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException{

        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);
        super.populate(source, target);
        target.setSubscriptionBillingId(source.getSubscriptionBillingId());
    }
}





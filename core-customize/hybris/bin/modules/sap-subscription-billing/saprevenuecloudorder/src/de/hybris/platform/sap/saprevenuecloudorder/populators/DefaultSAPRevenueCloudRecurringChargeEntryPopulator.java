/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionfacades.product.converters.populator.RecurringChargeEntryPopulator;


/**
 * Populate DTO {@link RecurringChargeEntryData} with data from {@link RecurringChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudRecurringChargeEntryPopulator<SOURCE extends RecurringChargeEntryModel,TARGET extends RecurringChargeEntryData> extends RecurringChargeEntryPopulator<SOURCE,TARGET> {

	@Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException{

        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);
        super.populate(source, target);
        target.setSubscriptionBillingId(source.getSubscriptionBillingId());

 }


}







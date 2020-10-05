/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquotefacades.populators;

/**
 * Converter for converting order / cart entries. It sets the total and base prices for entries that have multiple
 * billing frequencies (as they are subscription products)
 */

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;


import javax.annotation.Nonnull;

public class DiscountOrderEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {
    @Override
    public void populate(@Nonnull final AbstractOrderEntryModel source, @Nonnull final OrderEntryData target) {

        if (source instanceof QuoteEntryModel) {
            QuoteEntryModel quoteEntry = (QuoteEntryModel) source;
            validateParameterNotNullStandardMessage("source", source);
            validateParameterNotNullStandardMessage("target", target);

            String discountPercent  = String.format("%.4s",quoteEntry.getDiscountPercent());
            target.setDiscountPercent(discountPercent);
        }

    }
}




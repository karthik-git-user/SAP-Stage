/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.SubscriptionExtensionResponse;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;

/**
 * Populate DTO {@link SubscriptionExtensionResponse} with data from {@link SubscriptionExtensionData}.
 *
 * @param <S> source class
 * @param <T> target class
 */
public class DefaultSapSubscriptionExtensionPopulator<S extends SubscriptionExtensionResponse, T extends SubscriptionExtensionData>
        implements Populator<S, T>  {

    @Override
    public void populate(S source, T target) throws ConversionException {
        Date validUntilDate = SapRevenueCloudSubscriptionUtil.stringToDate(source.getValidUntil());
        target.setValidUntil(validUntilDate);
        target.setValidUntilIsUnlimited(source.isValidUntilIsUnlimited());
    }
}

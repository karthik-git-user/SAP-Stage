/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.MetaData;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.SubscriptionExtensionForm;
import de.hybris.platform.saprevenuecloudorder.data.MetadataData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionFormData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populate DTO {@link SubscriptionExtensionFormData} with data from {@link SubscriptionExtensionForm}.
 *
 * @param <S> source class
 * @param <T> target class
 */
public class DefaultSapSubscriptionExtensionFormPopulator<S extends SubscriptionExtensionFormData, T extends SubscriptionExtensionForm>
        implements Populator<S, T>  {

    @Override
    public void populate(S source, T target) throws ConversionException {
        target.setChangedAt(source.getChangedAt());
        target.setChangedBy(source.getChangedBy());
        target.setExtensionDate(source.getExtensionDate());
        target.setNumberOfBillingPeriods(source.getNumberOfBillingPeriods());
        target.setUnlimited(source.getUnlimited());

        MetadataData metadataData = source.getMetadata();
        if(metadataData !=null) {
            MetaData metadata = new MetaData();
            metadata.setVersion(Integer.toString(metadataData.getVersion()));
            target.setMetaData(metadata);
        }
    }
}

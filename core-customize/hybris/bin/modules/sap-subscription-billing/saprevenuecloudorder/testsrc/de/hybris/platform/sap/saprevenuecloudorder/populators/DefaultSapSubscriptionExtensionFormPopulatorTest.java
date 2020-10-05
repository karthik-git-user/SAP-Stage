/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.SubscriptionExtensionForm;
import de.hybris.platform.saprevenuecloudorder.data.MetadataData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionFormData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapSubscriptionExtensionFormPopulatorTest {

    private static final Date CHANGED_AT = new Date();
    private static final String CHANGED_BY = "some.user@sap.com";
    private static final Date EXTENSION_DATE = new Date();
    private static final int NUMBER_OF_BILLING_PERIODS = 2;
    private static final int VERSION = 2;
    private static final boolean IS_UNLIMITED = false;

    @InjectMocks
    DefaultSapSubscriptionExtensionFormPopulator<SubscriptionExtensionFormData, SubscriptionExtensionForm> defaultSubscriptionExtensionFormPopulator;

    @Test
    @SuppressWarnings({"removal"})
    public void populate_success(){

        //MetaData
        MetadataData metadata = new MetadataData();
        metadata.setVersion(VERSION);

        //SubscriptionExtensionFormData
        SubscriptionExtensionFormData source = new SubscriptionExtensionFormData();
        source.setChangedAt(CHANGED_AT);
        source.setChangedBy(CHANGED_BY);
        source.setExtensionDate(EXTENSION_DATE);
        source.setNumberOfBillingPeriods(NUMBER_OF_BILLING_PERIODS);
        source.setUnlimited(IS_UNLIMITED);
        source.setMetadata(metadata);

        //SubscriptionExtensionForm
        SubscriptionExtensionForm target = new SubscriptionExtensionForm();

        //Execute
        defaultSubscriptionExtensionFormPopulator.populate(source, target);

        //Verify
        Assert.assertEquals(target.getMetaData().getVersion(), String.valueOf(VERSION));
        Assert.assertEquals(CHANGED_AT, target.getChangedAt());
        Assert.assertEquals(CHANGED_BY, target.getChangedBy());
        Assert.assertEquals(EXTENSION_DATE, target.getExtensionDate());
        Assert.assertEquals(NUMBER_OF_BILLING_PERIODS, target.getNumberOfBillingPeriods().intValue());
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.SubscriptionExtensionResponse;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapSubscriptionExtensionPopulatorTest {

    @InjectMocks
    DefaultSapSubscriptionExtensionPopulator<SubscriptionExtensionResponse, SubscriptionExtensionData> defaultSapSubscriptionExtensionPopulator;

    private static final String VALID_UNTIL = "2029-01-01T05:00:00.000Z";
    private static final boolean IS_UNLIMITED = true;

    @Test
    public void populate_success(){
        //SubscriptionExtensionResponse
        SubscriptionExtensionResponse source = new SubscriptionExtensionResponse();
        source.setValidUntil(VALID_UNTIL);
        source.setValidUntilIsUnlimited(IS_UNLIMITED);

        SubscriptionExtensionData target = new SubscriptionExtensionData();

        //Execute
        defaultSapSubscriptionExtensionPopulator.populate(source, target);

        //Verify
        Assert.assertEquals(IS_UNLIMITED, target.getValidUntilIsUnlimited());
    }
}

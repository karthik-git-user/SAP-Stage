/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulatorTest{

    @Mock
    UsageChargeEntryModel usageChargeEntryModel;

    @Mock
    PriceDataFactory mockPriceDataFactory;

    @Mock
    CommonI18NService mockCommonI18NService;

    @InjectMocks
    DefaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulator<UsageChargeEntryModel,UsageChargeEntryData> defaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulator;

    private UsageChargeEntryData usageChargeEntryData = new UsageChargeEntryData();
    private final CurrencyModel currencyModel = new CurrencyModel();
    private PriceData priceData = new PriceData();


    @Before
    public void setUp(){
        //Price
        double price = 100d;
        usageChargeEntryModel.setFixedPrice(price);

        //PriceData
        BigDecimal bigDecimal = new BigDecimal("1238126387123");
        priceData.setValue(bigDecimal);

        //Mocks
        doReturn(priceData).when(mockPriceDataFactory).create(eq(PriceDataType.BUY),any(BigDecimal.class),any(CurrencyModel.class));
    }

    @Test
    public void populate_success(){
        //Setup
        doReturn(currencyModel).when(usageChargeEntryModel).getCurrency();

        //Execute
        defaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulator.populate(usageChargeEntryModel,usageChargeEntryData);

        //Verify
        Assert.assertNotNull(usageChargeEntryData);
        Assert.assertEquals(priceData,usageChargeEntryData.getFixedPrice());
    }

    @Test
    public void populate_currencyNull(){
        //Setup
        doReturn(null).when(usageChargeEntryModel).getCurrency();
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);

        //Execute
        defaultSAPRevenueCloudFixedPriceUsageChargeEntryPopulator.populate(usageChargeEntryModel,usageChargeEntryData);

        //Verify
        Assert.assertNotNull(usageChargeEntryData);
        Assert.assertEquals(priceData,usageChargeEntryData.getFixedPrice());
    }
}

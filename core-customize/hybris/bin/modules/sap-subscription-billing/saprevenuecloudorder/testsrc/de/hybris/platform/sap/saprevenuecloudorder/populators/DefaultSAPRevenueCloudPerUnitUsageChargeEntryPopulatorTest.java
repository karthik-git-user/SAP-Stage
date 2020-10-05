/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import com.sap.hybris.saprevenuecloudproduct.model.PerUnitUsageChargeEntryModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeEntryData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudPerUnitUsageChargeEntryPopulatorTest {

    @Mock
    PriceDataFactory mockPriceDataFactory;

    @Mock
    CommonI18NService mockCommonI18NService;

    @InjectMocks
    DefaultSAPRevenueCloudPerUnitUsageChargeEntryPopulator<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> dataDefaultSAPRevenueCloudPerUnitUsageChargeEntryPopulator;

    private PerUnitUsageChargeEntryModel perUnitUsageChargeEntryModel = new PerUnitUsageChargeEntryModel();
    private PerUnitUsageChargeEntryData perUnitUsageChargeEntryData = new PerUnitUsageChargeEntryData();
    private CurrencyModel currencyModel = new CurrencyModel();
    private PriceData priceData = new PriceData();

    @Test
    public void populate_success(){
        //Setup
        //Price
        double price = 100d;

        //Per Unit Usage Charge Entry Model
        perUnitUsageChargeEntryModel.setPrice(price);

        //Mocks
        doReturn(currencyModel).when(mockCommonI18NService).getCurrentCurrency();
        doReturn(priceData).when(mockPriceDataFactory).create(eq(PriceDataType.BUY),any(BigDecimal.class),any(CurrencyModel.class));

        //Execute
        dataDefaultSAPRevenueCloudPerUnitUsageChargeEntryPopulator.populate(perUnitUsageChargeEntryModel,perUnitUsageChargeEntryData);

        //Verify
        Assert.assertNotNull(perUnitUsageChargeEntryData);
        Assert.assertEquals(priceData,perUnitUsageChargeEntryData.getPrice());
    }
}

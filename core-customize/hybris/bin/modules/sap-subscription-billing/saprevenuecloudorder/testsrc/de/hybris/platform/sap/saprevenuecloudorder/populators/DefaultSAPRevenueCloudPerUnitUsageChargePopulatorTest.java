/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import com.sap.hybris.saprevenuecloudproduct.model.PerUnitUsageChargeEntryModel;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionservices.model.PerUnitUsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudPerUnitUsageChargePopulatorTest {

    @Mock
    Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> mockChargeEntryModelPerUnitUsageChargeEntryDataConverter;

    @InjectMocks
    DefaultSAPRevenueCloudPerUnitUsageChargePopulator<PerUnitUsageChargeModel, PerUnitUsageChargeData> dataDefaultSAPRevenueCloudPerUnitUsageChargePopulator;

    private LocaleProvider localeProvider = new StubLocaleProvider(Locale.ENGLISH);
    private PerUnitUsageChargeModel perUnitUsageChargeModel = new PerUnitUsageChargeModel();
    private PerUnitUsageChargeData perUnitUsageChargeData = new PerUnitUsageChargeData();
    private UsageChargeEntryModel usageChargeEntryModel = new PerUnitUsageChargeEntryModel();
    private List<UsageChargeEntryModel> usageChargeEntryModelList = new ArrayList<>();
    private UsageChargeEntryData usageChargeEntryData = new UsageChargeEntryData();
    private List<UsageChargeEntryData> usageChargeEntryDataList = new ArrayList<>();

    @Before
    public void setUp(){
        usageChargeEntryModelList.add(usageChargeEntryModel);
        usageChargeEntryDataList.add(usageChargeEntryData);

        //Item Model Context
        ItemModelContextImpl itemModelContext = (ItemModelContextImpl) perUnitUsageChargeModel.getItemModelContext();
        itemModelContext.setLocaleProvider(localeProvider);
    }

    @Test
    public void populate_success(){
        //SetUp
        DefaultSAPRevenueCloudPerUnitUsageChargePopulator<PerUnitUsageChargeModel, PerUnitUsageChargeData> spyDefaultSAPRevenueCloudPerUnitUsageChargePopulator = Mockito.spy(dataDefaultSAPRevenueCloudPerUnitUsageChargePopulator);

        //Per Unit Usage Charge Model
        perUnitUsageChargeModel.setUsageChargeEntries(usageChargeEntryModelList); 
        perUnitUsageChargeModel.setBlockSize(10);
        perUnitUsageChargeModel.setIncludedQty(15);
        perUnitUsageChargeModel.setMinBlocks(20);

        //Mocks
        doNothing().when(spyDefaultSAPRevenueCloudPerUnitUsageChargePopulator).populate(eq(perUnitUsageChargeModel),eq(perUnitUsageChargeData));
        doReturn(usageChargeEntryData).when(mockChargeEntryModelPerUnitUsageChargeEntryDataConverter).convert(any(PerUnitUsageChargeEntryModel.class));

        //Execute
        dataDefaultSAPRevenueCloudPerUnitUsageChargePopulator.populate(perUnitUsageChargeModel,perUnitUsageChargeData);

        //Verify
        Assert.assertNotNull(perUnitUsageChargeData);
        Assert.assertEquals(usageChargeEntryDataList.size(),perUnitUsageChargeData.getUsageChargeEntries().size());
        Assert.assertEquals(usageChargeEntryDataList,perUnitUsageChargeData.getUsageChargeEntries());
        Assert.assertEquals(perUnitUsageChargeModel.getBlockSize(),perUnitUsageChargeData.getBlockSize());
        Assert.assertEquals(perUnitUsageChargeModel.getIncludedQty(),perUnitUsageChargeData.getIncludedQty());
        Assert.assertEquals(perUnitUsageChargeModel.getMinBlocks(),perUnitUsageChargeData.getMinBlocks());
    }
}

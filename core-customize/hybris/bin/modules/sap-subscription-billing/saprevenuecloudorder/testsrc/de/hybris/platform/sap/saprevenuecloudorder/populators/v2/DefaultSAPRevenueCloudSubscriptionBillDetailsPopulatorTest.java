/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.v2;

import com.sap.hybris.saprevenuecloudproduct.enums.RatePlanElementType;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.*;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudSubscriptionBillDetailsPopulatorTest {

    @Mock
    PriceDataFactory mockPriceDataFactory;

    @Mock
    CommonI18NService mockCommonI18NService;

    @Mock
    CMSSiteService mockCmsSiteService;

    @Mock
    SapRevenueCloudProductService mockSapRevenueCloudProductService;

    @Mock
    Populator<ProductModel, ProductData> mockProductModelProductDataPopulator;

    @InjectMocks
    DefaultSAPRevenueCloudSubscriptionBillDetailsPopulator<Bill, SubscriptionBillingData> defaultSAPRevenueCloudSubscriptionBillDetailsPopulator;


    private Bill bill;
    private SubscriptionBillingData subscriptionBillingData;
    private Product product = new Product();
    private Subscription subscription = new Subscription();
    private NetAmount netAmount = new NetAmount();
    private PriceData priceData = new PriceData();
    private CurrencyModel currencyModel = new CurrencyModel();
    private RatingPeriod ratingPeriod = new RatingPeriod();
    private ConsumedQuantity consumedQuantity = new ConsumedQuantity();
    private UsageUnitModel usageUnitModel = new UsageUnitModel();
    private CurrencyModel currentCurrency = new CurrencyModel();
    private List<Charge> charges = new LinkedList<>();
    private Charge charge = new Charge();
    private CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
    private RatePlan ratePlan = new RatePlan();
    private ProductModel productModel = new ProductModel();
    private SubscriptionPricePlanModel subscriptionPricePlanModel = new SubscriptionPricePlanModel();
    private SAPRatePlanElementModel planElementModel = new SAPRatePlanElementModel();



    @Before
    public void setup(){
        bill = new Bill();
        subscriptionBillingData = new SubscriptionBillingData();

        //Product
        product.setCode("19302");

        //Subscription
        subscription.setDocumentNumber(12);

        //NetAmount
        netAmount.setAmount(290d);

        //Currency Model
        currencyModel.setIsocode("USD");

        //Populate Billing Charges
        //Rating period
        ratingPeriod.setStart("2017-01-01T10:00:00.000Z");
        ratingPeriod.setEnd("2019-01-01T10:00:00.000Z");

        //Currency Mode
        currentCurrency.setIsocode("USD");

        //Charge
        charge.setRatingPeriod(ratingPeriod);
        charge.setConsumedQuantity(consumedQuantity);

        //RatePlan
        ratePlan.setId("162536AB52617");

        //Subscription Price plan model
        subscriptionPricePlanModel.setProduct(productModel);

        //Bill
        bill.setProduct(product);
        bill.setSubscription(subscription);
        bill.setNetAmount(netAmount);
        bill.setRatePlan(ratePlan);
        bill.setCharges(charges);

        //SAP rate plan element model
        planElementModel.setName("SomeName");
        planElementModel.setType(RatePlanElementType.USAGE);

        //Mocks
        doReturn(currencyModel).when(mockCommonI18NService).getCurrentCurrency();
        doReturn(priceData).when(mockPriceDataFactory).create(eq(PriceDataType.BUY), any(BigDecimal.class), any(String.class));
        doReturn(catalogVersionModel).when(mockCmsSiteService).getCurrentCatalogVersion();
        doReturn(subscriptionPricePlanModel).when(mockSapRevenueCloudProductService).getSubscriptionPricePlanForId(any(String.class),any(CatalogVersionModel.class));
        doNothing().when(mockProductModelProductDataPopulator).populate(eq(productModel),any(ProductData.class));
        doReturn(usageUnitModel).when(mockSapRevenueCloudProductService).getUsageUnitfromId(any(String.class));
        doReturn(currentCurrency).when(mockCommonI18NService).getCurrentCurrency();
        doReturn(priceData).when(mockPriceDataFactory).create(eq(PriceDataType.BUY), any(BigDecimal.class), eq("USD"));
        doReturn(planElementModel).when(mockSapRevenueCloudProductService).getRatePlanElementfromId(any(String.class));
    }


    @Test
    public void populate_success_CQ1() {
        //Setup
        //Consumed Quantity
        consumedQuantity.setValue(1);

        //Usage Unit Model
        usageUnitModel.setNamePlural("NamePlural",Locale.ENGLISH);

        //Execute
        defaultSAPRevenueCloudSubscriptionBillDetailsPopulator.populate(bill, subscriptionBillingData);

        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(product.getCode(), subscriptionBillingData.getProductCode());
        Assert.assertEquals(String.valueOf(subscription.getDocumentNumber()), subscriptionBillingData.getSubscriptionId());
        Assert.assertEquals(priceData, subscriptionBillingData.getPrice());

        List<UsageChargeData> usageChargeDataList = subscriptionBillingData.getCharges();
        Assert.assertEquals(charges.size(), usageChargeDataList.size());
        for(UsageChargeData usageChargeData: usageChargeDataList){
            Assert.assertEquals(priceData,usageChargeData.getNetAmount());
            Assert.assertEquals((Integer)(charge.getConsumedQuantity().getValue()),usageChargeData.getUsage());
            Assert.assertEquals(usageUnitModel.getName(),usageChargeData.getUsageUnit().getName());
            Assert.assertEquals(usageUnitModel.getNamePlural(),usageChargeData.getUsageUnit().getNamePlural());
            Assert.assertEquals(planElementModel.getName(),usageChargeData.getName());
            Assert.assertEquals(SapRevenueCloudSubscriptionUtil.stringToDate(charge.getRatingPeriod().getEnd()),usageChargeData.getToDate());
            Assert.assertEquals(SapRevenueCloudSubscriptionUtil.stringToDate(charge.getRatingPeriod().getStart()),usageChargeData.getFromDate());
        }
    }

    @Test
    public void populate_success_CQ0() {
        //Setup
        //Consumed Quantity
        consumedQuantity.setValue(0);

        //Usage Unit Model
        usageUnitModel.setName("Name",Locale.ENGLISH);

        //Execute
        defaultSAPRevenueCloudSubscriptionBillDetailsPopulator.populate(bill, subscriptionBillingData);

        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(product.getCode(), subscriptionBillingData.getProductCode());
        Assert.assertEquals(String.valueOf(subscription.getDocumentNumber()), subscriptionBillingData.getSubscriptionId());
        Assert.assertEquals(priceData, subscriptionBillingData.getPrice());

        List<UsageChargeData> usageChargeDataList = subscriptionBillingData.getCharges();
        Assert.assertEquals(charges.size(), usageChargeDataList.size());
        for(UsageChargeData usageChargeData: usageChargeDataList){
            Assert.assertEquals(priceData,usageChargeData.getNetAmount());
            Assert.assertEquals((Integer)(charge.getConsumedQuantity().getValue()),usageChargeData.getUsage());
            Assert.assertEquals(usageUnitModel.getName(),usageChargeData.getUsageUnit().getName());
            Assert.assertEquals(usageUnitModel.getNamePlural(),usageChargeData.getUsageUnit().getNamePlural());
            Assert.assertEquals(planElementModel.getName(),usageChargeData.getName());
            Assert.assertEquals(SapRevenueCloudSubscriptionUtil.stringToDate(charge.getRatingPeriod().getEnd()),usageChargeData.getToDate());
            Assert.assertEquals(SapRevenueCloudSubscriptionUtil.stringToDate(charge.getRatingPeriod().getStart()),usageChargeData.getFromDate());
        }
    }

}


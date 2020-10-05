/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.v2;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.NetAmount;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudSubscriptionBillsListPopulatorTest {

    @Mock
    CommonI18NService mockCommonI18NService;

    @Mock
    PriceDataFactory mockPriceDataFactory;

    @InjectMocks
    DefaultSAPRevenueCloudSubscriptionBillsListPopulator<BillsList,SubscriptionBillingData> defaultSAPRevenueCloudSubscriptionBillsListPopulator;

    private BillsList billsList = new BillsList();
    private SubscriptionBillingData subscriptionBillingData = new SubscriptionBillingData();
    private PriceData priceData = new PriceData();
    private CurrencyModel currencyModel = new CurrencyModel();
    private NetAmount netAmount = new NetAmount();
    private List<Bill> list = new LinkedList<>();

    @Test
    public void populate_success(){
        //SetUp
        //NetAmount
        netAmount.setAmount(100d);

        //Currency Model
        currencyModel.setIsocode("USD");

        //Bills List
        billsList.setNetAmount(netAmount);
        billsList.setBillingDate("2017-01-01T10:00:00.000Z");
        billsList.setDocumentNumber(12);
        billsList.setBillItems(list);

        //Mocks
        doReturn(currencyModel).when(mockCommonI18NService).getCurrentCurrency();
        doReturn(priceData).when(mockPriceDataFactory).create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class));


        //Execute
        defaultSAPRevenueCloudSubscriptionBillsListPopulator.populate(billsList,subscriptionBillingData);


        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(SapRevenueCloudSubscriptionUtil.stringToDate(billsList.getBillingDate(), SUBSCRIPTION_BILL_DATE_FORMAT),
                subscriptionBillingData.getSubscriptionBillDate());
        Assert.assertEquals(priceData,subscriptionBillingData.getPrice());
        Assert.assertEquals(billsList.getBillingDate(),subscriptionBillingData.getBillingDate());
        Assert.assertEquals((String.valueOf(billsList.getDocumentNumber())), subscriptionBillingData.getBillingId());
        Assert.assertEquals((String.valueOf(billsList.getBillItems().size())), subscriptionBillingData.getItems());
    }
}

package de.hybris.platform.sap.saprevenuecloudorder.populators.bills.v2;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.MonetaryAmount;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Product;
import de.hybris.platform.sap.saprevenuecloudorder.populators.bill.v2.DefaultBillSummaryPopulator;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class DefaultBillSummaryPopulatorTest {

    @Mock
    CommonI18NService mockCommonI18NService;

    @Mock
    PriceDataFactory mockPriceDataFactory;

    @InjectMocks
    DefaultBillSummaryPopulator<Bill, SubscriptionBillingData> mockDefaultBillSummaryPopulatorTest;

    private SubscriptionBillingData subscriptionBillingData = new SubscriptionBillingData();
    private PriceData priceData = new PriceData();
    private CurrencyModel currencyModel = new CurrencyModel();
    private MonetaryAmount netAmount = new MonetaryAmount();
    private MonetaryAmount grossAmount = new MonetaryAmount();
    private MonetaryAmount netCreditAmount = new MonetaryAmount();
    private MonetaryAmount grossCreditAmount = new MonetaryAmount();
    private  Bill bill = new Bill();

    @Test
    public void populate_success_grossAmount(){
        //SetUp
        //NetAmount

        grossAmount.setAmount(100f);
        //Currency Model
        currencyModel.setIsocode("USD");

        //Bills List
        bill.setNetAmount(netAmount);
        bill.setBillingDate(new Date());
        bill.setDocumentNumber(12);
        bill.setBillingType("CHARGE");
        bill.setGrossAmount(grossAmount);

        //Product
        Product product = new Product();
        product.setId("SB_Product");
        product.setName("SB_Product");
        //Bill Item
        BillItem billItem = new BillItem();
        billItem.setNetAmount(netAmount);
        billItem.setGrossAmount(grossAmount);
        billItem.setProduct(product);
        List<BillItem> billItemList = new LinkedList<>();
        billItemList.add(billItem);
        bill.setBillItems(billItemList);

        //price data
        priceData.setValue(BigDecimal.valueOf(100.00));

        //Mocks

        MockitoAnnotations.initMocks(this);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class))).thenReturn(priceData);

        //Execute
        mockDefaultBillSummaryPopulatorTest.populate(bill,subscriptionBillingData);


        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(bill.getBillingDate(),
                subscriptionBillingData.getSubscriptionBillDate());
        Assert.assertEquals(priceData,subscriptionBillingData.getPrice());
        Assert.assertEquals(SapRevenueCloudSubscriptionUtil.dateToString(bill.getBillingDate(),SUBSCRIPTION_BILL_DATE_FORMAT),subscriptionBillingData.getBillingDate());
        Assert.assertEquals((String.valueOf(bill.getDocumentNumber())), subscriptionBillingData.getBillingId());
        Assert.assertEquals((String.valueOf(bill.getBillItems().size())), subscriptionBillingData.getItems());
    }


    @Test
    public void populate_success_netAmount(){
        //SetUp
        //NetAmount
        netAmount.setAmount(100f);

        //Currency Model
        currencyModel.setIsocode("USD");

        //Bills List
        bill.setNetAmount(netAmount);
        bill.setBillingDate(new Date());
        bill.setDocumentNumber(12);
        bill.setBillingType("CHARGE");


        //Product
        Product product = new Product();
        product.setId("SB_Product");
        product.setName("SB_Product");
        //Bill Item
        BillItem billItem = new BillItem();
        billItem.setNetAmount(netAmount);
        billItem.setProduct(product);
        List<BillItem> billItemList = new LinkedList<>();
        billItemList.add(billItem);
        bill.setBillItems(billItemList);

        //price data
        priceData.setValue(BigDecimal.valueOf(100.00));

        //Mocks

        MockitoAnnotations.initMocks(this);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class))).thenReturn(priceData);

        //Execute
        mockDefaultBillSummaryPopulatorTest.populate(bill,subscriptionBillingData);


        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(bill.getBillingDate(),
                subscriptionBillingData.getSubscriptionBillDate());
        Assert.assertEquals(priceData,subscriptionBillingData.getPrice());
        Assert.assertEquals(SapRevenueCloudSubscriptionUtil.dateToString(bill.getBillingDate(),SUBSCRIPTION_BILL_DATE_FORMAT),subscriptionBillingData.getBillingDate());
        Assert.assertEquals((String.valueOf(bill.getDocumentNumber())), subscriptionBillingData.getBillingId());
        Assert.assertEquals((String.valueOf(bill.getBillItems().size())), subscriptionBillingData.getItems());
    }

    @Test
    public void populate_success_nwtCreditAmount(){
        //SetUp
        //NetAmount
        netCreditAmount.setAmount(100f);
        netAmount.setAmount(100f);

        //Currency Model
        currencyModel.setIsocode("USD");

        //Bills List
        bill.setNetCreditAmount(netCreditAmount);
        bill.setBillingDate(new Date());
        bill.setDocumentNumber(12);
        bill.setBillingType("CREDIT");


        //Product
        Product product = new Product();
        product.setId("SB_Product");
        product.setName("SB_Product");
        //Bill Item
        BillItem billItem = new BillItem();
        billItem.setNetAmount(netAmount);
        billItem.setProduct(product);
        List<BillItem> billItemList = new LinkedList<>();
        billItemList.add(billItem);
        bill.setBillItems(billItemList);

        //price data
        priceData.setValue(BigDecimal.valueOf(100.00));

        //Mocks

        MockitoAnnotations.initMocks(this);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class))).thenReturn(priceData);

        //Execute
        mockDefaultBillSummaryPopulatorTest.populate(bill,subscriptionBillingData);


        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(bill.getBillingDate(),
                subscriptionBillingData.getSubscriptionBillDate());
        Assert.assertEquals(priceData,subscriptionBillingData.getPrice());
        Assert.assertEquals(SapRevenueCloudSubscriptionUtil.dateToString(bill.getBillingDate(),SUBSCRIPTION_BILL_DATE_FORMAT),subscriptionBillingData.getBillingDate());
        Assert.assertEquals((String.valueOf(bill.getDocumentNumber())), subscriptionBillingData.getBillingId());
        Assert.assertEquals((String.valueOf(bill.getBillItems().size())), subscriptionBillingData.getItems());
    }

    @Test
    public void populate_success_grossCreditAmount(){
        //SetUp
        //NetAmount
        grossCreditAmount.setAmount(100f);
        grossAmount.setAmount(100f);
        //Currency Model
        currencyModel.setIsocode("USD");

        //Bills List

        bill.setBillingDate(new Date());
        bill.setDocumentNumber(12);
        bill.setBillingType("CREDIT");
        bill.setGrossCreditAmount(grossCreditAmount);

        //Product
        Product product = new Product();
        product.setId("SB_Product");
        product.setName("SB_Product");
        //Bill Item
        BillItem billItem = new BillItem();
        billItem.setNetAmount(netAmount);
        billItem.setGrossAmount(grossAmount);
        billItem.setProduct(product);
        List<BillItem> billItemList = new LinkedList<>();
        billItemList.add(billItem);
        bill.setBillItems(billItemList);

        //price data
        priceData.setValue(BigDecimal.valueOf(100.00));

        //Mocks

        MockitoAnnotations.initMocks(this);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class))).thenReturn(priceData);

        //Execute
        mockDefaultBillSummaryPopulatorTest.populate(bill,subscriptionBillingData);


        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(bill.getBillingDate(),
                subscriptionBillingData.getSubscriptionBillDate());
        Assert.assertEquals(priceData,subscriptionBillingData.getPrice());
        Assert.assertEquals(SapRevenueCloudSubscriptionUtil.dateToString(bill.getBillingDate(),SUBSCRIPTION_BILL_DATE_FORMAT),subscriptionBillingData.getBillingDate());
        Assert.assertEquals((String.valueOf(bill.getDocumentNumber())), subscriptionBillingData.getBillingId());
        Assert.assertEquals((String.valueOf(bill.getBillItems().size())), subscriptionBillingData.getItems());
    }
}
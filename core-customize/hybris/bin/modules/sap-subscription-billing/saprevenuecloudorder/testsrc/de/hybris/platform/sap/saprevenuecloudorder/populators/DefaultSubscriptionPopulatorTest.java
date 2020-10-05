package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.saprevenuecloudproduct.enums.RatePlanElementType;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.populators.subscription.DefaultSubscriptionPopulator;
import de.hybris.platform.sap.saprevenuecloudorder.service.BillService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ItemModelInternalContext;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriptionPopulatorTest {


    @Mock
    private SapRevenueCloudProductService sapRevenueCloudProductService;
    @Mock
    private PriceDataFactory priceDataFactory;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private BillService billService;
    @Mock
    private ItemModelInternalContext itemModelInternalContext;
    @Mock
    private UsageUnitModel unitModel;


    @InjectMocks
    DefaultSubscriptionPopulator <Subscription,SubscriptionData > defaultSubscriptionPopulator;

    private static final String SUBSCRIPTION_ID = "sub123";
    private static final String ISO_CODE ="iso_code";
    private static final String METRIC_ID ="metricId";
    private static final String USAGE = "usage";
    private static final String UNIT_NAME_PLURAL = "unitData_plural";
    private static final String UNIT_DATA_NAME = "unit_data_name";
    private static final String PE_MODEL_NAME = "pe_model_name";
    private static final Float QUANTITY_VAL = 20f;
    private static final BigDecimal priceVal = new BigDecimal("12387123");


    private static List<Bill> billList = new ArrayList<>();
    private static List<BillItem> billItemList = new ArrayList<>();
    private static List<Charge> chargeList = new ArrayList<>();

    private static PaginationResult<List<Bill>> billsPage = new PaginationResult();
    private static Subscription subscription = new Subscription();
    private static de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Subscription billSubscription = new de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Subscription();
    private static SubscriptionData subscriptionData = new SubscriptionData();
    private static Bill bill = new Bill();
    private static BillItem billItem = new BillItem();
    private static Charge charge = new Charge();
    private static RatingPeriod ratingPeriod = new RatingPeriod();
    private static SAPRatePlanElementModel planElementModel = new SAPRatePlanElementModel();
    private static Quantity consumedQuantity = new Quantity();
    private static PriceData priceData = new PriceData();
    private static RatePlanElementType ratePlanElementType;
    private static MonetaryAmount monetaryAmount = new MonetaryAmount();
    private static CurrencyModel currencyModel = new CurrencyModel();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Date startDate;
    private static Date endDate;
    private static Calendar calendar = Calendar.getInstance();
    private static Credit credit = new Credit();
    private static List<Credit> creditList = new ArrayList<>();



    @Before
    public void setUp(){



    }




    @Test
    public void testPopulate_GrossCharge() {

        try {
            startDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Failed to parse date ");
        }

        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        ratingPeriod.setStart(startDate);
        ratingPeriod.setEnd(endDate);

        priceData.setValue(priceVal);
        currencyModel.setIsocode(ISO_CODE);
        billSubscription.setId(SUBSCRIPTION_ID);

        monetaryAmount.setAmount(QUANTITY_VAL);
        consumedQuantity.setValue(QUANTITY_VAL);
        charge.setRatingPeriod(ratingPeriod);
        charge.setMetricId(METRIC_ID);
        charge.setGrossAmount(monetaryAmount);
        charge.setConsumedQuantity(consumedQuantity);
        chargeList.add(charge);

        billItem.setSubscription(billSubscription);
        billItem.setCharges(chargeList);
        billItemList.add(billItem);
        bill.setBillItems(billItemList);
        billList.add(bill);

        planElementModel.setName(PE_MODEL_NAME);
        planElementModel.setType(ratePlanElementType.USAGE);

        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        billsPage.setResult(billList);

        //mock
        try {
            when( billService.getBillsPageBySubscriptionId(any(), any(), any(), any(), any(), any())).thenReturn(billsPage);
        } catch (SubscriptionServiceException e) {

            fail("Error : failed to mock BillService ");
        }
        when(sapRevenueCloudProductService.getRatePlanElementfromId(any())).thenReturn(planElementModel);
        when(sapRevenueCloudProductService.getUsageUnitfromId(any(String.class))).thenReturn(unitModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(priceDataFactory.create(any(),any(BigDecimal.class),any(String.class))).thenReturn(priceData);
        when(unitModel.getNamePlural()).thenReturn(UNIT_NAME_PLURAL);

        //execute
        defaultSubscriptionPopulator.populate(subscription,subscriptionData);

        //Verify
        Assert.assertEquals(startDate,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getRatingPeriod().getStart());
        Assert.assertEquals(endDate,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getRatingPeriod().getEnd());
        Assert.assertEquals(METRIC_ID,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getMetricId());
        Assert.assertEquals(QUANTITY_VAL,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getGrossAmount().getAmount());

    }

    @Test
    public void testPopulate_NetCharge() {

        try {
            startDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Failed to parse date ");
        }

        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        ratingPeriod.setStart(startDate);
        ratingPeriod.setEnd(endDate);

        priceData.setValue(priceVal);
        currencyModel.setIsocode(ISO_CODE);
        billSubscription.setId(SUBSCRIPTION_ID);

        monetaryAmount.setAmount(QUANTITY_VAL);
        consumedQuantity.setValue(QUANTITY_VAL);
        charge.setRatingPeriod(ratingPeriod);
        charge.setMetricId(METRIC_ID);
        charge.setNetAmount(monetaryAmount);
        charge.setConsumedQuantity(consumedQuantity);
        chargeList.add(charge);

        billItem.setSubscription(billSubscription);
        billItem.setCharges(chargeList);
        billItemList.add(billItem);
        bill.setBillItems(billItemList);
        billList.add(bill);

        planElementModel.setName(PE_MODEL_NAME);
        planElementModel.setType(ratePlanElementType.USAGE);

        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        billsPage.setResult(billList);

        //mock
        try {
            when( billService.getBillsPageBySubscriptionId(any(), any(), any(), any(), any(), any())).thenReturn(billsPage);
        } catch (SubscriptionServiceException e) {

            fail("Error : failed to mock BillService ");
        }
        when(sapRevenueCloudProductService.getRatePlanElementfromId(any())).thenReturn(planElementModel);
        when(sapRevenueCloudProductService.getUsageUnitfromId(any(String.class))).thenReturn(unitModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(priceDataFactory.create(any(),any(BigDecimal.class),any(String.class))).thenReturn(priceData);
        when(unitModel.getNamePlural()).thenReturn(UNIT_NAME_PLURAL);
        //execute
        defaultSubscriptionPopulator.populate(subscription,subscriptionData);

        //Verify
        Assert.assertEquals(startDate,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getRatingPeriod().getStart());
        Assert.assertEquals(endDate,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getRatingPeriod().getEnd());
        Assert.assertEquals(METRIC_ID,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getMetricId());
        Assert.assertEquals(QUANTITY_VAL,billsPage.getResult().get(0).getBillItems().get(0).getCharges().get(0).getNetAmount().getAmount());

    }

    @Test
    public void testPopulate_NetCredit() {

        try {
            startDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Failed to parse date ");
        }

        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        ratingPeriod.setStart(startDate);
        ratingPeriod.setEnd(endDate);

        priceData.setValue(priceVal);
        currencyModel.setIsocode(ISO_CODE);
        billSubscription.setId(SUBSCRIPTION_ID);

        monetaryAmount.setAmount(QUANTITY_VAL);
        consumedQuantity.setValue(QUANTITY_VAL);
        credit.setRatingPeriod(ratingPeriod);
        credit.setMetricId(METRIC_ID);
        credit.setNetAmount(monetaryAmount);
        credit.setConsumedQuantity(consumedQuantity);
        creditList.add(credit);

        billItem.setSubscription(billSubscription);
        billItem.setCredits(creditList);
        billItemList.add(billItem);
        bill.setBillItems(billItemList);
        billList.add(bill);

        planElementModel.setName(PE_MODEL_NAME);
        planElementModel.setType(ratePlanElementType.USAGE);

        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        billsPage.setResult(billList);

        //mock
        try {
            when( billService.getBillsPageBySubscriptionId(any(), any(), any(), any(), any(), any())).thenReturn(billsPage);
        } catch (SubscriptionServiceException e) {

            fail("Error : failed to mock BillService ");
        }
        when(sapRevenueCloudProductService.getRatePlanElementfromId(any())).thenReturn(planElementModel);
        when(sapRevenueCloudProductService.getUsageUnitfromId(any(String.class))).thenReturn(unitModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(priceDataFactory.create(any(),any(BigDecimal.class),any(String.class))).thenReturn(priceData);
        when(unitModel.getNamePlural()).thenReturn(UNIT_NAME_PLURAL);
        //execute
        defaultSubscriptionPopulator.populate(subscription,subscriptionData);

        //Verify
        Assert.assertEquals(startDate,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getRatingPeriod().getStart());
        Assert.assertEquals(endDate,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getRatingPeriod().getEnd());
        Assert.assertEquals(METRIC_ID,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getMetricId());
        Assert.assertEquals(QUANTITY_VAL,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getNetAmount().getAmount());

    }

    @Test
    public void testPopulate_GrossCredit() {

        try {
            startDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Failed to parse date ");
        }

        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        ratingPeriod.setStart(startDate);
        ratingPeriod.setEnd(endDate);

        priceData.setValue(priceVal);
        currencyModel.setIsocode(ISO_CODE);
        billSubscription.setId(SUBSCRIPTION_ID);

        monetaryAmount.setAmount(QUANTITY_VAL);
        consumedQuantity.setValue(QUANTITY_VAL);
        credit.setRatingPeriod(ratingPeriod);
        credit.setMetricId(METRIC_ID);
        credit.setGrossAmount(monetaryAmount);
        credit.setConsumedQuantity(consumedQuantity);
        creditList.add(credit);

        billItem.setSubscription(billSubscription);
        billItem.setCredits(creditList);
        billItemList.add(billItem);
        bill.setBillItems(billItemList);
        billList.add(bill);

        planElementModel.setName(PE_MODEL_NAME);
        planElementModel.setType(ratePlanElementType.USAGE);

        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        billsPage.setResult(billList);

        //mock
        try {
            when( billService.getBillsPageBySubscriptionId(any(), any(), any(), any(), any(), any())).thenReturn(billsPage);
        } catch (SubscriptionServiceException e) {

            fail("Error : failed to mock BillService ");
        }
        when(sapRevenueCloudProductService.getRatePlanElementfromId(any())).thenReturn(planElementModel);
        when(sapRevenueCloudProductService.getUsageUnitfromId(any(String.class))).thenReturn(unitModel);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(priceDataFactory.create(any(),any(BigDecimal.class),any(String.class))).thenReturn(priceData);
        when(unitModel.getNamePlural()).thenReturn(UNIT_NAME_PLURAL);

        //execute
        defaultSubscriptionPopulator.populate(subscription,subscriptionData);

        //Verify
        Assert.assertEquals(startDate,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getRatingPeriod().getStart());
        Assert.assertEquals(endDate,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getRatingPeriod().getEnd());
        Assert.assertEquals(METRIC_ID,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getMetricId());
        Assert.assertEquals(QUANTITY_VAL,billsPage.getResult().get(0).getBillItems().get(0).getCredits().get(0).getGrossAmount().getAmount());

    }
}
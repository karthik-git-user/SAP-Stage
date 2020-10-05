package de.hybris.platform.sap.saprevenuecloudorder.populators.bills.v2;

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
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.RatePlan;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Quantity;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.MonetaryAmount;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Credit;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Product;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.populators.bill.v2.DefaultBillItemPopulator;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DefaultBillItemPopulatorTest {

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
    DefaultBillItemPopulator<BillItem, SubscriptionBillingData> mockdefaultBillItemPopulator;


    private SubscriptionBillingData subscriptionBillingData;
    private Product product = new Product();
    private Subscription subscription = new Subscription();
    private MonetaryAmount netAmount = new MonetaryAmount();
    private PriceData priceData = new PriceData();
    private CurrencyModel currencyModel = new CurrencyModel();
    private RatingPeriod ratingPeriod = new RatingPeriod();
    private Quantity quantity = new Quantity();
    ItemModelContext ctx = ItemContextBuilder.createMockContext(UsageUnitModel.class, Locale.ENGLISH);
    private UsageUnitModel usageUnitModel = new UsageUnitModel(ctx);
    private CurrencyModel currentCurrency = new CurrencyModel();
    private List<Charge> charges = new LinkedList<>();
    private Charge charge = new Charge();
    private CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
    private RatePlan ratePlan = new RatePlan();
    private ProductModel productModel = new ProductModel();
    private SubscriptionPricePlanModel subscriptionPricePlanModel = new SubscriptionPricePlanModel();
    private SAPRatePlanElementModel planElementModel = new SAPRatePlanElementModel();
    private BillItem billItem = new BillItem();
    private List<Credit> credits = new LinkedList<>();
    private Credit credit = new Credit();
    private MonetaryAmount grossAmount = new MonetaryAmount();
    @Before
    public void setup(){

        subscriptionBillingData = new SubscriptionBillingData();

        //Product
        product.setCode("19302");

        //Subscription
        subscription.setDocumentNumber(12);

        //NetAmount
        netAmount.setAmount(290f);

        //Currency Model
        currencyModel.setIsocode("USD");

        //Populate Billing Charges
        //Rating period
        ratingPeriod.setStart(new Date());
        ratingPeriod.setEnd(new Date());

        //Currency Mode
        currentCurrency.setIsocode("USD");

        //Quantity
        quantity.setValue(1f);
        //Charge
        charge.setRatingPeriod(ratingPeriod);
        charge.setConsumedQuantity(quantity);
        charges.add(charge);

        //Credit
        credit.setRatingPeriod(ratingPeriod);
        credit.setConsumedQuantity(quantity);
        credits.add(credit);

        //RatePlan
        ratePlan.setId("162536AB52617");

        //Subscription Price plan model
        subscriptionPricePlanModel.setProduct(productModel);

        //Gross Amount
        grossAmount.setAmount(100f);

        //Bill
        billItem.setProduct(product);
        billItem.setSubscription(subscription);
        billItem.setNetAmount(netAmount);
        billItem.setRatePlan(ratePlan);
        billItem.setCharges(charges);
        billItem.setCredits(credits);
        billItem.setGrossAmount(grossAmount);


        //SAP rate plan element model
        planElementModel.setName("SomeName");
        planElementModel.setType(RatePlanElementType.USAGE);

        //price data
        priceData.setValue(BigDecimal.valueOf(100.00));

        //Mocks
        MockitoAnnotations.initMocks(this);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY),any(BigDecimal.class),any(String.class))).thenReturn(priceData);
        when(mockCmsSiteService.getCurrentCatalogVersion()).thenReturn(catalogVersionModel);
        when(mockSapRevenueCloudProductService.getSubscriptionPricePlanForId(any(String.class),any(CatalogVersionModel.class))).thenReturn(subscriptionPricePlanModel);
        doNothing().when(mockProductModelProductDataPopulator).populate(eq(productModel),any(ProductData.class));
        when(mockSapRevenueCloudProductService.getUsageUnitfromId(any(String.class))).thenReturn(usageUnitModel);
        when(mockCommonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(mockPriceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), eq("USD"))).thenReturn(priceData);
        when(mockSapRevenueCloudProductService.getRatePlanElementfromId(any(String.class))).thenReturn(planElementModel);
    }
    @Test
    public void populate_success_CQ1() {
        //Setup
        //Consumed Quantity
        quantity.setValue(1f);

        //Usage Unit Model
        usageUnitModel.setNamePlural("NamePlural", Locale.ENGLISH);
        usageUnitModel.setName("SomeName",Locale.ENGLISH);

        //Execute
        mockdefaultBillItemPopulator.populate(billItem, subscriptionBillingData);

        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(product.getCode(), subscriptionBillingData.getProductCode());
        Assert.assertEquals(String.valueOf(subscription.getDocumentNumber()), subscriptionBillingData.getSubscriptionId());
        Assert.assertEquals(priceData, subscriptionBillingData.getPrice());

        List<UsageChargeData> usageChargeDataList = subscriptionBillingData.getCharges();
        Assert.assertEquals(charges.size(), usageChargeDataList.size());
        for(UsageChargeData usageChargeData: usageChargeDataList){
            Assert.assertEquals(charge.getConsumedQuantity().getValue(),usageChargeData.getUsage());
            Assert.assertEquals(usageUnitModel.getName(),usageChargeData.getName());
            Assert.assertEquals(usageUnitModel.getNamePlural(),usageChargeData.getUsageUnit().getId());
            Assert.assertEquals(planElementModel.getName(),usageChargeData.getName());
            Assert.assertEquals(charge.getRatingPeriod().getEnd(),usageChargeData.getToDate());
            Assert.assertEquals(charge.getRatingPeriod().getStart(),usageChargeData.getFromDate());
            Assert.assertEquals(credit.getConsumedQuantity().getValue(),usageChargeData.getUsage());
            Assert.assertEquals(credit.getRatingPeriod().getEnd(),usageChargeData.getToDate());
            Assert.assertEquals(credit.getRatingPeriod().getStart(),usageChargeData.getFromDate());
        }
    }
    @Test
    public void populate_success_CQ0() {
        //Setup
        //Consumed Quantity
        quantity.setValue(0f);

        //Usage Unit Model
        usageUnitModel.setNamePlural("SomeName", Locale.ENGLISH);
        usageUnitModel.setName("SomeName",Locale.ENGLISH);

        //Execute
        mockdefaultBillItemPopulator.populate(billItem, subscriptionBillingData);

        //Verify
        Assert.assertNotNull(subscriptionBillingData);
        Assert.assertEquals(product.getCode(), subscriptionBillingData.getProductCode());
        Assert.assertEquals(String.valueOf(subscription.getDocumentNumber()), subscriptionBillingData.getSubscriptionId());
        Assert.assertEquals(priceData, subscriptionBillingData.getPrice());

        List<UsageChargeData> usageChargeDataList = subscriptionBillingData.getCharges();
        Assert.assertEquals(charges.size(), usageChargeDataList.size());
        for(UsageChargeData usageChargeData: usageChargeDataList){
            Assert.assertEquals(charge.getConsumedQuantity().getValue(),usageChargeData.getUsage());
            Assert.assertEquals(usageUnitModel.getName(),usageChargeData.getName());
            Assert.assertEquals(usageUnitModel.getNamePlural(),usageChargeData.getUsageUnit().getId());
            Assert.assertEquals(planElementModel.getName(),usageChargeData.getName());
            Assert.assertEquals(charge.getRatingPeriod().getEnd(),usageChargeData.getToDate());
            Assert.assertEquals(charge.getRatingPeriod().getStart(),usageChargeData.getFromDate());
        }
    }

}
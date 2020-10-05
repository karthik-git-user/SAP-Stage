package de.hybris.platform.sap.saprevenuecloudorder.populators.subscription;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.data.MetaData;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DefaultSubscriptionSummaryPopulatorTest {

    @Mock
    SapRevenueCloudProductService mockRevenueCloudProductService;

    @Mock
    Populator<ProductModel, ProductData> mockProductUrlPopulator;

    @Mock
    CMSSiteService mockCmsSiteService;

    @Mock
    CommonI18NService mockCommonI18NService;

    @Mock
    SapRevenueCloudProductService sapRevenueCloudProductService;

    @Mock
    SubscriptionService mockSubscriptionService;

    @Mock
    Populator<ProductModel, ProductData> mockSubscriptionProductPricePlanPopulator;


    @Mock
    Populator<SubscriptionPricePlanModel, ProductData> mockSbSubscriptionPricePopulator;
    @Spy
    Map<String, SubscriptionStatus> subscriptionStatusMap = new HashMap<>();

    @Spy
    Map<String, String> billingFrequencyMap = new HashMap<>();

    @InjectMocks
    DefaultSubscriptionSummaryPopulator<Subscription, SubscriptionData> defaultSubscriptionSummaryPopulator = new DefaultSubscriptionSummaryPopulator<Subscription, SubscriptionData>();



    private static final String SUBSCRIPTION_ID = "E2F44F08-DA57-44A2-B0A4-13CDBE60541B";
    private static final String DOCUMENT_NO = "24";
    private static final Date SUBSCRIPTION_VALID_UNTIL_DATE = new Date();
    private static final Date WITHDRAWAL_PERIOD_END_DATE = new Date();
    private static final Date RENEWAL_TERM_END_DATE = new Date();
    private static final String METADATA_VERSION = "2";
    private static final String PRODUCT_CODE = "SomeProductCode";
    private static final String RATE_PLAN_ID = "1327373634746374";
    private static final Date WITHDRAWN_AT = new Date();
    private static final String SUBSCRIPTION_STATUS = "Active";
    private static final String PRODUCT_NAME = "SomeSubscriptionProductName";
    private static final String BILLING_PLAN_ID = "anniversary_yearly";



    private Subscription source;

    @Before
    @SuppressWarnings({"removal"})
    public void setup(){
        //<editor-fold desc="List of Snapshots">
        //Product
        Product product = new Product();
        product.setCode(PRODUCT_CODE);

        //RatePlan
        RatePlan ratePlan = new RatePlan();
        ratePlan.setId(RATE_PLAN_ID);

        //Pricing
        Pricing pricing = new Pricing();
        pricing.setPricingDate(new Date());

        //List of Item
        List<Item> items = new LinkedList<>();
        Item item = new Item();
        item.setProduct(product);
        item.setRatePlan(ratePlan);
        item.setPricing(pricing);
        items.add(item);

        //List of Snapshot
        List<Snapshot> snapshots = new LinkedList<>();
        Snapshot snapshot = new Snapshot();
        snapshot.setItems(items);
        snapshot.setEffectiveDate(new Date());
        snapshots.add(snapshot);
        //</editor-fold>

        //Customer
        Customer customer = new Customer();
        customer.setId("1234");

        //MetaData
        Metadata metadata = new Metadata();
        metadata.setVersion(1);

        //RenewalTerm
        RenewalTerm renewalTerm = new RenewalTerm();
        renewalTerm.setEndDate(RENEWAL_TERM_END_DATE);

        //Payment
        Payment payment = new Payment();

        //CancellationPolicy
        CancellationPolicy cancellationPolicy = new CancellationPolicy();
        cancellationPolicy.setWithdrawalPeriodEndDate(WITHDRAWAL_PERIOD_END_DATE);

        // Map<String, SubscriptionStatus>
        subscriptionStatusMap.put("Active", SubscriptionStatus.ACTIVE);
        subscriptionStatusMap.put("Canceled", SubscriptionStatus.CANCELLED);
        subscriptionStatusMap.put("Expired", SubscriptionStatus.EXPIRED);
        subscriptionStatusMap.put("Not Started", SubscriptionStatus.NOT_STARTED);
        subscriptionStatusMap.put("Withdrawn", SubscriptionStatus.WITHDRAWN);

        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setId("1234");
        //Source Subscription
        Subscription subscription = new Subscription();
        subscription.setSnapshots(snapshots);
        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        subscription.setValidUntil(SUBSCRIPTION_VALID_UNTIL_DATE);
        subscription.setRenewalTerm(renewalTerm);
        subscription.setPayment(payment);
        subscription.setCancellationPolicy(cancellationPolicy);
        subscription.setWithdrawnAt(WITHDRAWN_AT);
        subscription.setStatus(SUBSCRIPTION_STATUS);
        subscription.setDocumentNumber(1);
        subscription.setMetadata(metadata);
        subscription.setCustomer(businessPartner);




        this.source = subscription;
    }

    @Test
    public void populate_success() throws SubscriptionServiceException {
        //<editor-fold desc="Mocks">

        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        catalogVersionModel.setActive(true);
        catalogVersionModel.setVersion("Online");
        catalogVersionModel.setCategorySystemID("electronicsProductCatalog");

        CatalogModel catalogModel = new CatalogModel();
        catalogModel.setId("electronicsProductCatalog");
        catalogModel.setDefaultCatalog(true);
        catalogModel.setActiveCatalogVersion(catalogVersionModel);
        catalogVersionModel.setCatalog(catalogModel);


        CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
        catalogVersionModel1.setActive(true);
        catalogVersionModel1.setVersion("Online");
        catalogVersionModel1.setCategorySystemID("powertoolsProductCatalog");

        CatalogModel catalogModel1 = new CatalogModel();
        catalogModel.setId("powertoolsProductCatalog");
        catalogModel.setDefaultCatalog(false);
        catalogModel.setActiveCatalogVersion(catalogVersionModel1);
        catalogVersionModel1.setCatalog(catalogModel1);

        List<CatalogVersionModel> catalogVersions = new ArrayList<>();
        catalogVersions.add(catalogVersionModel);
        catalogVersions.add(catalogVersionModel1);
        ItemModelContext catalogCtx = ItemContextBuilder.createMockContext(BillingFrequencyModel.class, Locale.US);
        BillingFrequencyModel billingFrequencyModel = new BillingFrequencyModel(catalogCtx);
        billingFrequencyModel.setNameInCart("onetime ",Locale.US);

        //BillingPlanModel
        BillingPlanModel billingPlanModel = new BillingPlanModel();
        billingPlanModel.setId(BILLING_PLAN_ID);
        billingPlanModel.setBillingFrequency(billingFrequencyModel);

        //SubscriptionTermModel
        SubscriptionTermModel subscriptionTerm = new SubscriptionTermModel();
        subscriptionTerm.setBillingPlan(billingPlanModel);

        //ProductModel
        ItemModelContext ctx = ItemContextBuilder.createMockContext(ProductModel.class, Locale.US);

        ProductModel productModel = new ProductModel(ctx);
        productModel.setName(PRODUCT_NAME, Locale.ENGLISH);
        productModel.setCode(PRODUCT_NAME);
        productModel.setSubscriptionTerm(subscriptionTerm);
        productModel.setCatalogVersion(catalogVersionModel1);



        //ProductData

        ProductData productData = new ProductData();
        productData.setPrice(new SubscriptionPricePlanData());
        productData.setUrl("productUrl");


        //SubscriptionPricePlanModel
       // MockitoAnnotations.initMocks(this);
        SubscriptionPricePlanModel pricePlanModel = new SubscriptionPricePlanModel();
       // Mockito.doReturn(pricePlanModel).when(pricePlanModel.getProduct()).getName();
        pricePlanModel.setProduct(productModel);
        pricePlanModel.setPricePlanId("1327373634746374");
        pricePlanModel.setCatalogVersion(catalogVersionModel1);


        List<CatalogModel> catalogModels = new ArrayList<>();
        catalogModels.add(catalogModel);
        catalogModels.add(catalogModel1);


        //Mocks

        MockitoAnnotations.initMocks(this);

        when(mockCmsSiteService.getCurrentSite()).thenReturn(null);
        when(mockCmsSiteService.getAllCatalogs(any())).thenReturn(catalogModels);

        when(mockRevenueCloudProductService.getSubscriptionPricesWithEffectiveDate( any(), Collections.singletonList(any()), any(Date.class) ) )
                .thenReturn(pricePlanModel);
        when(mockSubscriptionService.getBillingFrequency(any(ProductModel.class))).thenReturn(billingFrequencyModel);

        //</editor-fold>

        //Execute
        SubscriptionData target = new SubscriptionData();
        defaultSubscriptionSummaryPopulator.setSapRevenueCloudProductService(mockRevenueCloudProductService);

        defaultSubscriptionSummaryPopulator.setProductUrlPopulator(mockProductUrlPopulator);
        doNothing().when( mockProductUrlPopulator ).populate(any(ProductModel.class), any(ProductData.class));

        defaultSubscriptionSummaryPopulator.setSbSubscriptionPricePopulator(mockSbSubscriptionPricePopulator);
        doNothing().when(mockSbSubscriptionPricePopulator).populate(any(SubscriptionPricePlanModel.class),any(ProductData.class));

        defaultSubscriptionSummaryPopulator.populate(this.source, target);

    }
}



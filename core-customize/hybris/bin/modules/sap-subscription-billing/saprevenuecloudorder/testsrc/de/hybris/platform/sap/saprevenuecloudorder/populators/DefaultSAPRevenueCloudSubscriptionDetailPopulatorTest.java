/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudSubscriptionDetailPopulatorTest {

    @Mock
    CMSSiteService mockCmsSiteService;

    @Mock
    SapRevenueCloudSubscriptionService mockSapRevenueCloudSubscriptionService;

    @Mock
    SapRevenueCloudProductService mockSapRevenueCloudProductService;

    @Mock
    Populator<ProductModel, ProductData> mockProductUrlPopulator;

    @Spy
    Map<String, SubscriptionStatus> subscriptionStatusMap = new HashMap<>();

    @InjectMocks
    DefaultSAPRevenueCloudSubscriptionDetailPopulator<Subscription, SubscriptionData> defaultSAPRevenueCloudSubscriptionDetailPopulator;


    private static final String SUBSCRIPTION_ID = "E2F44F08-DA57-44A2-B0A4-13CDBE60541B";
    private static final String DOCUMENT_NO = "24";
    private static final String SUBSCRIPTION_VALID_UNTIL_DATE = "2017-01-01T10:00:00.000Z";
    private static final String WITHDRAWAL_PERIOD_END_DATE = "2017-01-01T10:00:00.000Z";
    private static final String RENEWAL_TERM_END_DATE = "2017-01-01T10:00:00.000Z";
    private static final String METADATA_VERSION = "2";
    private static final String PRODUCT_CODE = "SomeProductCode";
    private static final String RATE_PLAN_ID = "SomeRatePlanId";
    private static final String WITHDRAWN_AT = "2017-01-01T10:00:00.000Z";
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

        //List of Item
        List<Item> items = new LinkedList<>();
        Item item = new Item();
        item.setProduct(product);
        item.setRatePlan(ratePlan);
        items.add(item);

        //List of Snapshot
        List<Snapshot> snapshots = new LinkedList<>();
        Snapshot snapshot = new Snapshot();
        snapshot.setItems(items);
        snapshots.add(snapshot);
        //</editor-fold>

        //Customer
        Customer customer = new Customer();

        //MetaData
        MetaData metadata = new MetaData();
        metadata.setVersion(METADATA_VERSION);

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

        //Source Subscription
        Subscription subscription = new Subscription();
        subscription.setSnapshots(snapshots);
        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        subscription.setDocumentNumber(DOCUMENT_NO);
        subscription.setCustomer(customer);
        subscription.setValidUntil(SUBSCRIPTION_VALID_UNTIL_DATE);
        subscription.setMetaData(metadata);
        subscription.setRenewalTerm(renewalTerm);
        subscription.setPayment(payment);
        subscription.setCancellationPolicy(cancellationPolicy);
        subscription.setWithdrawnAt(WITHDRAWN_AT);
        subscription.setStatus(SUBSCRIPTION_STATUS);

        this.source = subscription;
    }

    @Test
    public void populate_success() {
        //<editor-fold desc="Mocks">
        CatalogVersionModel currentCatalog = new CatalogVersionModel();

        //BillingPlanModel
        BillingPlanModel billingPlanModel = new BillingPlanModel();
        billingPlanModel.setId(BILLING_PLAN_ID);

        //SubscriptionTermModel
        SubscriptionTermModel subscriptionTerm = new SubscriptionTermModel();
        subscriptionTerm.setBillingPlan(billingPlanModel);

        //ProductModel
        ItemModelContext ctx = ItemContextBuilder.createMockContext(ProductModel.class, Locale.US);
        ProductModel productModel = new ProductModel(ctx);
        productModel.setName(PRODUCT_NAME, Locale.US);
        productModel.setSubscriptionTerm(subscriptionTerm);

        //ProductData
        ProductData productData = new ProductData();
        productData.setPrice(new SubscriptionPricePlanData());

        //SubscriptionPricePlanModel
        SubscriptionPricePlanModel pricePlanModel = new SubscriptionPricePlanModel();
        pricePlanModel.setProduct(productModel);

        //Mocks
        defaultSAPRevenueCloudSubscriptionDetailPopulator.setSubscriptionProductPricePlanPopulator(mockProductUrlPopulator);
        when(mockCmsSiteService.getCurrentCatalogVersion())
                .thenReturn(currentCatalog);
        when(mockSapRevenueCloudProductService.getSubscriptionPricePlanForId( eq(RATE_PLAN_ID), eq(currentCatalog)) )
                .thenReturn(pricePlanModel);
        doNothing()
                .when(mockProductUrlPopulator).populate(any(ProductModel.class), any(ProductData.class));
        when(mockSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(eq(SUBSCRIPTION_ID), any(String.class)))
                .thenReturn(Collections.emptyList());
        //</editor-fold>

        //Execute
        SubscriptionData target = new SubscriptionData();
        defaultSAPRevenueCloudSubscriptionDetailPopulator.populate(this.source, target);



    }
}


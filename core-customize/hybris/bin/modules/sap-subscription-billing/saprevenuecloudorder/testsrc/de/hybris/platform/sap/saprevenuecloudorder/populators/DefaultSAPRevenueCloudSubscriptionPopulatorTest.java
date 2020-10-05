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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudSubscriptionPopulatorTest {

    @Mock
    UserService mockUserService;

    @Mock
    SapRevenueCloudProductService mockRevenueCloudProductService;

    @Mock
    Populator<ProductModel, ProductData> mockProductUrlPopulator;

    @Mock
    CMSSiteService mockCmsSiteService;

    @Spy
    Map<String, SubscriptionStatus> subscriptionStatusMap = new HashMap<>();

    @InjectMocks
    DefaultSAPRevenueCloudSubscriptionPopulator defaultSAPRevenueCloudSubscriptionPopulator;


    private static final String SUBSCRIPTION_ID = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";
    private static final String RATE_PLAN_ID = "DF259852-967C-4475-A5F9-6B4AF87A27E7";
    private static final String PRODUCT_NAME = "My Product Name";
    private static final String VALID_FROM = "2016-01-01T05:00:00.000Z";
    private static final String VALID_UNTIL = "2029-01-01T05:00:00.000Z";
    private static final String WITHDRAWAL_PERIOD = "2019-01-01T05:00:00.000Z";
    private static final String WITHDRAWN_AT = "2018-01-01T05:00:00.000Z";
    private static final String DOCUMENT_NUMBER = "24";
    private static final String REVENUE_CUSTOMER_ID = "9105002825";
    private static final String STATUS = "Active";

    @Before
    public void setup(){

        //CustomerModel
        CustomerModel customer = new CustomerModel();
        customer.setRevenueCloudCustomerId(REVENUE_CUSTOMER_ID);

        //ItemContext
        ItemModelContext ctx = ItemContextBuilder.createMockContext(ProductModel.class, Locale.US);

        //Product
        ProductModel product = new ProductModel(ctx);
        product.setName(PRODUCT_NAME);

        //Price Plan Id
        SubscriptionPricePlanModel pricePlanModel = new SubscriptionPricePlanModel();
        pricePlanModel.setProduct(product);

        //CatalogVersionModel
        CatalogVersionModel catalogVersion = new CatalogVersionModel();

        //UserService
        when(mockUserService.getCurrentUser()).thenReturn(customer);

        //CMSSiteService
        when(mockCmsSiteService.getCurrentCatalogVersion()).thenReturn(catalogVersion);

        //SapRevenueCloudProductService
        when(mockRevenueCloudProductService.getSubscriptionPricePlanForId(eq(RATE_PLAN_ID), eq(catalogVersion)))
                .thenReturn(pricePlanModel);

        //Populator<ProductModel, ProductData>
        doNothing().when( mockProductUrlPopulator ).populate(any(ProductModel.class), any(ProductData.class));

        // Map<String, SubscriptionStatus>
        subscriptionStatusMap.put("ACTIVE", SubscriptionStatus.ACTIVE);
        subscriptionStatusMap.put("CANCELED", SubscriptionStatus.CANCELLED);
        subscriptionStatusMap.put("EXPIRED", SubscriptionStatus.EXPIRED);
        subscriptionStatusMap.put("NOT STARTED", SubscriptionStatus.NOT_STARTED);
        subscriptionStatusMap.put("WITHDRAWN", SubscriptionStatus.WITHDRAWN);
    }

    @Test
    @SuppressWarnings({"removal"})
    public void populate_success(){

        //<editor-fold desc="Source Test Data">
        //RatePlan
        RatePlan ratePlan  = new RatePlan();
        ratePlan.setId(RATE_PLAN_ID);

        //List<Item>
        List<Item> items = new LinkedList<>();
        Item item = new Item();
        item.setRatePlan(ratePlan);
        items.add(item);

        //List<Snapshot>
        List<Snapshot> snapshots = new LinkedList<>();
        Snapshot snapshot = new Snapshot();
        snapshot.setItems(items);
        snapshots.add(snapshot);

        //CancellationPolicy
        CancellationPolicy cancellationPolicy = new CancellationPolicy();
        cancellationPolicy.setWithdrawalPeriodEndDate(WITHDRAWAL_PERIOD);

        //Subscription
        Subscription source = new Subscription();
        source.setSubscriptionId(SUBSCRIPTION_ID);
        source.setSnapshots(snapshots);
        source.setValidFrom(VALID_FROM);
        source.setValidUntil(VALID_UNTIL);
        source.setDocumentNumber(DOCUMENT_NUMBER);
        source.setCancellationPolicy(cancellationPolicy);
        source.setWithdrawnAt(WITHDRAWN_AT);
        source.setStatus(STATUS);
        //</editor-fold>

        //Target
        SubscriptionData target = new SubscriptionData();

        //Execute
        defaultSAPRevenueCloudSubscriptionPopulator.populate(source, target);

        //Verify
        Assert.assertEquals(REVENUE_CUSTOMER_ID, target.getCustomerId());
    }
}

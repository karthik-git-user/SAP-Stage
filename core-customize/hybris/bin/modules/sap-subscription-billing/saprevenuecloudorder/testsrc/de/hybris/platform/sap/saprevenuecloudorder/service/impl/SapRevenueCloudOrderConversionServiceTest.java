/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SapRevenueCloudOrderConversionServiceTest {

    @Mock
    B2BUnitService mockB2bUnitService;
    @Mock
    ConfigurationService mockConfigurationService;
    @Mock
    Configuration mockConfiguration;
    @Mock
    CMSSiteService mockCmsSiteService;
    @Mock
    CommonI18NService mockCommonI18NService;
    @Mock
    SubscriptionCommercePriceService mockCommercePriceService;

    @InjectMocks
    SapRevenueCloudOrderConversionService sapRevenueCloudOrderConversionService;

    //Constants
    private static final String SUBSCRIPTION_ID = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";
    private static final String UNIT_CODE = "SomeUnitCode";
    private static final String PRICE_PLAN_ID = "SomePricePlanId";
    private static final Long QUANTITY = 1L;
    private static final Integer ENTRY_NUMBER = 10;

    private final OrderModel orderModel = new OrderModel();
    private final BaseStoreModel baseStoreModel = new BaseStoreModel();
    private final B2BCustomerModel b2BCustomerModel = new B2BCustomerModel();
    private final B2BUnitModel b2BUnit = new B2BUnitModel();
    private final B2BUnitModel rootUnit = new B2BUnitModel();
    private final PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
    private final PaymentInfoModel paymentInfoModel2 = new CreditCardPaymentInfoModel();
    private final BaseSiteModel baseSiteModel = new BaseSiteModel();
    private final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();
    private final CustomerModel customerModel = new CustomerModel();

    private SubscriptionPricePlanModel pricePlanModel;

    @Before
    public void setup() {

        //BaseStoreModel
        baseStoreModel.setUid("electronics");

        //ProductModel
        ItemModelContext ctx = ItemContextBuilder.createMockContext(ProductModel.class, Locale.US);
        ProductModel product = new ProductModel(ctx);
        product.setSubscriptionCode(SUBSCRIPTION_ID);

        //UnitModel
        UnitModel unit = new UnitModel();
        unit.setCode(UNIT_CODE);

        //List<AbstractOrderEntryModel>
        List<AbstractOrderEntryModel> entries = new LinkedList<>();
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        entry.setProduct(product);
        entry.setQuantity(QUANTITY);
        entry.setUnit(unit);
        entry.setEntryNumber(ENTRY_NUMBER);

        //OrderModel
        orderModel.setCode("10101029393948023");
        orderModel.setStore(baseStoreModel);
        orderModel.setPaymentInfo(paymentInfoModel);
        orderModel.setEntries(entries);
        orderModel.setSite(baseSiteModel);
        orderModel.setCurrency(new CurrencyModel());

        //B2BUnitModel
        rootUnit.setUid("19839203ABC283929");
        rootUnit.setRevenueCloudCompanyId("9105002825");

        //SubscriptionPricePlanModel
        SubscriptionPricePlanModel pricePlanModel = new SubscriptionPricePlanModel();
        pricePlanModel.setPricePlanId(PRICE_PLAN_ID);
        this.pricePlanModel = pricePlanModel;

    }

    @Test
    public void convertOrderToSapCpiOrder_b2bCustomer_success() {
        //Setup
        b2BCustomerModel.setUid("some.user@test.com");
        orderModel.setUser(b2BCustomerModel);
        doReturn(b2BUnit).when(mockB2bUnitService).getParent(b2BCustomerModel);
        when(mockB2bUnitService.getRootUnit(b2BUnit)).thenReturn(rootUnit);
        when(mockConfigurationService.getConfiguration()).thenReturn(mockConfiguration);

        //Execute
        SAPCpiOutboundOrderModel result = sapRevenueCloudOrderConversionService.convertOrderToSapCpiOrder(orderModel);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test
    public void convertOrderToSapCpiOrder_b2cCustomer_success() {
        //Setup
        customerModel.setCustomerID("6105002837");
        customerModel.setRevenueCloudCustomerId("9105002825");
        orderModel.setUser(customerModel);
        when(mockConfigurationService.getConfiguration()).thenReturn(mockConfiguration);

        //Execute
        SAPCpiOutboundOrderModel result = sapRevenueCloudOrderConversionService.convertOrderToSapCpiOrder(orderModel);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test
    public void populatePaymentDetails_defaultPayment_success() {
        //Execute
        sapRevenueCloudOrderConversionService.populatePaymentDetails(sapCpiOutboundOrderModel, paymentInfoModel2);
    }

    @Test
    public void populatePaymentDetails_creditCardPaymentInfoModel_success() {
        //SetUp
        when(mockConfigurationService.getConfiguration()).thenReturn(mockConfiguration);

        //Execute
        sapRevenueCloudOrderConversionService.populatePaymentDetails(sapCpiOutboundOrderModel, paymentInfoModel);
    }

    @Test
    public void populateOrderItems_success() {
        //Mocks
        try {
            doNothing()
                    .when(mockCmsSiteService)
                    .setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), eq(true));
        }
        catch (CMSItemNotFoundException e){
            e.getMessage();
        }
        doNothing()
                .when(mockCommonI18NService).setCurrentCurrency(any(CurrencyModel.class));
        when(mockCommercePriceService.getSubscriptionPricePlanForProduct(any(ProductModel.class)))
                .thenReturn(this.pricePlanModel);

        //Execute
        sapRevenueCloudOrderConversionService.populateOrderItems(sapCpiOutboundOrderModel, orderModel);
    }
}
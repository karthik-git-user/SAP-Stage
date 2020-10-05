/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.data.Customer;
import de.hybris.platform.sap.saprevenuecloudorder.data.Market;
import de.hybris.platform.sap.saprevenuecloudorder.data.OrderItem;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionOrder;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSAPRevenueCloudSubscriptionOrderPopulatorTest {

    @Mock
    CMSSiteService mockCmsSiteService;

    @Mock
    CommonI18NService mockCommonI18NService;

    @Mock
    Configuration mockConfiguration;

    @Mock
    ConfigurationService mockConfigurationService;

    @Mock
    SubscriptionCommercePriceService mockSubscriptionCommercePriceService;

    @Mock
    GenericDao<SAPMarketToCatalogMappingModel> mockSapMarketToCatalogMappingModelGenericDao;

    @InjectMocks
    DefaultSAPRevenueCloudSubscriptionOrderPopulator<AbstractOrderModel, SubscriptionOrder> defaultSAPRevenueCloudSubscriptionOrderPopulator;

    private static final String USER_ID = "UID123";
    private static final String CUSTOMER_ID = "CID123";
    private static final String MARKET_ID = "MID123";
    private static final String SUBSCRIPTION_CODE = "SUBCODE";

    private AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
    private SubscriptionOrder subscriptionOrder = new SubscriptionOrder();

    //Catalog Model
    CatalogModel catalogModel = new CatalogModel();

    @Before
    public void setUp(){
        //User Model
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUid(USER_ID);
        customerModel.setRevenueCloudCustomerId(CUSTOMER_ID);

        //List Catalog Model
        List<CatalogModel> catalogModelList = new ArrayList<>();
        catalogModelList.add(catalogModel);

        //Base Store Model
        BaseStoreModel baseStoreModel = new BaseStoreModel();
        baseStoreModel.setCatalogs(catalogModelList);

        //Unit Model
        UnitModel unitModel = new UnitModel();
        unitModel.setCode("CODE123");

        //ProductModel
        ProductModel productModel = new ProductModel();
        productModel.setSubscriptionCode(SUBSCRIPTION_CODE);
        productModel.setPriceQuantity(100d);
        productModel.setUnit(unitModel);

        //AbstractOrderEntryModel
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);

        //AbstractOrderEntryModelList
        List<AbstractOrderEntryModel> abstractOrderEntryModelList = new ArrayList<>();
        abstractOrderEntryModelList.add(abstractOrderEntryModel);

        //BaseSiteModel
        CMSSiteModel cmsSiteModel = new CMSSiteModel();

        //CurrencyModel
        CurrencyModel currencyModel = new CurrencyModel();

        //AbstractOrderModel
        abstractOrderModel.setUser(customerModel);
        abstractOrderModel.setStore(baseStoreModel);
        abstractOrderModel.setEntries(abstractOrderEntryModelList);
        abstractOrderModel.setSite(cmsSiteModel);
        abstractOrderModel.setCurrency(currencyModel);
    }

    @Test
    @SuppressWarnings({"removal"})
    public void populate_success(){
        //Setup

        //SAPMarketToCatalogMappingModel
        SAPMarketToCatalogMappingModel sapMarketToCatalogMappingModel = new SAPMarketToCatalogMappingModel();
        sapMarketToCatalogMappingModel.setMarketId(MARKET_ID);
        sapMarketToCatalogMappingModel.setCatalog(catalogModel);

        //SAPMarketToCatalogMappingModelList
        List<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingList = new ArrayList<>();
        sapMarketToCatalogMappingList.add(sapMarketToCatalogMappingModel);

        //Market
        Market market = new Market();
        market.setMarketId(MARKET_ID);

        //Customer
        Customer customer = new Customer();
        customer.setCustomerNumber(CUSTOMER_ID);

        //SubscriptionPricePlanModel
        SubscriptionPricePlanModel subscriptionPricePlanModel = new SubscriptionPricePlanModel();
        subscriptionPricePlanModel.setPricePlanId("PID123");

        //Mocks
        when(mockSapMarketToCatalogMappingModelGenericDao.find()).thenReturn(sapMarketToCatalogMappingList);
        when(mockConfigurationService.getConfiguration()).thenReturn(mockConfiguration);
        when(mockSubscriptionCommercePriceService.getSubscriptionPricePlanForProduct(any(ProductModel.class))).thenReturn(subscriptionPricePlanModel);

        //Execute
        defaultSAPRevenueCloudSubscriptionOrderPopulator.populate(abstractOrderModel,subscriptionOrder);

        //Verify
        Assert.assertEquals(USER_ID,subscriptionOrder.getOwner());
        Assert.assertEquals(market.getMarketId(),subscriptionOrder.getMarket().getMarketId());
        Assert.assertEquals(customer.getCustomerNumber(),subscriptionOrder.getCustomer().getCustomerNumber());

        List<OrderItem> orderItemList = subscriptionOrder.getOrderItems();
        for(OrderItem orderItem : orderItemList){
            Assert.assertEquals(SaprevenuecloudorderConstants.SUBSCRIPTIONITEM,orderItem.getItemType());
            Assert.assertEquals(abstractOrderModel.getEntries().get(0).getProduct().getSubscriptionCode(),orderItem.getProduct().getId());
            Assert.assertEquals(abstractOrderModel.getEntries().get(0).getProduct().getPriceQuantity().toString(),orderItem.getQuantity().getValue());
            Assert.assertEquals(abstractOrderModel.getEntries().get(0).getProduct().getUnit().getCode(),orderItem.getQuantity().getUnit());
            Assert.assertEquals(subscriptionPricePlanModel.getPricePlanId(),orderItem.getPrice().getAspectData().getSubscriptionItemPrice().getPriceObjectId());
        }
    }
}

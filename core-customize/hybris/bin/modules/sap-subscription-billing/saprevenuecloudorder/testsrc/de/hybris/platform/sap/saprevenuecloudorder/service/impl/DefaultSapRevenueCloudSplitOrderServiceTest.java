/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudSplitOrderServiceTest {

    @Mock
    List<AbstractOrderEntryModel> mockOrderEntryModelList;

    @Mock
    AbstractOrderEntryModel mockAbstractOrderEntryModel;

    @Mock
    CartModel mockCartModel;

    @InjectMocks
    DefaultSapRevenueCloudSplitOrderService mockDefaultSapRevenueCloudSplitOrderService;

    private final BaseStoreModel baseStoreModel = new BaseStoreModel();


    @Before
    public void setUp(){

        baseStoreModel.setExternalTaxEnabled(true);
        mockCartModel.setEntries(mockOrderEntryModelList);
        mockCartModel.setTotalPrice(30.0d);
        mockCartModel.setNet(true);
        mockCartModel.setStore(baseStoreModel);
        mockCartModel.setTotalTax(4d);

        mockOrderEntryModelList.add(mockAbstractOrderEntryModel);
        ProductModel productModel = new ProductModel();
        String subscriptionId = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";
        productModel.setSubscriptionCode(subscriptionId);
        mockAbstractOrderEntryModel.setProduct(productModel);
    }


    //<editor-fold desc=“getAuthorizationAmountListFromCart”>
    @Test
    public void getAuthorizationAmountListFromCart_success(){

        //Execute
        Map<String, BigDecimal> result = mockDefaultSapRevenueCloudSplitOrderService.getAuthorizationAmountListFromCart(mockCartModel);

        //Verify
        Assert.assertNotNull(result);
    }
    //</editor-fold>


}
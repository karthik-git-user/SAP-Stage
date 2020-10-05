/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.service.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTransactionResult;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.Map;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudDigitalPaymentServiceTest {

    @Mock
    BaseStoreService mockBaseStoreService;

    @Mock
    ModelService mockModelService;

    @Mock
    UserService mockUserService;

    @Mock
    CartService mockCartService;

    @Mock
    SapDigitalPaymentService mockSapDigitalPaymentService;

    @Mock
    Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> mockCisSapDigitalPaymentPaymentInfoConverter;

    @Mock
    Converter<AddressModel, AddressData> mockAddressConverter;

    @Mock
    BaseStoreModel mockBaseStoreModel;

    @Mock
    SAPDigitalPaymentConfigurationModel mockSapDigitalPaymentConfigurationModel;


    @InjectMocks
    private DefaultSapRevenueCloudDigitalPaymentService defaultSapRevenueCloudDigitalPaymentService;

    AddressModel addressModel;
    UserModel currentUserModel;
    @Before
    public void setUp() throws Exception {
        currentUserModel = new UserModel();
        String apartmentName = "Some Apartment";
        String buildingName = "Some Building name";
        String cellphone = "9191919199";
        String town = "SomeTown";

        addressModel = new AddressModel();
        addressModel.setAppartment(apartmentName);
        addressModel.setBuilding(buildingName);
        addressModel.setCellphone(cellphone);
        addressModel.setTown(town);

        LinkedList<AddressModel> addressModels = new LinkedList<>();
        addressModels.add(addressModel);
        currentUserModel.setAddresses(addressModels);
        currentUserModel.setDefaultPaymentAddress(addressModel);

        defaultSapRevenueCloudDigitalPaymentService = spy(defaultSapRevenueCloudDigitalPaymentService);
    }

    @Test
    public void saveCCPaymentInfo_success(){
        //Setup
        AddressData addressData = new AddressData();

        doReturn(mockCisSapDigitalPaymentPaymentInfoConverter)
                .when(defaultSapRevenueCloudDigitalPaymentService).getCisSapDigitalPaymentPaymentInfoConverter();
        when(mockCisSapDigitalPaymentPaymentInfoConverter.convert(any(CisSapDigitalPaymentPollRegisteredCardResult.class)))
                .thenReturn(new CCPaymentInfoData());
        doReturn(mockUserService)
                .when(defaultSapRevenueCloudDigitalPaymentService).getUserService();
        when(mockUserService.getCurrentUser())
                .thenReturn(currentUserModel);
        doReturn(mockAddressConverter)
                .when(defaultSapRevenueCloudDigitalPaymentService).getAddressConverter();
        when(mockAddressConverter.convert(addressModel))
                .thenReturn(addressData);
        doReturn(mockBaseStoreService)
                .when(defaultSapRevenueCloudDigitalPaymentService).getBaseStoreService();
        when(mockBaseStoreService.getCurrentBaseStore())
                .thenReturn(mockBaseStoreModel);
        doReturn(mockCartService)
                .when(defaultSapRevenueCloudDigitalPaymentService).getCartService();
        when(mockCartService.getSessionCart())
                .thenReturn(new CartModel());
        doReturn(mockSapDigitalPaymentService)
                .when(defaultSapRevenueCloudDigitalPaymentService).getSapDigitalPaymentService();
        when(mockSapDigitalPaymentService.createPaymentSubscription(any(CCPaymentInfoData.class), any(Map.class)))
                .thenReturn(new CreditCardPaymentInfoModel());
        doNothing()
                .when(mockModelService).save(any(CreditCardPaymentInfoModel.class));

        //Prepare data
        CisSapDigitalPaymentPollRegisteredCardResult result = new CisSapDigitalPaymentPollRegisteredCardResult();
        CisSapDigitalPaymentTransactionResult transactionResult = new CisSapDigitalPaymentTransactionResult();
        result.setCisSapDigitalPaymentTransactionResult(transactionResult);

        //Execute
        CreditCardPaymentInfoModel model = defaultSapRevenueCloudDigitalPaymentService.saveCCPaymentInfo(result);

        //Verify
        Assert.assertNotNull(model);

    }


}

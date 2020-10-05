/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudSubscriptionFacadeTest {

    @Mock
    private CustomerAccountService mockCustomerAccountService;

    @Mock
    private UserService mockUserService;

    @Mock
    private CustomerFacade mockCustomerFacade;

    @Mock
    private SAPRevenueCloudConfigurationModel mockSapRevenueCloudConfigurationModel;

    @Mock
    private SubscriptionService mockSubscriptionService;

    @Mock
    private SAPRevenueCloudConfigurationModel configurationModel;

    @InjectMocks
    private DefaultSapRevenueCloudSubscriptionFacade defaultSapRevenueCloudSubscriptionFacade;

    private SubscriptionData subscriptionData;
    private Subscription subscription;
    private String paymentCardToken;
    private CustomerData customerData;
    private CustomerModel customerModel;

    private String paymentInvoice = "Invoice";
    private String paymentCard = "Payment Card";
    private String customerId = "12345";
    private String subscriptionId = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";

    @Before
    public void setUp() {
        subscriptionData = new SubscriptionData();
        paymentCardToken = "SomeRandomPaymentCardToken";

        configurationModel.setPaymentMethod(paymentInvoice);

        subscriptionData.setId(subscriptionId);
        subscriptionData.setVersion("78");

        customerData = new CustomerData();
        customerData.setUid("some.user@test.com");

        customerModel = new CustomerModel();
        customerModel.setCustomerID(customerId);
        customerModel.setRevenueCloudCustomerId(customerId);

        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setId(customerId);

        subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionId);
        subscription.setCustomer(businessPartner);

        when(mockCustomerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(mockUserService.getCurrentUser()).thenReturn(this.customerModel);
        defaultSapRevenueCloudSubscriptionFacade = Mockito.spy(defaultSapRevenueCloudSubscriptionFacade);
        Mockito.doReturn(configurationModel).when(defaultSapRevenueCloudSubscriptionFacade).getRevenueCloudConfiguration();

        try {
            when(mockSubscriptionService.getSubscription(anyString())).thenReturn(subscription);
        } catch (SubscriptionServiceException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void changePaymentDetailsAsCard_success() throws SubscriptionFacadeException, SubscriptionServiceException {

        when(mockSapRevenueCloudConfigurationModel.getPaymentMethod()).thenReturn(paymentCard);
        when(mockSubscriptionService.updatePayment(eq(subscriptionData.getId()), any(PaymentRequest.class)))
            .thenReturn(new PaymentResponse());

        CreditCardPaymentInfoModel ccPaymentInfoModel = new CreditCardPaymentInfoModel();
        ccPaymentInfoModel.setSubscriptionId(paymentCardToken);
        when(mockCustomerAccountService.getCreditCardPaymentInfoForCode(any(CustomerModel.class), any(String.class))).thenReturn(ccPaymentInfoModel);

        //Execute
        defaultSapRevenueCloudSubscriptionFacade.changePaymentDetailsAsCard(subscriptionData, paymentCardToken);
    }

    @Test
    public void changePaymentDetailsAsInvoice_success() throws SubscriptionFacadeException, SubscriptionServiceException {
        when(mockSapRevenueCloudConfigurationModel.getInvoiceMethod()).thenReturn(paymentInvoice);
        when(mockSubscriptionService.updatePayment(eq(subscriptionData.getId()), any(PaymentRequest.class)))
            .thenReturn(new PaymentResponse());

        defaultSapRevenueCloudSubscriptionFacade.changePaymentDetailsAsInvoice(subscriptionData);
    }

    @Test
    public void reverseCancellation_success() throws SubscriptionFacadeException, SubscriptionServiceException {
        //Setup
        when(mockSubscriptionService
                .reverseCancellation(anyString(), any(CancellationReversalRequest.class))
        ).thenReturn(null);

        //Execute
        defaultSapRevenueCloudSubscriptionFacade.reverseCancellation(subscriptionData);
    }

    @Test
    public void reverseCancellation_failed() throws SubscriptionFacadeException, SubscriptionServiceException {

        //Setup
            when(mockSubscriptionService
                    .reverseCancellation(eq(subscriptionData.getId()), any(CancellationReversalRequest.class))
            )
            .thenReturn(null);

        //Execute
         defaultSapRevenueCloudSubscriptionFacade.reverseCancellation(subscriptionData);
    }

}
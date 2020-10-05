/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriptionServiceTest {

    @Mock
    private SubscriptionBillingApiClient mockSbApiClient;

    @Mock
    private UserService userService;

    @InjectMocks
    DefaultSubscriptionService defaultSubscriptionService;

    //Constants
    private final String CUSTOMER_ID = "6301453447";
    private final String CUSTOMER_EMAIL_ID = "some.user@sap.com";
    private final Integer INITIAL_PAGE_IDX = 0;
    private final Integer PAGE_SIZE = 10;
    private final String SORT_DOC_ASC = "documentNo,Asc";
    private final String SUBSCRIPTION_ID = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";
    private final String PAYMENT_INVOICE = "Invoice";
    private final String REQ_CANCEL_DATE = "2019-01-31";


    @Before
    public void setUp() {

    }

    //<editor-fold desc="getSubscriptionsByClientId(String)" >
    @Test
    public void getSubscriptionsByClientId() throws SubscriptionServiceException {
        //Mock
        Subscription subscription = new Subscription();
        Subscription[] apiResponse = new Subscription[]{subscription};
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Subscription[].class)))
                .thenReturn(apiResponse);

        //Execute
        List<Subscription> subscriptions = defaultSubscriptionService.getSubscriptionsByClientId(CUSTOMER_ID);

        //Verify
        Assert.assertNotNull(subscriptions);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void getSubscriptionsByClientId_NullCustomerId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Subscription[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.getSubscriptionsByClientId(null);
    }
    //</editor-fold>

    //<editor-fold desc="getSubscriptionsByClientIdPage(String, Integer, Integer, Integer, String)">
    @Test
    public void getSubscriptionsByClientIdPage() throws SubscriptionServiceException {

        //Mock
        Subscription[] subscriptions = {};
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("x-count", "100");
        headers.add("x-pagecount", "10");
        when(mockSbApiClient.getRawEntity(any(UriComponents.class), eq(Subscription[].class)))
                .thenReturn(new ResponseEntity<>(subscriptions, headers, HttpStatus.valueOf(200)));

        //Execute
        PaginationResult<List<Subscription>> subscriptionsPage = defaultSubscriptionService.getSubscriptionsByClientIdPage(CUSTOMER_ID,
                INITIAL_PAGE_IDX,
                PAGE_SIZE,
                SORT_DOC_ASC);

        //Verify
        Assert.assertNotNull(subscriptionsPage);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void getSubscriptionsByClientIdPage_nullCustomer() throws SubscriptionServiceException {

        //Mock
        when(mockSbApiClient.getRawEntity(any(UriComponents.class), eq(Subscription[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.getSubscriptionsByClientIdPage(null,
                INITIAL_PAGE_IDX,
                PAGE_SIZE,
                SORT_DOC_ASC);
    }
    //</editor-fold>

    //<editor-fold desc="getSubscription(String)" >
    @Test
    public void getSubscription() throws SubscriptionServiceException {
        //Mock
        Subscription subscription = new Subscription();
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Subscription.class)))
                .thenReturn(subscription);

        //Execute
        Subscription subscriptionResult = defaultSubscriptionService.getSubscription(SUBSCRIPTION_ID);

        //Verify
        Assert.assertNotNull(subscriptionResult);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void getSubscription_NullSubscriptionId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Subscription.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.getSubscription(null);
    }
    //</editor-fold>

    //<editor-fold desc="cancelSubscription(String, CancellationRequest)" >
    @Test
    public void cancelSubscription() throws SubscriptionServiceException {
        //Mock
        ItemModelContext itemModelCtx = ItemContextBuilder.createDefaultContext(UserModel.class);
        UserModel userModel = new UserModel(itemModelCtx);
        userModel.setUid(CUSTOMER_EMAIL_ID);
        when(userService.getCurrentUser()).thenReturn(userModel);

        when(mockSbApiClient.postEntity(any(UriComponents.class), any(CancellationRequest.class), eq(CancellationResponse.class)))
            .thenReturn(new CancellationResponse());

        //Execute
        defaultSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, new CancellationRequest());
    }

    @Test(expected = SubscriptionServiceException.class)
    public void cancelSubscription_NullSubscriptionId() throws SubscriptionServiceException {
        //Mock
        ItemModelContext itemModelCtx = ItemContextBuilder.createDefaultContext(UserModel.class);
        UserModel userModel = new UserModel(itemModelCtx);
        userModel.setUid(CUSTOMER_EMAIL_ID);
        when(userService.getCurrentUser()).thenReturn(userModel);

        when(mockSbApiClient.postEntity(any(UriComponents.class), any(CancellationRequest.class), eq(CancellationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.cancelSubscription(null, new CancellationRequest());
    }
    //</editor-fold>

    //<editor-fold desc="withdrawSubscription(String, WithdrawalRequest)" >
    @Test
    public void withdrawSubscription() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(WithdrawalRequest.class), eq(WithdrawalResponse.class)))
        .thenReturn(new WithdrawalResponse());

        //Execute
        WithdrawalResponse withdrawalResponse = defaultSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, new WithdrawalRequest());

        //Verify
        Assert.assertNotNull(withdrawalResponse);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void withdrawSubscription_NullSubscriptionId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(WithdrawalRequest.class), eq(WithdrawalResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.withdrawSubscription(null, new WithdrawalRequest());
    }
    //</editor-fold>

    //<editor-fold desc="updatePayment(String, PaymentRequest)" >
    @Test
    public void updatePayment() throws SubscriptionServiceException {
        //Input
        Payment payment = new Payment();
        payment.setPaymentMethod(PAYMENT_INVOICE);

        PaymentRequest paymentForm = new PaymentRequest();
        paymentForm.setPayment(payment);

        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(PaymentRequest.class), eq(PaymentResponse.class)))
                .thenReturn(new PaymentResponse());

        //Execute
        PaymentResponse paymentResponse = defaultSubscriptionService.updatePayment(SUBSCRIPTION_ID, paymentForm);

        //Verify
        Assert.assertNotNull(paymentResponse);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void updatePayment_NullSubscriptionId() throws SubscriptionServiceException {

        //Input
        Payment payment = new Payment();
        payment.setPaymentMethod(PAYMENT_INVOICE);

        PaymentRequest paymentForm = new PaymentRequest();
        paymentForm.setPayment(payment);

        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(PaymentRequest.class), eq(PaymentResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.updatePayment(SUBSCRIPTION_ID, paymentForm);
    }
    //</editor-fold>

    //<editor-fold desc="getBillingFrequency(ProductModel)" >
    @Test
    public void getBillingFrequency() {
        //Input
        BillingFrequencyModel billingFrequencyModel = new BillingFrequencyModel();

        BillingPlanModel billingPlanModel = new BillingPlanModel();
        billingPlanModel.setBillingFrequency(billingFrequencyModel);

        SubscriptionTermModel termModel = new SubscriptionTermModel();
        termModel.setBillingPlan(billingPlanModel);

        ItemModelContext itemModelCtx = ItemContextBuilder.createDefaultContext(UserModel.class);
        ProductModel productModel = new ProductModel(itemModelCtx);
        productModel.setSubscriptionTerm(termModel);

        //Execute
        BillingFrequencyModel billingFrequencyResult = defaultSubscriptionService.getBillingFrequency(productModel);

        //Verify
        Assert.assertEquals(billingFrequencyModel, billingFrequencyResult);
    }
    //</editor-fold>

    //<editor-fold desc="computeCancellationDate(String, String)" >
    @Test
    public void computeCancellationDate() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(EffectiveExpirationDate.class)))
                .thenReturn(new EffectiveExpirationDate());

        //Execute
        EffectiveExpirationDate effectiveExpirationDate = defaultSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCEL_DATE);

        //Verify
        Assert.assertNotNull(effectiveExpirationDate);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void computeCancellationDate_NullSubscriptionId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(EffectiveExpirationDate.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.computeCancellationDate(null, REQ_CANCEL_DATE);
    }
    //</editor-fold>

    //<editor-fold desc="reverseCancellation(String, String)" >
    @Test
    public void reverseCancellation() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(CancellationReversalRequest.class), eq(CancellationReversalResponse.class)))
                .thenReturn(new CancellationReversalResponse());

        //Execute
        CancellationReversalResponse response = defaultSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, new CancellationReversalRequest());

        //Verify
        Assert.assertNotNull(response);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void reverseCancellation_NullSubscriptionId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.postEntity(any(UriComponents.class), any(CancellationReversalRequest.class), eq(CancellationReversalResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultSubscriptionService.reverseCancellation(null, new CancellationReversalRequest());
    }
    //</editor-fold>

}

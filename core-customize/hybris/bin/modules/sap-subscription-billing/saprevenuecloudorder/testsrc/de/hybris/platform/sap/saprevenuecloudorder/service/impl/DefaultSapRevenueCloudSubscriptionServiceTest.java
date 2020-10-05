/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import com.hybris.charon.exp.BadRequestException;
import com.hybris.charon.exp.ClientException;
import com.hybris.charon.exp.ServerException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SapRevenueCloudSubscriptionClient;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudBusinessException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudClientException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudServerException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudUnknownException;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionConfigurationService;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
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

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudSubscriptionServiceTest {

    @Mock
    SapRevenueCloudSubscriptionConfigurationService mockSapRevenueCloudSubscriptionConfigurationService;

    @Mock
    SapRevenueCloudSubscriptionClient mockSapRevenueCloudSubscriptionClient;

    @Mock
    Response mockResponse;

    @InjectMocks
    DefaultSapRevenueCloudSubscriptionService defaultSapRevenueCloudSubscriptionService;


    private static final String CLIENT_ID = "9105002825";
    private static final String SUBSCRIPTION_ID = "794347FC-F86B-4E7D-AF51-ACDCA81E5B9C";
    private static final String BILL_ID = "DF259852-967C-4475-A5F9-6B4AF87A27E7";
    private static  final String CURRENT_DATE = "2019-09-05";
    private static final String REQ_CANCELLATION_DATE = "2029-01-01";
    private static final String CUSTOMER_ID = "9105002825";
    private static final String FROM_DATE = "2018-12-01";
    private static final String TO_DATE = "2019-04-01";
    private final CancelSubscription cancelForm = new CancelSubscription();
    private final WithdrawSubscription withdrawForm = new WithdrawSubscription();
    private final ExtendSubscription extendForm = new ExtendSubscription();
    private final ChangePaymentData changePaymentData = new ChangePaymentData();
    private final ExtendSubscription extendSubscription = new ExtendSubscription();
    private final SubscriptionExtensionForm subscriptionExtensionForm = new SubscriptionExtensionForm();
    private final Payment payment = new Payment();
    private final Subscription subscription = new Subscription();
    private final MetaData metaData = new MetaData();
    private final Date d = new Date();
    private final CancellationReversal cancellationReversal = new CancellationReversal();
    private final SubscriptionExtensionResponse subscriptionExtensionResponse = new SubscriptionExtensionResponse();


    @Before
    public void setUp() {
        when(mockSapRevenueCloudSubscriptionConfigurationService.getSapSubscriptionClient())
                .thenReturn(mockSapRevenueCloudSubscriptionClient);

        metaData.setVersion("78");
        subscriptionExtensionForm.setChangedBy("abc@xyz.com");
        subscriptionExtensionForm.setChangedAt(new Date());
        subscriptionExtensionForm.setExtensionDate(d);
        subscriptionExtensionForm.setMetaData(metaData);
        subscriptionExtensionForm.setNumberOfBillingPeriods(20);
        subscriptionExtensionForm.setUnlimited(false);
        subscriptionExtensionResponse.setValidUntil("2029-01-01T05:00:00.000Z");

        cancellationReversal.setMetaData(metaData);
    }

    //<editor-fold desc="getSubscriptionsByClientId">
    @Test
    public void getSubscriptionsByClientId_nullClientId() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(null)).thenReturn(Collections.emptyList());

        //Execute
        List<Subscription> subscriptions = defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId(null);

        //Verify
        Assert.assertEquals(0, subscriptions.size());
    }

    @Test
    public void getSubscriptionsByClientId_success() {
        //Setup
        List<Subscription> subscriptions = Collections.emptyList();
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(any(String.class))).thenReturn(subscriptions);

        //Execute
        List<Subscription> returnList = defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId("9105002825");

        //Verify
        Assert.assertNotNull(returnList);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getSubscriptionsByClientId_badRequestException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(any(String.class))).thenThrow(new BadRequestException(400, "Some bad parameter"));

        //Execute
        defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId(CLIENT_ID);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getSubscriptionsByClientId_clientException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(any(String.class))).thenThrow(new ClientException(401, "Client Exception"));

        //Execute
        defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId(CLIENT_ID);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getSubscriptionsByClientId_serverException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(any(String.class))).thenThrow(new ServerException(500, "ServerException"));

        //Execute
        defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId(CLIENT_ID);
    }

    @Test(expected = Exception.class)
    public void getSubscriptionsByClientId_unknownException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionsByClientId(any(String.class))).thenThrow(new Exception("Unknown Exception"));

        //Execute
        defaultSapRevenueCloudSubscriptionService.getSubscriptionsByClientId(CLIENT_ID);
    }
    //</editor-fold>


    //<editor-fold desc="getSubscriptionsWithPagination">
    //</editor-fold>


    //<editor-fold desc="getSubscriptionById">
    @Test
    public void getSubscriptionById_nullSubscriptionId() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(null)).thenReturn(null);

        //Execute
        Subscription returnSubscriptionId = defaultSapRevenueCloudSubscriptionService.getSubscriptionById(null);

        //Verify
        Assert.assertNull(returnSubscriptionId);
    }

    @Test
    public void getSubscriptionById_success() {
        //Setup
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(any(String.class))).thenReturn(subscription);

        //Execute
        Subscription resultSubscription = defaultSapRevenueCloudSubscriptionService.getSubscriptionById(SUBSCRIPTION_ID);

        //Verify
        Assert.assertNotNull(resultSubscription);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getSubscriptionsById_badRequestException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(any(String.class))).thenThrow(new BadRequestException(400, "Some bad parameter"));

        //Process
        defaultSapRevenueCloudSubscriptionService.getSubscriptionById(SUBSCRIPTION_ID);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getSubscriptionsById_clientException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(any(String.class))).thenThrow(new ClientException(100, "Client Exception"));

        //Process
        defaultSapRevenueCloudSubscriptionService.getSubscriptionById(SUBSCRIPTION_ID);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getSubscriptionsById_serverException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(any(String.class))).thenThrow(new ServerException(101, "ServerException"));

        //Process
        defaultSapRevenueCloudSubscriptionService.getSubscriptionById(SUBSCRIPTION_ID);
    }

    @Test(expected = Exception.class)
    public void getSubscriptionsById_unknownException() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionById(any(String.class))).thenThrow(new Exception("Unknown Exception"));

        //Process
        defaultSapRevenueCloudSubscriptionService.getSubscriptionById(SUBSCRIPTION_ID);
    }
    //</editor-fold>


    //<editor-fold desc="cancelSubscription">
    @Test
    public void cancelSubscription_success() {
        //Setup
        doNothing().when(mockSapRevenueCloudSubscriptionClient).cancelSubscription(any(String.class), any(CancelSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, cancelForm);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void cancelSubscription_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).cancelSubscription(any(String.class), any(CancelSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, cancelForm);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void cancelSubscription_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).cancelSubscription(any(String.class), any(CancelSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, cancelForm);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void cancelSubscription_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).cancelSubscription(any(String.class), any(CancelSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, cancelForm);
    }

    @Test(expected = RevenueCloudUnknownException.class)
    public void cancelSubscription_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).cancelSubscription(any(String.class), any(CancelSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.cancelSubscription(SUBSCRIPTION_ID, cancelForm);
    }
    //</editor-fold>


    //<editor-fold desc="withdrawSubscription">
    @Test
    public void withdrawSubscription_success() {
        //Setup
        doNothing().when(mockSapRevenueCloudSubscriptionClient).withdrawSubscription(any(String.class), any(WithdrawSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, withdrawForm);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void withdrawSubscription_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).withdrawSubscription(any(String.class), any(WithdrawSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, withdrawForm);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void withdrawSubscription_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).withdrawSubscription(any(String.class), any(WithdrawSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, withdrawForm);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void withdrawSubscription_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).withdrawSubscription(any(String.class), any(WithdrawSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, withdrawForm);
    }

    @Test(expected = RevenueCloudUnknownException.class)
    public void withdrawSubscription_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).withdrawSubscription(any(String.class), any(WithdrawSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.withdrawSubscription(SUBSCRIPTION_ID, withdrawForm);
    }
    //</editor-fold>


    //<editor-fold desc="extendSubscription">
    @Test
    @SuppressWarnings({"deprecation", "removal"})
    public void extendSubscription_success() {
        //Setup
        doNothing().when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendForm);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    @SuppressWarnings({"deprecation", "removal"})
    public void extendSubscription_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendForm);
    }

    @Test(expected = RevenueCloudClientException.class)
    @SuppressWarnings({"deprecation", "removal"})
    public void extendSubscription_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendForm);
    }

    @Test(expected = RevenueCloudServerException.class)
    @SuppressWarnings({"deprecation", "removal"})
    public void extendSubscription_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendForm);
    }

    @Test(expected = RevenueCloudUnknownException.class)
    @SuppressWarnings({"deprecation", "removal"})
    public void extendSubscription_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class));

        //Process
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendForm);
    }
    //</editor-fold>


    //<editor-fold desc="changePaymentDetails">
    @Test
    public void changePaymentDetails_success() {
        //Setup
        payment.setPaymentMethod("Payment Card");
        changePaymentData.setPayment(payment);
        doNothing().when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void changePaymentDetails_nullPayment() {
        //Setup
        changePaymentData.setPayment(payment);
        doNothing().when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void changePaymentDetails_badRequestException() {
        //SetUp
        payment.setPaymentMethod("Card");
        changePaymentData.setPayment(payment);
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void changePaymentDetails_clientException() {
        //SetUp
        payment.setPaymentMethod("Card");
        changePaymentData.setPayment(payment);
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void changePaymentDetails_serverException() {
        //SetUp
        payment.setPaymentMethod("Card");
        changePaymentData.setPayment(payment);
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }

    @Test(expected = Exception.class)
    public void changePaymentDetails_unknownException() {
        //Setup
        payment.setPaymentMethod("Card");
        changePaymentData.setPayment(payment);
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).changePaymentDetails(any(String.class), any(ChangePaymentData.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.changePaymentDetails(SUBSCRIPTION_ID, changePaymentData);
    }
    //</editor-fold>


    //<editor-fold desc="getBillingFrequency">
    @Test
    public void getBillingFrequency_success() {
        //Setup
        ProductModel productModel = new ProductModel();
        SubscriptionTermModel subscriptionTermModel = new SubscriptionTermModel();
        BillingPlanModel billingPlanModel = new BillingPlanModel();
        BillingFrequencyModel billingFrequencyModel = new BillingFrequencyModel();
        billingFrequencyModel.setCartAware(true);
        billingPlanModel.setBillingFrequency(billingFrequencyModel);
        subscriptionTermModel.setBillingPlan(billingPlanModel);
        productModel.setSubscriptionTerm(subscriptionTermModel);

        //Execute
        BillingFrequencyModel result = defaultSapRevenueCloudSubscriptionService.getBillingFrequency(productModel);

        //Verify
        Assert.assertNotNull(result);
    }

    public void getBillingFrequency_nullPaymentModel() {
        //Setup
        ProductModel productModel = new ProductModel();
        SubscriptionTermModel subscriptionTermModel = new SubscriptionTermModel();
        BillingPlanModel billingPlanModel = new BillingPlanModel();
        BillingFrequencyModel billingFrequencyModel = new BillingFrequencyModel();
        billingPlanModel.setBillingFrequency(billingFrequencyModel);
        subscriptionTermModel.setBillingPlan(billingPlanModel);
        productModel.setSubscriptionTerm(subscriptionTermModel);

        //Execute
        BillingFrequencyModel result = defaultSapRevenueCloudSubscriptionService.getBillingFrequency(productModel);

        //Verify
        Assert.assertNull(result);
    }

    //</editor-fold>


    //<editor-fold desc="computeCancellationDate">
    @Test
    public void computeCancellationDate_success() {
        //Setup
        subscription.setEffectiveExpirationDate(REQ_CANCELLATION_DATE);
        when(mockSapRevenueCloudSubscriptionClient.getCancellationDate(any(String.class), any(String.class))).thenReturn(subscription);

        //Execute
        String subscriptionById = defaultSapRevenueCloudSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCELLATION_DATE);

        //Verify
        Assert.assertNotNull(subscriptionById);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void computeCancellationDate_badRequestException() {
        //SetUp
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).getCancellationDate(any(String.class), any(String.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCELLATION_DATE);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void computeCancellationDate_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).getCancellationDate(any(String.class), any(String.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCELLATION_DATE);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void computeCancellationDate_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).getCancellationDate(any(String.class), any(String.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCELLATION_DATE);
    }

    @Test(expected = Exception.class)
    public void computeCancellationDate_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).getCancellationDate(any(String.class), any(String.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.computeCancellationDate(SUBSCRIPTION_ID, REQ_CANCELLATION_DATE);
    }

    //</editor-fold>


    //<editor-fold desc=" extendSubscription_ReturnSubscription">

    @Test
    public void extendSubscription_returnSubscription_success() {
        //Setup
        String extensionDate = "2028-01-01T05:00:00.000Z";
        String unlimited = "false";
        extendSubscription.setMetaData(metaData);
        extendSubscription.setExtensionDate(extensionDate);
        extendSubscription.setUnlimited(unlimited);
        when(mockSapRevenueCloudSubscriptionClient.extendSubscription(any(String.class), any(ExtendSubscription.class), any(Boolean.class))).thenReturn(new Subscription());

        //Execute
        Subscription result = defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendSubscription, true);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void extendSubscription_returnSubscription_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendSubscription, true);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void extendSubscription_returnSubscription_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendSubscription, true);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void extendSubscription_returnSubscription_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendSubscription, true);
    }

    @Test(expected = Exception.class)
    public void extendSubscription_returnSubscription_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(ExtendSubscription.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, extendSubscription, true);
    }

    //</editor-fold>


    //<editor-fold desc="extendSubscription_ReturnSubscriptionExtensionResponse">
    @Test
    public void extendSubscription_returnSubscriptionExtensionResponse_success() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.extendSubscription(any(String.class), any(SubscriptionExtensionForm.class), any(Boolean.class))).thenReturn(subscriptionExtensionResponse);

        //Execute
        SubscriptionExtensionResponse result = defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, subscriptionExtensionForm, true);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void extendSubscription_returnSubscriptionExtensionResponse_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(SubscriptionExtensionForm.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, subscriptionExtensionForm, true);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void extendSubscription_returnSubscriptionExtensionResponse_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(SubscriptionExtensionForm.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, subscriptionExtensionForm, true);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void extendSubscription_returnSubscriptionExtensionResponse_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(SubscriptionExtensionForm.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, subscriptionExtensionForm, true);
    }

    @Test(expected = Exception.class)
    public void extendSubscription_returnSubscriptionExtensionResponse_unknownException() {
        //Setup
        SubscriptionExtensionForm subscriptionExtensionForm1 = new SubscriptionExtensionForm();
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).extendSubscription(any(String.class), any(SubscriptionExtensionForm.class), any(Boolean.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.extendSubscription(SUBSCRIPTION_ID, subscriptionExtensionForm1, true);
    }

    //</editor-fold>


    //<editor-fold desc="getBillsBySubscriptionsId">
    @Test
    @SuppressWarnings({"deprecation", "removal"})
    public void getBillsBySubscriptionsId_success() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionBills(any(String.class), any(String.class), any(String.class))).thenReturn(Collections.emptyList());

        //Execute
        List<Bills> result = defaultSapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(CUSTOMER_ID, FROM_DATE, TO_DATE);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getBillsBySubscriptionsId_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBills(any(String.class), any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(CUSTOMER_ID, FROM_DATE, TO_DATE);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getBillsBySubscriptionsId_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBills(any(String.class), any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(CUSTOMER_ID, FROM_DATE, TO_DATE);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getBillsBySubscriptionsId_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBills(any(String.class), any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(CUSTOMER_ID, FROM_DATE, TO_DATE);
    }

    @Test(expected = Exception.class)
    public void getBillsBySubscriptionsId_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBills(any(String.class), any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(CUSTOMER_ID, FROM_DATE, TO_DATE);
    }
    //</editor-fold>


    //<editor-fold desc="getSubscriptionCurrentUsage">
    @Test
    @SuppressWarnings({"deprecation", "removal"})
    public void getSubscriptionCurrentUsage_success() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionCurrentUsage(any(String.class), any(String.class))).thenReturn(Collections.emptyList());

        //Execute
        List<Bills> result = defaultSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(SUBSCRIPTION_ID, CURRENT_DATE);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getSubscriptionCurrentUsage_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionCurrentUsage(any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(SUBSCRIPTION_ID, CURRENT_DATE);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getSubscriptionCurrentUsage_clientException() {
        //SetUp
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionCurrentUsage(any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(SUBSCRIPTION_ID, CURRENT_DATE);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getSubscriptionCurrentUsage_serverException() {
        //SetUp
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionCurrentUsage(any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(SUBSCRIPTION_ID, CURRENT_DATE);
    }

    @Test(expected = Exception.class)
    public void getSubscriptionCurrentUsage_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionCurrentUsage(any(String.class), any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionCurrentUsage(SUBSCRIPTION_ID, CURRENT_DATE);
    }
    //</editor-fold>


    //<editor-fold desc="getSubscriptionBillsById">

    @Test
    @SuppressWarnings({"deprecation", "removal"})
    public void getSubscriptionBillsById_success() {
        //Setup
        Bills bills = new Bills();
        bills.setId(BILL_ID);
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionBillById(any(String.class))).thenReturn(bills);

        //Execute
        Bills result = defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsById(BILL_ID);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getSubscriptionBillsById_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillById(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsById(BILL_ID);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getSubscriptionBillsById_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillById(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsById(BILL_ID);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getSubscriptionBillsById_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillById(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsById(BILL_ID);
    }

    @Test(expected = Exception.class)
    public void getSubscriptionBillsById_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillById(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsById(BILL_ID);
    }
    //</editor-fold>


    //<editor-fold desc="getBillsBySubscriptionsId">
    //</editor-fold>

    //<editor-fold desc="getSubscriptionBillsByBillId">
    @Test
    public void getSubscriptionBillsByBillId_success() {
        //Setup
        BillsList billsList = new BillsList();
        billsList.setId(BILL_ID);
        when(mockSapRevenueCloudSubscriptionClient.getSubscriptionBillByBillId(any(String.class))).thenReturn(billsList);

        //Execute
        BillsList result = defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsByBillId(BILL_ID);

        //Verify
        Assert.assertNotNull(result);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void getSubscriptionBillsByBillId_badRequestException() {
        //SetUp
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillByBillId(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsByBillId(BILL_ID);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void getSubscriptionBillsByBillId_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillByBillId(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsByBillId(BILL_ID);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void getSubscriptionBillsByBillId_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillByBillId(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsByBillId(BILL_ID);
    }

    @Test(expected = Exception.class)
    public void getSubscriptionBillsByBillId_unknownException() {
        //SetUp
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).getSubscriptionBillByBillId(any(String.class));

        //Verify
        defaultSapRevenueCloudSubscriptionService.getSubscriptionBillsByBillId(BILL_ID);
    }
    //</editor-fold>


    //<editor-fold desc="reverseCancellation">
    public void reverseCancellation_success() {
        //Setup
        when(mockSapRevenueCloudSubscriptionClient.cancellationReversal(eq(SUBSCRIPTION_ID), any(CancellationReversal.class)))
                .thenReturn(new CancellationReversalResponse());

        //Execute
        defaultSapRevenueCloudSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, cancellationReversal);
    }

    @Test(expected = RevenueCloudBusinessException.class)
    public void reverseCancellation_badRequestException() {
        //Setup
        doThrow(new BadRequestException(400, "Wrong Data")).when(mockSapRevenueCloudSubscriptionClient).cancellationReversal(eq(SUBSCRIPTION_ID), any(CancellationReversal.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, cancellationReversal);
    }

    @Test(expected = RevenueCloudClientException.class)
    public void reverseCancellation_clientException() {
        //Setup
        doThrow(new ClientException(401, "Unauthorised")).when(mockSapRevenueCloudSubscriptionClient).cancellationReversal(eq(SUBSCRIPTION_ID), any(CancellationReversal.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, cancellationReversal);
    }

    @Test(expected = RevenueCloudServerException.class)
    public void reverseCancellation_serverException() {
        //Setup
        doThrow(new ServerException(500, "Internal Server Error")).when(mockSapRevenueCloudSubscriptionClient).cancellationReversal(eq(SUBSCRIPTION_ID), any(CancellationReversal.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, cancellationReversal);
    }

    @Test(expected = Exception.class)
    public void reverseCancellation_unknownException() {
        //Setup
        doThrow(new RuntimeException("Some Unknown Exception")).when(mockSapRevenueCloudSubscriptionClient).cancellationReversal(eq(SUBSCRIPTION_ID), any(CancellationReversal.class));

        //Execute
        defaultSapRevenueCloudSubscriptionService.reverseCancellation(SUBSCRIPTION_ID, cancellationReversal);
    }

    //</editor-fold>

}
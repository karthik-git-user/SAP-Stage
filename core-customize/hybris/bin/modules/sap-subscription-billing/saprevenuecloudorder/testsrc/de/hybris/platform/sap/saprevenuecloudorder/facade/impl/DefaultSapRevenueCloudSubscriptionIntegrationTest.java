/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.model.*;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSSiteService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Customer;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.*;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Market;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Metadata;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Payment;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Product;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.RatePlan;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.populators.subscription.DefaultSubscriptionPopulator;
import de.hybris.platform.sap.saprevenuecloudorder.service.impl.DefaultBillService;
import de.hybris.platform.sap.saprevenuecloudorder.service.impl.DefaultSubscriptionService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil.stringToDate;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


/**
 * Integration test for DefaultSapRevenueCloudSubscription
 */
@IntegrationTest
@ContextConfiguration(locations =
        {"classpath:saprevenuecloudorder/saprevenuecloudorder-testclasses.xml"})
public class DefaultSapRevenueCloudSubscriptionIntegrationTest extends BaseCommerceBaseTest {

    @InjectMocks
    @Resource(name = "subscriptionFacade")
    private SapRevenueCloudSubscriptionFacade sapRevenueCloudSubscriptionFacade;

    @Mock
    private DefaultSapRevenueCloudSubscriptionFacade mockDefaultSapRevenueCloudSubscriptionFacade;

    @InjectMocks
    @Resource(name = "sbSubscriptionService")
    private DefaultSubscriptionService mockSubscriptionService;

    @InjectMocks
    @Resource(name = "sbSubscriptionBillingApiClient")
    private SubscriptionBillingApiClient mockSubscriptionBillingApiClient;

    @InjectMocks
    @Resource(name = "sbBillService")
    private DefaultBillService mockBillService;

    @InjectMocks
    @Resource(name = "sbSubscriptionPopulator")
    private DefaultSubscriptionPopulator mockSubscriptionPopulator;

    @Resource
    private SubscriptionFacade subscriptionFacade;

    @Resource
    private UserService userService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Mock
    private RestOperations mockRestOperations;

    @Mock
    private GenericDao mockGenericDao;

    @Mock
    private DefaultCMSSiteService mockCmsSiteService;

    @Mock
    private CustomerAccountService mockCustomerAccountService;

    @Mock
    private CustomerAccountDao mockCustomerAccountDao;

    @Mock
    private SAPRevenueCloudConfigurationModel mockSAPRevenueCloudConfigurationModel;

    @Mock
    private UserService mockUserService;

    @Mock
    private CustomerFacade mockCustomerFacade;

    @Mock
    private DestinationService mockDestinationService;

    @Mock
    private CustomerModel mockUser;

    @Mock
    private CustomerData customerData;


    private static final String COMPUTED_CANCELLATION_DATE = "2029-01-01";
    private static final String CUSTOMER_ID = "9105002825";
    private static final String CUSTOMER_EMAIL = "some.user@sap.com";
    private static final String PAYMENT_INVOICE = "paymentInvoice";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String PAYMENT_CARD_ID = "paymentCardId";
    private static final String PAYMENT_CARD_TOKEN = "paymentCardToken";
    private static final String SUBSCRIPTION_ID = "DAD12217-4CCB-4B80-8A9A-DCFC32CE8B61";

    //@Before
    public void setUp() {
        //createCoreData();

        //importCsv("/saprevenuecloudorder/TestData.impex", "utf-8");

        //MockitoAnnotations.initMocks(this);
    }

    //@Test
    public void shouldFetchSubscriptions() throws SubscriptionFacadeException {

        //Response
        Subscription subscription = getSampleSubscription();
        List<Subscription> subscriptionList = List.of(subscription);
        ResponseEntity<Subscription[]> response = getResponseEntity(new Subscription[]{subscription});

        //Mock
        mockCurrentUser();
        mockDestinationService();
        CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testProductCatalog", "Online");
        when(mockCmsSiteService.getCurrentCatalogVersion()).thenReturn(catalogVersionModel);
        when(mockRestOperations.getForEntity(any(URI.class), eq(Subscription[].class)))
                .thenReturn(response);

        //Execute
        Collection<SubscriptionData> subscriptions = sapRevenueCloudSubscriptionFacade.getSubscriptions();

        //Verify
        Assert.assertEquals(subscriptionList.size(), subscriptions.size());
        Assert.assertNotNull(subscriptions);
    }

    //@Test
    public void shouldCancelSubscription() throws SubscriptionFacadeException, SubscriptionServiceException {

        //Input
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setVersion("23");
        subscriptionData.setValidTillDate("2019-10-19");
        subscriptionData.setId(SUBSCRIPTION_ID);

        //Mocks
        mockDestinationService();

        Metadata metadata = new Metadata();
        metadata.setVersion(123);
        CancellationResponse response = new CancellationResponse();
        response.setCancellationReason("Some Reason");
        response.setChangedAt(new Date());
        response.setChangedBy("some.user@sap.com");
        response.setMetadata(metadata);
        response.setRequestedCancellationDate("2019-10-31");
        when(mockSubscriptionService.cancelSubscription(any(String.class), any(CancellationRequest.class)))
                .thenReturn(response);

        //Execute
        sapRevenueCloudSubscriptionFacade.cancelSubscription(subscriptionData);

    }

    //@Test
    public void shouldWithdrawSubscription() throws SubscriptionFacadeException {

        //Input
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setVersion("10");
        subscriptionData.setId(SUBSCRIPTION_ID);

        //Mocks
        mockDestinationService();

        Metadata metadata = new Metadata();
        metadata.setVersion(10);
        WithdrawalResponse withdrawalResponse = new WithdrawalResponse();
        withdrawalResponse.setChangedAt(new Date());
        withdrawalResponse.setChangedBy("some.user@sap.com");
        withdrawalResponse.setMetadata(metadata);
        ResponseEntity<WithdrawalResponse> response = getResponseEntity(withdrawalResponse);

        when(mockRestOperations.postForEntity(any(URI.class), any(WithdrawalRequest.class), eq(WithdrawalResponse.class)))
                .thenReturn(response);

        //Execute
        sapRevenueCloudSubscriptionFacade.withdrawSubscription(subscriptionData);

    }

    //@Test
    public void shouldComputeCancellationDate() throws SubscriptionFacadeException, SubscriptionServiceException {

        //Response
        EffectiveExpirationDate response = new EffectiveExpirationDate();
        response.setEffectiveExpirationDate(new Date());

        //Mocks
        mockDestinationService();
        when(mockSubscriptionService.computeCancellationDate(eq(SUBSCRIPTION_ID), any(String.class))).thenReturn(response);

        //Execute
        SubscriptionData result = sapRevenueCloudSubscriptionFacade.computeCancellationDate(SUBSCRIPTION_ID);

        //Verify
        Assert.assertEquals(COMPUTED_CANCELLATION_DATE, result.getValidTillDate());

    }

    //@Test
    public void shouldReverseCancellation() throws SubscriptionFacadeException, SubscriptionServiceException {
        //Input
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setVersion("23");
        subscriptionData.setId(SUBSCRIPTION_ID);

        //Mocks
        mockDestinationService();

        CancellationReversalResponse response = new CancellationReversalResponse();
        response.setValidUntil(new Date());
        response.setValidUntilIsUnlimited(false);
        when(mockSubscriptionService.reverseCancellation(eq(SUBSCRIPTION_ID), any(CancellationReversalRequest.class))).thenReturn(response);

        //Execute
        sapRevenueCloudSubscriptionFacade.reverseCancellation(subscriptionData);

    }

    //@Test
    public void shouldChangePaymentDetailsAsCard() throws SubscriptionFacadeException, SubscriptionServiceException {

        //Input
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setVersion("23");
        subscriptionData.setId(SUBSCRIPTION_ID);

        //SetUp
        customerData.setUid(CUSTOMER_EMAIL);
        CreditCardPaymentInfoModel ccPaymentInfoModel = new CreditCardPaymentInfoModel();
        ccPaymentInfoModel.setSubscriptionId(SUBSCRIPTION_ID);
        SAPRevenueCloudConfigurationModel revenueCloudModel = new SAPRevenueCloudConfigurationModel();
        revenueCloudModel.setInvoiceMethod("Invoice");
        revenueCloudModel.setPaymentMethod("Payment Card");
        revenueCloudModel.setName("name");

        List<SAPRevenueCloudConfigurationModel> revenueCloudConfigs = new LinkedList<>();
        revenueCloudConfigs.add(revenueCloudModel);

        //Mocks
        when(mockGenericDao.find()).thenReturn(revenueCloudConfigs);
        when(mockSAPRevenueCloudConfigurationModel.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(mockCustomerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(mockUserService.getCurrentUser()).thenReturn(mockUser);
        when(mockCustomerAccountService.getCreditCardPaymentInfoForCode(any(CustomerModel.class), any(String.class))).thenReturn(ccPaymentInfoModel);
        when(mockCustomerAccountDao.findCreditCardPaymentInfoByCustomer(any(CustomerModel.class), eq(PAYMENT_CARD_ID))).thenReturn(ccPaymentInfoModel);
        mockDestinationService();

        Metadata metadata = new Metadata();
        metadata.setVersion(123);

        Payment payment = new Payment();
        payment.setPaymentCardToken("External Card");
        payment.setPaymentCardToken("Some_card_token");

        PaymentResponse response = new PaymentResponse();
        response.setChangedAt(new Date());
        response.setChangedBy("some.user@sap.com");
        response.setMetadata(metadata);
        response.setPayment(payment);
        when(mockSubscriptionService.updatePayment(any(String.class), any(PaymentRequest.class)))
                .thenReturn(response);

        //Verify
        Assert.assertNotNull("Payment Card Id should not be null.", PAYMENT_CARD_ID);
        Assert.assertNotNull("Payment Card Id is invalid or doesn't belong to customer", PAYMENT_CARD_TOKEN);

        //Execute
        sapRevenueCloudSubscriptionFacade.changePaymentDetailsAsCard(subscriptionData, PAYMENT_CARD_ID);
    }


    //@Test
    public void shouldChangePaymentDetailsAsInvoice() throws SubscriptionFacadeException, SubscriptionServiceException {

        //Input
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setVersion("23");
        subscriptionData.setId(SUBSCRIPTION_ID);

        //SetUp
        customerData.setUid(CUSTOMER_EMAIL);
        SAPRevenueCloudConfigurationModel revenueCloudModel = new SAPRevenueCloudConfigurationModel();
        revenueCloudModel.setInvoiceMethod("Invoice");
        revenueCloudModel.setPaymentMethod("Payment Card");
        revenueCloudModel.setName("name");

        List<SAPRevenueCloudConfigurationModel> revenueCloudConfigs = new LinkedList<>();
        revenueCloudConfigs.add(revenueCloudModel);

        //Mocks
        when(mockGenericDao.find()).thenReturn(revenueCloudConfigs);
        when(mockSAPRevenueCloudConfigurationModel.getInvoiceMethod()).thenReturn(PAYMENT_INVOICE);
        when(mockCustomerFacade.getCurrentCustomer()).thenReturn(customerData);
        mockDestinationService();

        Metadata metadata = new Metadata();
        metadata.setVersion(123);

        Payment payment = new Payment();
        payment.setPaymentCardToken("Invoice");

        PaymentResponse response = new PaymentResponse();
        response.setChangedAt(new Date());
        response.setChangedBy("some.user@sap.com");
        response.setMetadata(metadata);
        response.setPayment(payment);
        when(mockSubscriptionService.updatePayment(any(String.class), any(PaymentRequest.class)))
                .thenReturn(response);

        //Execute
        sapRevenueCloudSubscriptionFacade.changePaymentDetailsAsInvoice(subscriptionData);

    }

    //@Test
    public void shouldFetchBillDetails() throws SubscriptionFacadeException, SubscriptionServiceException {
        //Mocks
        mockDestinationService();
        Bill bill = getSampleBill();
        when(mockBillService.getBill(any(String.class))).thenReturn(bill);

        //Execute
        List<SubscriptionBillingData> subscriptionBillDetails = sapRevenueCloudSubscriptionFacade.getSubscriptionBills("2019-10-31","121");

        Assert.assertNotNull(subscriptionBillDetails);
    }

    @Test
    public void shouldFetchSubscriptions_details(){

    }

    public Bill getSampleBill() {

        BillMetadata metadata = new BillMetadata();
        metadata.setCreatedAt(stringToDate("2019-11-16T06:01:33.006Z"));

        Closing closing = new Closing();
        closing.setMethod("AUTO");
        closing.setMetaData(metadata);

        BillMarket market = new BillMarket();
        market.setId("US");
        market.setTimeZone("America/New_York");
        market.setCurrency("USD");
        market.setPriceType("Net");

        Customer customer = new Customer();
        customer.setId("6301453447");
        customer.setType("INDIVIDUAL");

        BillPayment payment = new BillPayment();
        payment.setMethod("External Card");
        payment.setToken("0a099acd-2a32-48c1-8da7-d980ad6e210e");
        payment.setPaymentStatus("NOT_SETTLED");

        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency("USD");
        monetaryAmount.setAmount(24f);

        BillItem billItem = getSampleBillItem();

        Bill bill = new Bill();
        bill.setId("2E7F17D2-C14C-4B6D-AFFE-A395B0035DEC");
        bill.setDocumentNumber(116);
        bill.setMetaData(metadata);
        bill.setBillingType("CHARGE");
        bill.setBillStatus("OPEN");
        bill.setClosing(closing);
        bill.setTransferStatus("NOT_TRANSFERRED");
        bill.setBillingDate(stringToDate("2019-12-14", SUBSCRIPTION_BILL_DATE_FORMAT));
        bill.setCreateInvoice(true);
        bill.setMarket(market);
        bill.setCustomer(customer);
        bill.setPayment(payment);
        bill.setNetAmount(monetaryAmount);
        bill.setBillItems(List.of(billItem));

        return bill;
    }

    private BillItem getSampleBillItem() {
        BillSubscription subscription = new BillSubscription();
        subscription.setId("0DF60592-62FE-4EBC-B446-03395D0E60AA");
        subscription.setDocumentNumber(83);
        subscription.setItemId("4e686eed-b367-485d-b341-20a8da8ac327");

        BillProduct product = new BillProduct();
        product.setId("ab4bb66a-8ffa-48ea-8226-86cdf23b5372");
        product.setCode("MZ-SR-P004");
        product.setName("Pump Monitoring Service - Standard Edition");

        BillRatePlan ratePlan = new BillRatePlan();
        ratePlan.setId("501c6ea0-db1c-4c8c-be55-ab527e1aa3e4");

        MonetaryAmount netAmount = new MonetaryAmount();
        netAmount.setCurrency("USD");
        netAmount.setAmount(-15.2f);

        Charge charge = getSampleCharge();

        BillItem billItem = new BillItem();
        billItem.setSubscription(subscription);
        billItem.setProduct(product);
        billItem.setRatePlan(ratePlan);
        billItem.setNetAmount(netAmount);
        billItem.setCharges(List.of(charge));
        billItem.setCredits(Collections.emptyList());

        return billItem;
    }

    private Charge getSampleCharge() {
        RatingPeriod ratingPeriod = new RatingPeriod();
        ratingPeriod.setStart(stringToDate("2019-11-11T05:00:00Z"));
        ratingPeriod.setEnd(stringToDate("2019-12-11T05:00:00Z"));

        Quantity consumedQuantity = new Quantity();
        consumedQuantity.setUnit("Each");
        consumedQuantity.setValue(1f);

        Quantity chargedQuantity = new Quantity();
        chargedQuantity.setUnit("Each");
        chargedQuantity.setValue(1f);

        Quantity includedQuantity = new Quantity();
        includedQuantity.setUnit("Each");
        includedQuantity.setValue(0f);

        MonetaryAmount netAmount = new MonetaryAmount();
        netAmount.setCurrency("USD");
        netAmount.setAmount(-19f);

        Charge charge = new Charge();
        charge.setMetricId("Recurrring");
        charge.setRatingPeriod(ratingPeriod);
        charge.setConsumedQuantity(consumedQuantity);
        charge.setNetAmount(netAmount);
        charge.setReversal(true);

        return charge;
    }

    private Subscription getSampleSubscription() {
        Metadata metadata = new Metadata();
        metadata.setVersion(2);

        BusinessPartner customer = new BusinessPartner();
        customer.setId("6301453447");

        Market market = new Market();
        market.setId("US");
        market.setTimeZone("America/New_York");
        market.setCurrency("USD");

        CancellationPolicy cancellationPolicy = new CancellationPolicy();
        cancellationPolicy.setAllowMidBillCycleExpiration(false);
        cancellationPolicy.setWithdrawalPeriod(15);
        cancellationPolicy.setWithdrawalPeriodEndDate(stringToDate("2019-11-27T05:00:00.000Z"));

        Payment payment = new Payment();
        payment.setPaymentMethod("External Card");
        payment.setPaymentCardToken("0a099acd-2a32-48c1-8da7-d980ad6e210e");

        PrecedingDocument precedingDocument = new PrecedingDocument();
        precedingDocument.setId("6887e969-0b93-48a0-b414-02e255cc8de8");
        precedingDocument.setItemId("78e53648-cf42-4803-b43e-c2af9af16131_1");
        precedingDocument.setDocumentNumber(129);
        precedingDocument.setSource("provisioning-request");

        Product product = new Product();
        product.setId("ab4bb66a-8ffa-48ea-8226-86cdf23b5372");
        product.setCode("MZ-SR-P004");
        product.setName("Pump Monitoring Service - Standard Edition");

        RatePlan ratePlan = new RatePlan();
        ratePlan.setId("501c6ea0-db1c-4c8c-be55-ab527e1aa3e4");
        ratePlan.setSource("product");

        Pricing pricing = new Pricing();

        Item item = new Item();
        item.setItemId("78e53648-cf42-4803-b43e-c2af9af16131");
        item.setLineNumber("1");
        item.setProduct(product);
        item.setRatePlan(ratePlan);
        item.setPricing(pricing);
        item.setTechnicalResources(Collections.emptyList());
        item.setSubscriptionType("Commercial");
        item.setCreateBill(true);
        item.setCreateInvoice(true);
        item.setCreateRating(true);
        item.setSubscriptionParameters(Collections.emptyList());
        item.setCustomReferences(Collections.emptyList());

        Snapshot snapshot = new Snapshot();
        snapshot.setEffectiveDate(stringToDate("2019-11-11T11:15:20.144Z"));
        snapshot.setPrecedingDocument(precedingDocument);
        snapshot.setItems(List.of(item));

        ContractTerm contractTerm = new ContractTerm();
        contractTerm.setStartDate(stringToDate("2019-11-11T11:15:20.144Z"));
        contractTerm.setPeriod(1);
        contractTerm.setEndDate(stringToDate("2019-12-11T05:00:00.000Z"));
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        subscription.setDocumentNumber(84);
        subscription.setMetadata(metadata);
        subscription.setValidFrom(stringToDate("2019-11-11T11:16:42.906Z"));
        subscription.setBillingCycleReferenceDate(stringToDate("2019-11-11T05:00:00.000Z"));
        subscription.setCreatedAt(stringToDate("2019-11-11T11:16:42.906Z"));
        subscription.setCreatedBy("Provisioning-Request");
        subscription.setCustomer(customer);
        subscription.setMarket(market);
        subscription.setCancellationPolicy(cancellationPolicy);
        subscription.setCustomReferences(Collections.emptyList());
        subscription.setContractTerm(contractTerm);
        subscription.setCancelledWithOverruledTerms(false);
        subscription.setStatus("Active");
        subscription.setBillingCycle("anniversary-monthly");
        subscription.setSnapshots(List.of(snapshot));

        return subscription;
    }

    public void mockCurrentUser() {
        when(mockUserService.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getRevenueCloudCustomerId()).thenReturn(CUSTOMER_ID);
    }

    private void mockDestinationService() {
        AbstractDestinationModel destinationModel = new ExposedDestinationModel();
        destinationModel.setActive(true);
        destinationModel.setCredential(new ExposedOAuthCredentialModel());
        destinationModel.setDestinationTarget(new DestinationTargetModel());
        destinationModel.setEndpoint(new EndpointModel());
        destinationModel.setId("sapSubscriptionBilling");
        destinationModel.setUrl("https://sample.subscriptionbilling.url");
        when(mockDestinationService.getDestinationById(anyString())).thenReturn(destinationModel);
    }

    private <T> ResponseEntity<T> getResponseEntity(T obj){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("x-count", "100");
        headers.add("x-pagecount", "10");
        return new ResponseEntity<>(obj, headers, HttpStatus.valueOf(200));
    }

    private static class BillMetadata extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Metadata {
    }

    private static class BillMarket extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Market {
    }

    private static class BillPayment extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Payment {
    }

    private static class BillSubscription extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Subscription {
    }

    private static class BillProduct extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Product {
    }

    private static class BillRatePlan extends de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.RatePlan {
    }
}

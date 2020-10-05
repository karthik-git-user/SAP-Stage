/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.exception.SapSubscriptionConfigurationException;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.SubscriptionExtensionResponse;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.BillService;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.saprevenuecloudorder.data.MetadataData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionFormData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionfacades.data.PaymentData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionfacades.impl.DefaultSubscriptionFacade;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * SAR RevenueCloud implementation of the {@link SapRevenueCloudSubscriptionFacade} interface and extending few methods {@link DefaultSubscriptionFacade} class.
 */
public class DefaultSapRevenueCloudSubscriptionFacade extends DefaultSubscriptionFacade
        implements SapRevenueCloudSubscriptionFacade {

    private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionFacade.class);
    private static final String ASC = "asc";

    private UserService userService;
    private GenericDao<SAPRevenueCloudConfigurationModel> sapRevenueCloudConfigurationModelGenericDao;

    private BaseStoreService baseStoreService;
    private ConfigurationService configurationService;
    private CustomerFacade customerFacade;
    private Converter<Subscription, SubscriptionData> subscriptionConverter;
    private Converter<Subscription, SubscriptionData> subscriptionSummaryConverter;
    private Converter<SubscriptionExtensionResponse, SubscriptionExtensionData> sapSubscriptionExtensionConverter;
    private Converter<BillItem, SubscriptionBillingData> billItemConverter;
    private Converter<Bill, SubscriptionBillingData> billSummaryConverter;
    private B2BUnitService b2bUnitService;

    private Map<String, String> cardTypeMap;

    private SubscriptionService subscriptionService;
    private BillService billService;
    private List<String> subscriptionSortingOptions;
    private List<String> billSortingOptions;

    @Override
    @NonNull
    public Collection<SubscriptionData> getSubscriptions() throws SubscriptionFacadeException {
        //Prepare Input
        String customerId = getRevenueCloudCustomerId();

        //Call Service
        final List<Subscription> subscriptions;
        try {
            subscriptions = getSubscriptionService().getSubscriptionsByClientId(customerId);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Unable to fetch subscriptions for clientId: [%s]", customerId));
            throw new SubscriptionFacadeException(e.getMessage());
        }

        //Prepare Output
        LOG.info(String.format("Customer [%s] subscriptions with descending creation date:", customerId));
        subscriptions.forEach(entry -> LOG.info(entry.getSubscriptionId()));
        return Converters.convertAll(subscriptions, subscriptionSummaryConverter);
    }

    @Override
    public SearchPageData<SubscriptionData> getSubscriptions(final int currentPage, final int pageSize, final String sort) throws SubscriptionFacadeException {

        //Prepare Input
        String customerId = getRevenueCloudCustomerId();

        //Call Service
        final PaginationResult<List<Subscription>> response;
        try {
            response = getSubscriptionService().getSubscriptionsByClientIdPage(customerId, currentPage, pageSize, sort);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Unable to fetch subscriptions page for clientId: [%s]", customerId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        //Prepare Output
        LOG.info(String.format("Customer [%s] subscriptions with descending creation date:", customerId));
        final List<Subscription> subscriptionList = response.getResult();
        final List<SubscriptionData> subscriptionDataList = Converters.convertAll(subscriptionList, this.subscriptionSummaryConverter);
        return prepareSearchPageData(response, subscriptionDataList, this.subscriptionSortingOptions);
    }

    @Override
    public SubscriptionData getSubscription(final String subscriptionId) throws SubscriptionFacadeException {

        //Call Service
        final Subscription subscription;
        try {
            subscription = getSubscriptionService().getSubscription(subscriptionId);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while fetching Subscription for id [%s]", subscriptionId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        //Process Output
        checkAuthorization(subscription);
        return getSubscriptionConverter().convert(subscription);
    }

    @Override
    public void cancelSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException {

        //Check Authorization
        checkAuthorization(subscriptionData.getId());

        String cancellationReason = getConfigurationService().getConfiguration().getString(SaprevenuecloudorderConstants.CANCELLATION_REASON);

        int version;
        try {
            version = Integer.parseInt(subscriptionData.getVersion());
        }
        catch (NumberFormatException ex){
            throw new SubscriptionFacadeException("Unable to parse metadata version. Required for cancellation");
        }
        final Metadata metadata = new Metadata();
        metadata.setVersion(version);

        String requestedCancellationDate = subscriptionData.getValidTillDate();
        //If empty valid date take value from endDate
        if(StringUtils.isBlank(requestedCancellationDate)){
            requestedCancellationDate = SapRevenueCloudSubscriptionUtil.dateToUtcString(subscriptionData.getEndDate());
        }

        //Prepare input
        CancellationRequest cancellationRequest = new CancellationRequest();
        cancellationRequest.setCancellationReason(cancellationReason);
        cancellationRequest.setRequestedCancellationDate(requestedCancellationDate);
        cancellationRequest.setMetadata(metadata);

        //Call Service
        try {
            getSubscriptionService().cancelSubscription(subscriptionData.getId(), cancellationRequest);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while cancelling Subscription for id [%s]", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public void withdrawSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException {
        //Check Authorization
        checkAuthorization(subscriptionData.getId());

        //Prepare Input
        final WithdrawalRequest withdrawSubscription = buildWithdrawRequest(subscriptionData);

        //Call Service
        try {
            getSubscriptionService().withdrawSubscription(subscriptionData.getId(), withdrawSubscription);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while withdrawing Subscription id [%s]", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public SubscriptionExtensionData extendSubscription(String subscriptionId, SubscriptionExtensionFormData formData, boolean simulation) throws SubscriptionFacadeException {

        //Check Authorization
        checkAuthorization(subscriptionId);

        Date extensionDate = formData.getExtensionDate();
        Integer billingPeriods = formData.getNumberOfBillingPeriods();
        Boolean isUnlimited = formData.getUnlimited();

        MetadataData metadataData = formData.getMetadata();
        Metadata metadata;
        if (metadataData != null) {
            metadata = new Metadata();
            metadata.setVersion(metadataData.getVersion());
        } else {
            throw new SubscriptionFacadeException("Metadata cannot be null");
        }

        //Prepare Input
        ExtensionRequest extensionRequest = new ExtensionRequest();
        extensionRequest.setExtensionDate(extensionDate);
        extensionRequest.setNumberOfBillingPeriods(billingPeriods);
        extensionRequest.setMetadata(metadata);
        extensionRequest.setUnlimited(isUnlimited);

        //Call Service
        ExtensionResponse response;
        try {
            response = getSubscriptionService().extendSubscription(subscriptionId, extensionRequest, simulation);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while fetching Subscription id [%s]", subscriptionId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        //Prepare Result
        Date validUntilDate = response.getValidUntil();

        Boolean validUntilIsUnlimited = response.getValidUntilIsUnlimited();

        SubscriptionExtensionData extensionResponse = new SubscriptionExtensionData();
        extensionResponse.setValidUntil(validUntilDate);
        extensionResponse.setValidUntilIsUnlimited(validUntilIsUnlimited);
        return extensionResponse;
    }

    @Override
    public void changePaymentDetailsAsCard(SubscriptionData subscriptionData, String paymentCardId) throws SubscriptionFacadeException {
        //Check Authorization
        checkAuthorization(subscriptionData.getId());

        //Find Payment Card Token from id
        if (StringUtils.isBlank(paymentCardId)) {
            throw new SubscriptionFacadeException("Payment Card Id cannot be null");
        }

        String paymentCardToken = getPaymentTokenFromId(paymentCardId);
        if (paymentCardToken == null) {
            throw new SubscriptionFacadeException("Payment Card Id is invalid or doesn't belong to customer");
        }

        final Metadata metaData = new Metadata();
        final Payment payment = new Payment();

        String paymentMethod = getRevenueCloudConfiguration().getPaymentMethod();
        metaData.setVersion(parseInteger(subscriptionData.getVersion()));

        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentCardToken(paymentCardToken);
        String customerEmail = getCustomerFacade().getCurrentCustomer().getUid();

        //Prepare Input
        final PaymentRequest changePaymentData = new PaymentRequest();
        changePaymentData.setMetadata(metaData);
        changePaymentData.setPayment(payment);
        changePaymentData.setChangedBy(customerEmail);

        try {
            getSubscriptionService().updatePayment(subscriptionData.getId(), changePaymentData);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while changing payment for subscription [%s]", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public void changePaymentDetailsAsInvoice(SubscriptionData subscriptionData) throws SubscriptionFacadeException {
        //Check Authorization
        checkAuthorization(subscriptionData.getId());

        final PaymentRequest changePaymentData = new PaymentRequest();
        final Metadata metaData = new Metadata();
        final Payment payment = new Payment();

        metaData.setVersion(parseInteger(subscriptionData.getVersion()));
        changePaymentData.setMetadata(metaData);

        String paymentInvoice = getRevenueCloudConfiguration().getInvoiceMethod();
        payment.setPaymentMethod(paymentInvoice);
        changePaymentData.setPayment(payment);

        String customerEmail = getCustomerFacade().getCurrentCustomer().getUid();
        changePaymentData.setChangedBy(customerEmail);

        //Call Service
        try {
            getSubscriptionService().updatePayment(subscriptionData.getId(), changePaymentData);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while setting payment as Invoice for subscription [%s]", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public SubscriptionData computeCancellationDate(String subscriptionId) throws SubscriptionFacadeException {

        //Check Authorization
        checkAuthorization(subscriptionId);

        final String DATE_TIME_PATTERN = "yyyy-MM-dd";
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
        final Date currentDate = Date.from(ZonedDateTime.now().toInstant());
        final String reqCancellationDate = formatter.format(currentDate);


        EffectiveExpirationDate effExpirationDate;
        try {
            effExpirationDate = getSubscriptionService().computeCancellationDate(subscriptionId, reqCancellationDate);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while computing cancellation date for subscription [%s]", subscriptionId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        String strValidTillDate = SapRevenueCloudSubscriptionUtil.dateToString(effExpirationDate.getEffectiveExpirationDate());

        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setEndDate(effExpirationDate.getEffectiveExpirationDate());
        subscriptionData.setValidTillDate(strValidTillDate);
        return subscriptionData;
    }

    @Override
    public List<SubscriptionBillingData> getSubscriptionBills(String fromDate, String toDate) throws SubscriptionFacadeException {

        //Prepare Input
        String customerId = getRevenueCloudCustomerId();

        //Call Service
        PaginationResult<List<Bill>> billsPage;
        try {
            billsPage = getBillService().getBillsPageByCustomerId(customerId, fromDate, toDate, 0, Integer.MAX_VALUE, null);
        } catch (SubscriptionServiceException ex) {
            LOG.error("Exception Occurred while fetching bills page", ex);
            throw new SubscriptionFacadeException(ex.getMessage());
        }

        //Prepare Output
        List<Bill> bills = billsPage.getResult();
        return getBillSummaryConverter().convertAll(bills);
    }

    @Override
    public SearchPageData<SubscriptionBillingData> getSubscriptionBillsHistory(
            String fromDate,
            String toDate,
            final int currentPage,
            final int pageSize,
            String sort) throws SubscriptionFacadeException {

        //Prepare Input
        String customerId = getRevenueCloudCustomerId();

        //Call Service
        final PaginationResult<List<Bill>> billsPage;
        try {
            billsPage = getBillService().getBillsPageByCustomerId(customerId, fromDate, toDate, currentPage, pageSize, sort);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while fetching subscription bills for customer [%s]", customerId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        List<Bill> bills = billsPage.getResult();
        final List<SubscriptionBillingData> billingDataList = Converters.convertAll(bills, getBillSummaryConverter());

        return prepareSearchPageData(billsPage, billingDataList, this.billSortingOptions);
    }

    @Override
    public List<SubscriptionBillingData> getBillDetails(String billId) throws SubscriptionFacadeException {
        //Call Service
        List<SubscriptionBillingData> billingDataList;
        try {
            Bill bill = getBillService().getBill(billId);

            //Check Authorization
            checkAuthorization(bill);
            billingDataList = getBillItemConverter().convertAll(bill.getBillItems());
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while fetching bill with id [%s]", billId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }

        return billingDataList;
    }

    @Override
    public List<SubscriptionBillingData> getSubscriptionBillsById(String billId) {
        throw new NotImplementedException("getSubscriptionBillsById(billId) is not implemented instead use getBillDetails(String)");
    }

    @Override
    public void populateCardTypeName(CCPaymentInfoData paymentDetails) {
        if (paymentDetails == null) {
            LOG.warn("Payment Details is null");
            return;
        }

        String cardTypeName = getCardTypeMap().getOrDefault(paymentDetails.getCardType(), paymentDetails.getCardType());
        CardTypeData cardTypeData = paymentDetails.getCardTypeData();
        if (cardTypeData == null) {
            cardTypeData = new CardTypeData();
        }

        cardTypeData.setName(cardTypeName);
        paymentDetails.setCardType(cardTypeName);
    }

    @Override
    public void populateCardTypeName(List<CCPaymentInfoData> paymentDetailsList) {
        for (CCPaymentInfoData paymentDetails : paymentDetailsList) {
            populateCardTypeName(paymentDetails);
        }
    }

    @Override
    public void reverseCancellation(SubscriptionData subscriptionData) throws SubscriptionFacadeException {
        //Check Authorization
        checkAuthorization(subscriptionData.getId());

        Metadata metaData = new Metadata();
        metaData.setVersion(parseInteger(subscriptionData.getVersion()));

        //Prepare Input
        CancellationReversalRequest cancellationReversal = new CancellationReversalRequest();
        cancellationReversal.setMetadata(metaData);

        //Call Service
        try {
            getSubscriptionService().reverseCancellation(subscriptionData.getId(), cancellationReversal);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while reversing cancellation for subscription [%s]", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public void updateSubscription(SubscriptionData subscriptionData) throws SubscriptionFacadeException {
        try {
            //Check Authorization
            checkAuthorization(subscriptionData.getId());

            //Determine Operation
            SubscriptionStatus subscriptionStatus = subscriptionData.getStatus();
            PaymentData paymentData = subscriptionData.getPayment();
            Boolean isUnlimited = subscriptionData.getUnlimited();
            if(subscriptionStatus!=null){
                //Operation is Withdraw or cancel or reverse cancellation
                switch(subscriptionStatus){
                    //Withdraw (ACTIVE->WITHDRAWN state)
                    case WITHDRAWN:
                        this.withdrawSubscription(subscriptionData);
                        break;

                    //Cancel (ACTIVE->CANCELLED state)
                    case CANCELLED:
                        this.cancelSubscription(subscriptionData);
                        break;

                    //Reverse Cancellation (CANCELLED->ACTIVE state)
                    case ACTIVE:
                        this.reverseCancellation(subscriptionData);
                        break;
                    default: throw new SubscriptionFacadeException(String.format("Unknown requested subscription status %s", subscriptionStatus.getCode()));
                }
            }
            //Change Payment
            else if(paymentData!=null && StringUtils.isNotBlank(paymentData.getPaymentMethod()) ){
                String paymentCardId = paymentData.getPaymentCardToken();
                if(StringUtils.isNotBlank(paymentCardId)){
                    this.changePaymentDetailsAsCard(subscriptionData, paymentCardId);
                }else{
                    this.changePaymentDetailsAsInvoice(subscriptionData);
                }
            }
            //Extend Subscription
            else if( isUnlimited!=null || subscriptionData.getEndDate()!=null ){
                ExtensionRequest extensionRequest = new ExtensionRequest();

                Metadata metadata = new Metadata();
                metadata.setVersion(parseInteger(subscriptionData.getVersion()));

                extensionRequest.setMetadata(metadata);
                extensionRequest.setExtensionDate(subscriptionData.getEndDate());
                extensionRequest.setUnlimited(subscriptionData.getUnlimited());

                this.subscriptionService.extendSubscription(subscriptionData.getId(), extensionRequest,false);
            }

            //Unknown Operation
            else{
                throw new SubscriptionFacadeException("Cannot determine operation from data");
            }

        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception occurred while fetching subscription id: %s", subscriptionData.getId()), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
    }

    @Override
    public void extendSubscription(SubscriptionData subscriptionData){
        throw new NotImplementedException("This extend subscription method is not implemented instead use extendSubscription(String , SubscriptionExtensionFormData , boolean=false)");
    }

    @Override
    public SubscriptionData computeExtensionDate(SubscriptionData subscriptionData){
        throw new NotImplementedException("Compute Extend subscription method is not implemented instead use extendSubscription(String , SubscriptionExtensionFormData , boolean=true)");
    }

    @Override
    public List<SubscriptionBillingData> getSubscriptionBillsByBillId(String billId){
        throw new NotImplementedException("getSubscriptionBillsByBillId() method is not implemented instead use UserFacade.getCCPaymentInfos(true)");
    }

    @Override
    public List<CCPaymentInfoData> getCCPaymentDetails(boolean saved){
        throw new NotImplementedException("getCCPaymentDetails() subscription method is not implemented instead use UserFacade.getCCPaymentInfos(true)");
    }

    @Override
    @NonNull
    public List<ProductData> getUpsellingOptionsForSubscription(@Nonnull final String productCode) {
        return Collections.emptyList();
    }

    //<editor-fold desc="Utility Methods">
    private String getPaymentTokenFromId(String paymentCardId) {
        final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
        final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
                .getCreditCardPaymentInfoForCode(currentCustomer, paymentCardId);
        if (ccPaymentInfoModel == null) {
            return null;
        }
        return ccPaymentInfoModel.getSubscriptionId();
    }

    private <T, R> SearchPageData<T> prepareSearchPageData(final PaginationResult<List<R>> response, List<T> results, List<String> sortOptions) {

        PaginationData paginationData = new PaginationData();
        paginationData.setCurrentPage(response.getPageIndex());
        paginationData.setHasNext(response.getPageIndex() < response.getPageCount() - 1);
        paginationData.setHasPrevious(response.getPageIndex() > 0);
        paginationData.setTotalNumberOfResults(response.getCount());
        paginationData.setNumberOfPages(response.getPageCount() ); //Since Commerce page starts from 0
        paginationData.setPageSize(response.getPageSize());
        paginationData.setNeedsTotal(true);

        List<SortData> sorts = new LinkedList<>();
        for(String sortOption: sortOptions ){
            if(StringUtils.isBlank(sortOption) || !sortOption.contains(",")){
                continue;
            }

            SortData sortData = new SortData();
            String[] sortPair = sortOption.trim().split(",");
            sortData.setCode(sortPair[0]);
            sortData.setAsc(sortPair[1].trim().equalsIgnoreCase(ASC));
            sorts.add(sortData);
        }

        final SearchPageData<T> searchPageData = new SearchPageData<>();
        searchPageData.setPagination(paginationData);
        searchPageData.setResults(results);
        searchPageData.setSorts(sorts);

        return searchPageData;
    }

    private String getRevenueCloudCustomerId() throws SubscriptionFacadeException {
        String customerId;
        final CustomerModel customer = ((CustomerModel) getUserService().getCurrentUser());
        if (customer instanceof B2BCustomerModel) {
            B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            B2BUnitModel b2bUnit = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);
            B2BUnitModel rootUnit = (B2BUnitModel) getB2bUnitService().getRootUnit(b2bUnit);
            customerId = rootUnit.getRevenueCloudCompanyId();

        } else {
            customerId = ((CustomerModel) userService.getCurrentUser()).getRevenueCloudCustomerId();
        }

        if(StringUtils.isBlank(customerId)){
            throw new SubscriptionFacadeException("SAP Subscription billing customer id is empty");
        }
        return customerId;
    }

    private Integer parseInteger(String str) throws SubscriptionFacadeException {
        int strInt;
        try {
            strInt = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            LOG.error(ex);
            throw new SubscriptionFacadeException(String.format("string '[%s]' cannot be parsed", str));
        }
        return strInt;
    }

    private void checkAuthorization(String subscriptionId) throws SubscriptionFacadeException{
        final Subscription subscription;
        try {
            subscription = getSubscriptionService().getSubscription(subscriptionId);
        } catch (SubscriptionServiceException e) {
            LOG.error(String.format("Exception Occurred while fetching Subscription for id [%s]", subscriptionId), e);
            throw new SubscriptionFacadeException(e.getMessage());
        }
        checkAuthorization(subscription);
    }

    private void checkAuthorization(Subscription subscription) throws SubscriptionFacadeException{
        String currentCustomerId = getRevenueCloudCustomerId();
        String subscriptionCustomerId = subscription.getCustomer().getId();

        if(!StringUtils.equals( subscriptionCustomerId, currentCustomerId )){
            throw new SubscriptionFacadeException(String.format("Subscription id %s does not belong to current user %s", subscription.getSubscriptionId(), currentCustomerId) );
        }

    }

    private void checkAuthorization(Bill bill) throws SubscriptionFacadeException{
        String currentCustomerId = getRevenueCloudCustomerId();
        String billCustomerId = bill.getCustomer().getId();

        if(!StringUtils.equals( billCustomerId, currentCustomerId )){
            throw new SubscriptionFacadeException(String.format("Bill id %s does not belong to current user %s", bill.getDocumentNumber(), currentCustomerId) );
        }
    }

    private WithdrawalRequest buildWithdrawRequest(SubscriptionData subscriptionData) throws SubscriptionFacadeException {
        Integer version;
        try {
            version = parseInteger(subscriptionData.getVersion());
        }
        catch (SubscriptionFacadeException ex){
            throw new SubscriptionFacadeException("Operation Cannot be performed without metadata");
        }
        final Metadata metaData = new Metadata();
        metaData.setVersion(version);

        //Prepare Input
        final WithdrawalRequest withdrawSubscription = new WithdrawalRequest();
        withdrawSubscription.setMetadata(metaData);
        return withdrawSubscription;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    private UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Override
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public void setSubscriptionSummaryConverter(Converter<Subscription, SubscriptionData> subscriptionSummaryConverter) {
        this.subscriptionSummaryConverter = subscriptionSummaryConverter;
    }

    private ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private Converter<Subscription, SubscriptionData> getSubscriptionConverter() {
        return subscriptionConverter;
    }

    public void setSubscriptionConverter(Converter<Subscription, SubscriptionData> subscriptionConverter) {
        this.subscriptionConverter = subscriptionConverter;
    }

    private B2BUnitService getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(B2BUnitService b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    private CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public void setCustomerFacade(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    public Converter<SubscriptionExtensionResponse, SubscriptionExtensionData> getSapSubscriptionExtensionConverter() {
        return sapSubscriptionExtensionConverter;
    }

    public void setSapSubscriptionExtensionConverter(Converter<SubscriptionExtensionResponse, SubscriptionExtensionData> sapSubscriptionExtensionConverter) {
        this.sapSubscriptionExtensionConverter = sapSubscriptionExtensionConverter;
    }

    protected SAPRevenueCloudConfigurationModel getRevenueCloudConfiguration() {
        final Optional<SAPRevenueCloudConfigurationModel> revenueCloudConfigOpt = getSapRevenueCloudConfigurationModelGenericDao()
                .find().stream().findFirst();
        if (revenueCloudConfigOpt.isPresent()) {
            return revenueCloudConfigOpt.get();
        } else {
            LOG.error("No Revenue Cloud Configuration found.");
            throw new SapSubscriptionConfigurationException("Missing Revenue Cloud Configurations");
        }
    }

    private GenericDao<SAPRevenueCloudConfigurationModel> getSapRevenueCloudConfigurationModelGenericDao() {
        return sapRevenueCloudConfigurationModelGenericDao;
    }

    public void setSapRevenueCloudConfigurationModelGenericDao(GenericDao<SAPRevenueCloudConfigurationModel> sapRevenueCloudConfigurationModelGenericDao) {
        this.sapRevenueCloudConfigurationModelGenericDao = sapRevenueCloudConfigurationModelGenericDao;
    }

    private Map<String, String> getCardTypeMap() {
        return cardTypeMap;
    }

    public void setCardTypeMap(Map<String, String> cardTypeMap) {
        this.cardTypeMap = cardTypeMap;
    }

    private SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    private BillService getBillService() {
        return billService;
    }

    public void setBillService(BillService billService) {
        this.billService = billService;
    }

    private Converter<BillItem, SubscriptionBillingData> getBillItemConverter() {
        return billItemConverter;
    }

    public void setBillItemConverter(Converter<BillItem, SubscriptionBillingData> billItemConverter) {
        this.billItemConverter = billItemConverter;
    }

    private Converter<Bill, SubscriptionBillingData> getBillSummaryConverter() {
        return billSummaryConverter;
    }

    public void setBillSummaryConverter(Converter<Bill, SubscriptionBillingData> billSummaryConverter) {
        this.billSummaryConverter = billSummaryConverter;
    }

    public void setSubscriptionSortingOptions(List<String> subscriptionSortingOptions) {
        this.subscriptionSortingOptions = subscriptionSortingOptions;
    }

    public void setBillSortingOptions(List<String> billSortingOptions) {
        this.billSortingOptions = billSortingOptions;
    }

    //</editor-fold>
}

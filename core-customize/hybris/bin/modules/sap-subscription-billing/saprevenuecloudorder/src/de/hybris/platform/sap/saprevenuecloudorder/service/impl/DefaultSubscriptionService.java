/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Default Subscription Service to communicate with Sap Subscription Billing system
 */
public class DefaultSubscriptionService implements SubscriptionService {

    private static final Logger LOG = Logger.getLogger(DefaultSubscriptionService.class);

    private static final String KEY_CUSTOMER_ID = "customer.id";
    private static final String KEY_DOC_NO_DESC = "documentNumber,desc";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private SubscriptionBillingApiClient sbApiClient;
    private UserService userService;

    @Override
    public List<Subscription> getSubscriptionsByClientId(final String clientId) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .queryParam(KEY_CUSTOMER_ID, clientId)
                .queryParam("sort", KEY_DOC_NO_DESC)
                .build();

        //Call API
        Subscription[] subscriptions;
        try {
            subscriptions = sbApiClient.getEntity(uriComponents, Subscription[].class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while fetching subscriptions: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }

        //Prepare Output
        return List.of(subscriptions);
    }

    @Override
    public PaginationResult<List<Subscription>> getSubscriptionsByClientIdPage(String clientId, Integer pageIdx, Integer pageSize, String sort) throws SubscriptionServiceException {
        //Prepare Input
        sort = StringUtils.defaultIfBlank(sort, KEY_DOC_NO_DESC);

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .queryParam(KEY_CUSTOMER_ID, clientId)
                .queryParam("pageNumber", pageIdx + 1) //Revenue Cloud Page number starts from 1 whereas commerce page index starts from 0
                .queryParam("pageSize", pageSize)
                .queryParam("sort", sort)
                .build();

        //Call API
        ResponseEntity<Subscription[]> rawSubscriptions;
        try {
            rawSubscriptions = sbApiClient.getRawEntity(uriComponents, Subscription[].class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while fetching subscriptions page: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }

        //Extract Response
        Subscription[] subscriptions = rawSubscriptions.getBody();
        if (subscriptions == null) {
            throw new SubscriptionServiceException(String.format("Received null as a response for clientId [%s] ", clientId));
        }
        HttpHeaders headers = rawSubscriptions.getHeaders();

        //Prepare output data
        List<Subscription> subscriptionList = List.of(subscriptions);
        Integer count = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-count")));
        Integer pageCount = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-pagecount")));

        //Prepare Output
        PaginationResult<List<Subscription>> page = new PaginationResult<>();
        page.setResult(subscriptionList);
        page.setPageIndex(pageIdx);
        page.setPageCount(pageCount);
        page.setCount(count);
        page.setPageSize(pageSize);

        return page;
    }

    @Override
    public Subscription getSubscription(final String subscriptionId) throws SubscriptionServiceException {
        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.getEntity(uriComponents, Subscription.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while fetching subscription: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }
    }

    @Override
    public CancellationResponse cancelSubscription(final String subscriptionId, final CancellationRequest cancellationForm) throws SubscriptionServiceException {
        //Prepare Input
        if (cancellationForm.getChangedAt() == null) {
            cancellationForm.setChangedAt(new Date());
        }

        if (cancellationForm.getChangedBy() == null) {
            String userMailId = userService.getCurrentUser().getUid();
            cancellationForm.setChangedBy(userMailId);
        }

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/cancellation";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, cancellationForm, CancellationResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while cancelling subscription: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }
    }

    @Override
    public WithdrawalResponse withdrawSubscription(String subscriptionId, WithdrawalRequest withdrawalForm) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/withdrawal";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, withdrawalForm, WithdrawalResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while withdrawing subscription: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }
    }

    @Override
    public PaymentResponse updatePayment(final String subscriptionId, final PaymentRequest paymentDetailsForm) throws SubscriptionServiceException {
        if (paymentDetailsForm.getPayment() == null || StringUtils.isBlank(paymentDetailsForm.getPayment().getPaymentMethod())) {
            throw new SubscriptionServiceException("payment data cannot be null");
        }

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/payment";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, paymentDetailsForm, PaymentResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while updating subscription payment: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }

    }

    @Override
    public BillingFrequencyModel getBillingFrequency(final ProductModel productModel) {
        BillingFrequencyModel billingFrequency = null;
        final SubscriptionTermModel subscriptionTerm = productModel.getSubscriptionTerm();
        if (subscriptionTerm != null) {
            final BillingPlanModel billingPlan = subscriptionTerm.getBillingPlan();
            if (billingPlan != null && billingPlan.getBillingFrequency() != null) {
                billingFrequency = billingPlan.getBillingFrequency();
            }
        }
        return billingFrequency;

    }

    @Override
    public EffectiveExpirationDate computeCancellationDate(final String subscriptionId, String reqCancellationDate) throws SubscriptionServiceException {
        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/computedcancellationdate";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("requestedCancellationDate", reqCancellationDate)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.getEntity(uriComponents, EffectiveExpirationDate.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while fetching subscription effective cancellation date : ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }
    }

    @Override
    public ExtensionResponse extendSubscription(
            String subscriptionId,
            ExtensionRequest extensionForm,
            boolean simulate) throws SubscriptionServiceException {

        //<editor-fold desc="Prepare Input">
        //Prefer Unlimited over number of months
        if (extensionForm.getUnlimited() != null && extensionForm.getUnlimited()) {
            LOG.warn("extension form is marked for unlimited hence removing number of billing periods and extension date ");
            extensionForm.setNumberOfBillingPeriods(null);
            extensionForm.setExtensionDate(null);
        }

        //Default empty values
        if (extensionForm.getChangedAt() == null) {
            extensionForm.setChangedAt(new Date());
        }

        if (extensionForm.getChangedBy() == null) {
            String userMailId = userService.getCurrentUser().getUid();
            extensionForm.setChangedBy(userMailId);
        }
        //</editor-fold>

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/extension";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("simulation", simulate)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, extensionForm, ExtensionResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while extending subscription: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }

    }

    @Override
    public CancellationReversalResponse reverseCancellation(String subscriptionId, CancellationReversalRequest cancellationReversalForm) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/subscription/v1/subscriptions/{id}/cancellationreversal";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", subscriptionId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            return sbApiClient.postEntity(uriComponents, cancellationReversalForm, CancellationReversalResponse.class);
        } catch (HttpClientErrorException clientError) {
            LOG.error("Error while reversing subscription cancellation: ", clientError);
            String actualError = extractErrorMessage(clientError.getResponseBodyAsString());
            throw new SubscriptionServiceException(actualError);
        }
    }

    //<editor-fold desc="Private Methods">
    private String extractErrorMessage(final String errorJson){
        try {
            JsonObject main = JSON_PARSER.parse(errorJson).getAsJsonObject();
            return main.get("message").getAsString();
        }
        catch (Exception e){
            LOG.error(String.format("Unable to parse json: %s", errorJson), e);
            return "Unknown subscription error. Check server logs for details";
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">

    public void setSbApiClient(SubscriptionBillingApiClient sbApiClient) {
        this.sbApiClient = sbApiClient;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    //</editor-fold>
}

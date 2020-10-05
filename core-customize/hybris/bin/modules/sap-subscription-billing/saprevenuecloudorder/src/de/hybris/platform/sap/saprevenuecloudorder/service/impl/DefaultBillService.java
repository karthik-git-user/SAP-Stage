/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.service.BillService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Default Bill Service for Subscription Billing System
 */
public class DefaultBillService implements BillService {

    private static final Logger LOG = Logger.getLogger(DefaultBillService.class);

    private static final String KEY_CUSTOMER_ID = "customer.id";
    private SubscriptionBillingApiClient sbApiClient;
    private Map<String, String> subscriptionBillServiceSortFieldMap;

    @Override
    public PaginationResult<List<Bill>> getBillsPageByCustomerId(String customerId,
                                                                 String fromDate,
                                                                 String toDate,
                                                                 Integer pageIdx,
                                                                 Integer pageSize,
                                                                 String sort) throws SubscriptionServiceException {
        //Prepare Input
        sort = SapRevenueCloudSubscriptionUtil.commerceToSbSortFormat(sort, this.subscriptionBillServiceSortFieldMap);

        //Prepare Url
        String uriString = "/bill/v2/bills";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString)
                .queryParam(KEY_CUSTOMER_ID, customerId)
                .queryParam("from", StringUtils.defaultIfBlank(fromDate, StringUtils.EMPTY)) //Empty is added because revenue cloud throws error on null
                .queryParam("to", StringUtils.defaultIfBlank(toDate, StringUtils.EMPTY))
                .queryParam("sort", StringUtils.defaultIfBlank(sort, "billingDate,desc")) //Sort does not accept empty hence defaulting
                .queryParam("pageNumber", pageIdx + 1) //Revenue Cloud Page number starts from 1 whereas commerce page number starts from 0
                .queryParam("pageSize", pageSize);

        //Call API
        ResponseEntity<Bill[]> rawBills;
        try {
            rawBills = sbApiClient.getRawEntity(builder.build(), Bill[].class);
        } catch (HttpClientErrorException clientError) {
            LOG.error(String.format("Error while fetching bills page for customer id: %s", customerId), clientError);
            throw new SubscriptionServiceException(clientError.getMessage());
        }

        //Extract Response
        Bill[] bills = rawBills.getBody();
        if (bills == null) {
            throw new SubscriptionServiceException(String.format("Received null as response for clientId [%s] ", customerId));
        }
        HttpHeaders headers = rawBills.getHeaders();

        //Prepare output data
        List<Bill> billList = List.of(bills);
        Integer count = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-count")));
        Integer pageCount = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-pagecount")));

        //Prepare Output
        PaginationResult<List<Bill>> page = new PaginationResult<>();
        page.setResult(billList);
        page.setPageIndex(pageIdx);
        page.setPageCount(pageCount);
        page.setCount(count);
        page.setPageSize(pageSize);

        return page;
    }

    @Override
    public PaginationResult<List<Bill>> getBillsPageBySubscriptionId(String subscriptionId,
                                                     String fromDate,
                                                     String toDate,
                                                     Integer pageIdx,
                                                     Integer pageSize,
                                                     String sort) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/bill/v2/bills";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString)
                .queryParam("billItems.subscription.id", subscriptionId)
                .queryParam("from", StringUtils.defaultIfBlank(fromDate, StringUtils.EMPTY)) //Empty is added because revenue cloud throws error on null
                .queryParam("to", StringUtils.defaultIfBlank(toDate, StringUtils.EMPTY))
                .queryParam("sort", StringUtils.defaultIfBlank(sort, "billingDate,desc")) //Sort does not accept empty hence defaulting
                .queryParam("pageNumber", pageIdx + 1) //Revenue Cloud Page number starts from 1 whereas commerce page number starts from 0
                .queryParam("pageSize", pageSize);

        //Call API
        ResponseEntity<Bill[]> rawBills;
        try {
            rawBills = sbApiClient.getRawEntity(builder.build(), Bill[].class);
        } catch (HttpClientErrorException clientError) {
            LOG.error(String.format("Error while fetching bills page for subscription id: %s", subscriptionId), clientError);
            throw new SubscriptionServiceException(clientError.getMessage());
        }

        //Extract Response
        Bill[] bills = rawBills.getBody();
        if (bills == null) {
            throw new SubscriptionServiceException(String.format("Received null as response for subscriptionId [%s] ", subscriptionId));
        }
        HttpHeaders headers = rawBills.getHeaders();

        //Prepare output data
        List<Bill> billList = List.of(bills);
        Integer count = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-count")));
        Integer pageCount = Integer.parseInt(Objects.requireNonNull(headers.getFirst("x-pagecount")));

        //Prepare Output
        PaginationResult<List<Bill>> page = new PaginationResult<>();
        page.setResult(billList);
        page.setPageIndex(pageIdx);
        page.setPageCount(pageCount);
        page.setCount(count);
        page.setPageSize(pageSize);

        return page;
    }

    @Override
    public Bill getBill(final String billId) throws SubscriptionServiceException {

        //Prepare Url
        String uriString = "/bill/v2/bills/{id}";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", billId);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(pathParams);

        //Call API
        try {
            Bill bill = sbApiClient.getEntity(uriComponents, Bill.class);

            //When there are no items in bill Subscription billing returns "204:No Content" resulting bill as null
            if(bill==null){
                bill = new Bill();
                bill.setBillItems(Collections.emptyList());
            }
            return bill;
        } catch (HttpClientErrorException clientError) {
            LOG.error(String.format("Error while fetching bill for id: %s", billId), clientError);
            throw new SubscriptionServiceException(clientError.getMessage());
        }
    }

    //<editor-fold desc="Getters and Setters">

    public void setSbApiClient(SubscriptionBillingApiClient sbApiClient) {
        this.sbApiClient = sbApiClient;
    }

    public void setSubscriptionBillServiceSortFieldMap(Map<String, String> subscriptionBillServiceSortFieldMap) {
        this.subscriptionBillServiceSortFieldMap = subscriptionBillServiceSortFieldMap;
    }

    //</editor-fold>

}

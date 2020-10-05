/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;

import java.util.List;

/**
 * This service connects to Sap Subscription Billing system and fetches billing related data
 */
public interface BillService {

    /**
     * fetch bills using customerId and dates
     *
     * @param customerId      SAP Subscription billing customer id
     * @param fromDate        the date from which bills should be displayed
     * @param toDate          the date till which bills should be displayed
     * @param pageNumber      pageNumber
     * @param pageSize        pageSize
     * @param sort            sort
     * @return single page of List of {@link Bill}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    PaginationResult<List<Bill>> getBillsPageByCustomerId(final String customerId,
                                                          final String fromDate,
                                                          final String toDate,
                                                          final Integer pageNumber,
                                                          final Integer pageSize,
                                                          final String sort) throws SubscriptionServiceException;


    /**
     * fetch bills using subscriptionId and dates
     *
     * @param subscriptionsId subscriptionId
     * @param fromDate        the date from which bills should be displayed
     * @param toDate          the date till which bills should be displayed
     * @param pageNumber      pageNumber
     * @param pageSize        pageSize
     * @param sort            sort
     * @return single page of List of {@link Bill}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    PaginationResult<List<Bill>> getBillsPageBySubscriptionId(final String subscriptionsId,
                                              final String fromDate,
                                              final String toDate,
                                              final Integer pageNumber,
                                              final Integer pageSize,
                                              final String sort) throws SubscriptionServiceException;

    /**
     * fetch bill using billId
     *
     * @param billId subscription billId
     * @return {@link Bill} bill for the given id
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    Bill getBill(String billId) throws SubscriptionServiceException;
}

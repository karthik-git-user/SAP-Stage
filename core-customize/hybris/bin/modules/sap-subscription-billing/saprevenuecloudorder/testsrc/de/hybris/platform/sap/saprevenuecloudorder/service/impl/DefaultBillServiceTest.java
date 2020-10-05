/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBillServiceTest {

    @Mock
    private SubscriptionBillingApiClient mockSbApiClient;

    @InjectMocks
    DefaultBillService defaultBillService;

    //Constants
    private final String CUSTOMER_ID = "6301453447";
    private final String FROM_DATE = "2019-01-31";
    private final String TO_DATE = "2019-10-31";
    private final Integer INITIAL_PAGE_IDX = 0;
    private final Integer PAGE_SIZE = 10;
    private final String SORT_DOC_ASC = "documentNo,asc";
    private final String BILL_ID = "2";


    @Before
    public void setUp() {

    }

    //<editor-fold desc=“getAuthorizationAmountListFromCart(String, String, String, Integer, String)”>
    @Test
    public void getBillsPage() throws SubscriptionServiceException {

        //Mock
        Bill[] bills = {};
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("x-count", "100");
        headers.add("x-pagecount", "10");
        when(mockSbApiClient.getRawEntity(any(UriComponents.class), eq(Bill[].class)))
                .thenReturn(new ResponseEntity<>(bills, headers, HttpStatus.valueOf(200)));
        defaultBillService.setSubscriptionBillServiceSortFieldMap(Collections.emptyMap());

        //Execute
        PaginationResult<List<Bill>> billsPage = defaultBillService.getBillsPageByCustomerId(CUSTOMER_ID,
                FROM_DATE,
                TO_DATE,
                INITIAL_PAGE_IDX,
                PAGE_SIZE,
                SORT_DOC_ASC);

        //Verify
        Assert.assertNotNull(billsPage);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void getBillsPage_nullCustomer() throws SubscriptionServiceException {

        //Mock
        when(mockSbApiClient.getRawEntity(any(UriComponents.class), eq(Bill[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        defaultBillService.setSubscriptionBillServiceSortFieldMap(Collections.emptyMap());

        //Execute
        defaultBillService.getBillsPageByCustomerId(null,
                FROM_DATE,
                TO_DATE,
                INITIAL_PAGE_IDX,
                PAGE_SIZE,
                SORT_DOC_ASC);
    }
    //</editor-fold>

    //<editor-fold desc="getBill(String)" >
    @Test
    public void getBill() throws SubscriptionServiceException {
        //Mock
        Bill bill = new Bill();
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Bill.class)))
                .thenReturn(bill);

        //Execute
        Bill billResult = defaultBillService.getBill(BILL_ID);

        //Verify
        Assert.assertNotNull(billResult);
    }

    @Test(expected = SubscriptionServiceException.class)
    public void getBill_NullBillId() throws SubscriptionServiceException {
        //Mock
        when(mockSbApiClient.getEntity(any(UriComponents.class), eq(Bill.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //Execute
        defaultBillService.getBill(null);
    }
    //</editor-fold>

}

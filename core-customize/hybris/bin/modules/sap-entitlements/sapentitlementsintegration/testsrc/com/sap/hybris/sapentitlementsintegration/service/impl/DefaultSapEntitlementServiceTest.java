/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapentitlementsintegration.pojo.BusinessCategory;
import com.sap.hybris.sapentitlementsintegration.pojo.Data;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.GetEntitlementRequest;
import com.sap.hybris.sapentitlementsintegration.pojo.TheRight;
import com.sap.hybris.sapentitlementsintegration.pojo.Uom;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementOutboundService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Test for {@DefaultSapEntitlementService}
 */
@UnitTest
public class DefaultSapEntitlementServiceTest {
	
	@InjectMocks
	private DefaultSapEntitlementService service = new DefaultSapEntitlementService();
	@Mock
	private SapEntitlementOutboundService outboundService;
	@Mock
	private UserService userService;
	
	private Entitlements entitlements = new Entitlements();
	
	private Entitlement entitlement = new Entitlement();
	
	private Data data = new Data();
	private String dummyText = "dummyText";
	@Mock
	private BusinessCategory dummyBusinessCategory;
	@Mock
	private TheRight theRight;
	@Mock
	private Uom uom;
	private ArrayList<Entitlement> dummyResponse = new ArrayList<Entitlement>();
	@Mock
	private CustomerModel user;
	private static final int PAGE_NUM = 4;
	private static final int PAGE_SIZE = 5;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicEntitlementFields();
        
        when(outboundService.sendRequest(any(GetEntitlementRequest.class), eq(Entitlements.class), any(String.class))).thenReturn(entitlements);
		when(userService.getCurrentUser()).thenReturn(user);
    }
	
	@Test
	public void testGetEntitlementsForCurrentCustomer() {
		
		Entitlements response = service.getEntitlementsForCurrentCustomer(PAGE_NUM,PAGE_SIZE);
		Assert.assertEquals(response.getData().getResponse().get(0).getUom(), uom);
		Assert.assertEquals(response.getData().getResponse().get(0).getEntitlementNo(), Integer.valueOf(1));
		Assert.assertEquals(response.getData().getResponse().get(0).getBusinessCategory(), dummyBusinessCategory);
		
	}
	
	@Test
	public void testGetEntitlementForNumber() {
		
		Entitlements response = service.getEntitlementForNumber(dummyText);
		Assert.assertEquals(response.getData().getResponse().get(0).getUom(), uom);
		Assert.assertEquals(response.getData().getResponse().get(0).getEntitlementNo(), Integer.valueOf(1));
		Assert.assertEquals(response.getData().getResponse().get(0).getBusinessCategory(), dummyBusinessCategory);
		
	}
	
	@Test(expected = Exception.class)
	public void testNullDestinationModelException () {
		when(userService.getCurrentUser()).thenReturn(null);
		service.getEntitlementsForCurrentCustomer(PAGE_NUM,PAGE_SIZE);
	}
	
	private void mockBasicEntitlementFields() {
		data.setStatus(dummyText);
		entitlement.setEntitlementNo(Integer.valueOf(1));
		entitlement.setEntitlementGuid(dummyText);
		entitlement.setValidFrom(new Date());
		entitlement.setValidTo(new Date());
		entitlement.setStatusName(dummyText);
		entitlement.setRefDocNo(dummyText);
		entitlement.setTheRight(theRight);
		entitlement.setGeolocation(dummyText);
		entitlement.setBusinessCategory(dummyBusinessCategory);
		entitlement.setOfferingID(dummyText);
		entitlement.setEntitlementModelName(dummyText);
		entitlement.setQuantity(Integer.valueOf(5));
		entitlement.setUom(uom);
		entitlement.setEntitlementTypeName(dummyText);
		dummyResponse.add(entitlement);
		data.setResponse(dummyResponse);
		entitlements.setData(data);
	}

}

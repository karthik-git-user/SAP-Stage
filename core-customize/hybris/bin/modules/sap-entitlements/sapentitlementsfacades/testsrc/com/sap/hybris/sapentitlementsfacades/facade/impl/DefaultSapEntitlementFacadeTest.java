/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.facade.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapentitlementsintegration.pojo.Data;
import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsfacades.facade.impl.DefaultSapEntitlementFacade;
import com.sap.hybris.sapentitlementsintegration.pojo.BusinessCategory;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.TheRight;
import com.sap.hybris.sapentitlementsintegration.pojo.Uom;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService;


/**
 * Test for {@DefaultSapEntitlementFacade}
 */
@UnitTest
public class DefaultSapEntitlementFacadeTest {

	@InjectMocks
	private final DefaultSapEntitlementFacade facade = new DefaultSapEntitlementFacade();

	@Mock
	private SapEntitlementService sapEntitlementService;

	@Mock
	private Converter<Entitlement, EntitlementData> sapEntitlementConverter;

	@Mock
	private Converter<Entitlement, EntitlementData> sapEntitlementDetailsConverter;

	@Mock
	private ProductData productData;
	@Mock
	private BusinessCategory dummyBusinessCategory;
	@Mock
	private TheRight theRight;
	@Mock
	private Uom uom;

	private static final int PAGE_NUM = 4;
	private static final int PAGE_SIZE = 5;

	private final Entitlements entitlements = new Entitlements();
	private final Entitlement entitlement = new Entitlement();

	private final EntitlementData entitlementData = new EntitlementData();

	private final Data data = new Data();
	private final String dummyText = "dummyText";
	private final ArrayList<EntitlementData> entitlementDataList = new ArrayList<EntitlementData>();
	private final ArrayList<Entitlement> dummyResponse = new ArrayList<Entitlement>();

	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockBasicEntitlementFields();


    }

	@Test
	public void testGetEntitlementsForCurrentCustomer() {
		when(sapEntitlementService.getEntitlementsForCurrentCustomer(any(Integer.class),any(Integer.class))).thenReturn(entitlements);
		final List<EntitlementData> response = facade.getEntitlementsForCurrentCustomer(PAGE_NUM,PAGE_SIZE);
		doAnswer( i -> {
			Assert.assertEquals(response.get(0).getName(), null);
			return null;
		}).when(sapEntitlementConverter).convert(any(Entitlement.class));

		Assert.assertEquals(entitlementData.getId(), dummyText);
	}

	@Test
	public void testGetEntitlementsForNumber() {
		when(sapEntitlementService.getEntitlementForNumber(anyString())).thenReturn(entitlements);
		final EntitlementData response = facade.getEntitlementForNumber(dummyText);
		doAnswer( i -> {
			Assert.assertEquals(response.getName(), null);
			return null;
		}).when(sapEntitlementDetailsConverter).convert(any(Entitlement.class));

		Assert.assertEquals(entitlementData.getId(), dummyText);
	}

	@Test
	public void testNullEntitlementsForNumber() {

		final EntitlementData response = facade.getEntitlementForNumber(dummyText);
		Assert.assertNull(response.getId());
	}

	private void mockBasicEntitlementFields() {
		entitlementData.setId(dummyText);
		entitlementData.setEntitlementNumber(Integer.valueOf(1));
		entitlementData.setProduct(productData);
		entitlementData.setName(dummyText);
		entitlementData.setDescription(dummyText);
		entitlementData.setStatus(dummyText);
		entitlementData.setQuantity(dummyText);
		entitlementData.setName(dummyText);
		entitlementData.setValidFrom(new Date());
		entitlementData.setValidTo(new Date());
		entitlementData.setOrderNumber(dummyText);
		entitlementData.setType(dummyText);
		entitlementData.setRight(dummyText);
		entitlementData.setRegion(dummyText);
		entitlementDataList.add(entitlementData);
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

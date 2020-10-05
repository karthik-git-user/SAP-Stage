/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.util.*;

import com.sap.hybris.saprevenuecloudproduct.dao.SapSubscriptionPricePlanWithEffectiveDateDao;
import de.hybris.platform.catalog.model.CatalogModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.saprevenuecloudproduct.dao.SapRevenueCloudProductDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;


/**
 * JUnit test suite for {@link DefaultSapRevenueCloudProductServiceTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudProductServiceTest
{

	SubscriptionPricePlanModel pricePlan;

	@Mock
	private SapRevenueCloudProductDao sapRevenueCloudProductDao;

	@Mock
	private SapSubscriptionPricePlanWithEffectiveDateDao sapSubscriptionPricePlanWithEffectiveDateDao;

	@InjectMocks
	private DefaultSapRevenueCloudProductService defaultSapRevenueCloudProductService = new DefaultSapRevenueCloudProductService();

	@Before
	public void setUp() throws Exception
	{
		pricePlan = new SubscriptionPricePlanModel();
	}

	@Test
	public void checkIfPricePlanIsReturnedForValidPricePlanAndCatalogVersion()
	{
		when(sapRevenueCloudProductDao.getSubscriptionPricePlanForId(any(String.class), any(CatalogVersionModel.class)))
				.thenReturn(Optional.of(pricePlan));
		final SubscriptionPricePlanModel pricePlanRes = defaultSapRevenueCloudProductService
				.getSubscriptionPricePlanForId("dummyPricePlan", new CatalogVersionModel());
		assertNotNull(pricePlanRes);


		CatalogModel catalogModel = new CatalogModel();

		catalogModel.setId("electronicsProductCatalog");
		catalogModel.setDefaultCatalog(true);

		CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setActive(true);
		catalogVersionModel.setVersion("Online");
		catalogVersionModel.setCategorySystemID("electronicsProductCatalog");
		catalogVersionModel.setCatalog(catalogModel);





		CatalogModel catalogModel1 = new CatalogModel();
		catalogModel.setId("powertoolsProductCatalog");
		catalogModel.setDefaultCatalog(false);

		CatalogVersionModel catalogVersionModel1 = new CatalogVersionModel();
		catalogVersionModel1.setActive(true);
		catalogVersionModel1.setVersion("Online");
		catalogVersionModel1.setCategorySystemID("powertoolsProductCatalog");
		catalogVersionModel1.setCatalog(catalogModel1);


		List<CatalogVersionModel> catalogVersions = List.of(catalogVersionModel , catalogVersionModel1);


		when(sapSubscriptionPricePlanWithEffectiveDateDao.getSubscriptionPricesWithEffectiveDate(any(), Collections.singletonList(any()),any(Date.class))).thenReturn(Optional.of(pricePlan));
		defaultSapRevenueCloudProductService.setSapSubscriptionPricePlanWithEffectiveDateDao(sapSubscriptionPricePlanWithEffectiveDateDao);
		String pricePlanId ="1327373634746374";
		final SubscriptionPricePlanModel pricePlanRes2 = defaultSapRevenueCloudProductService.getSubscriptionPricesWithEffectiveDate(pricePlanId, catalogVersions,new Date());
		assertNotNull(pricePlanRes2);
	}


}
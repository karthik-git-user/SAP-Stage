/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultSapSubscriptionProductDaoTest}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapSubscriptionProductDaoTest
{

	SubscriptionPricePlanModel pricePlan;
	CatalogVersionModel catalogVersion;

	@Mock
	private FlexibleSearchService flexibleSearchService;


	@InjectMocks
	private DefaultSapSubscriptionProductDao defaultSapSubscriptionProductDao;

	private static final String PRICE_PLAN_ID = "1327373634746374";
	@Before
	public void setUp()
	{
		pricePlan = new SubscriptionPricePlanModel();
		catalogVersion = getDummyCatalogVersion();

	}

	@Test
	public void checkSubscriptionPricePlanForValidPricePlanIdAndCatalogVersion()
	{

		when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(new SubscriptionPricePlanModel());

		final Optional<SubscriptionPricePlanModel> pricePlanOpt = defaultSapSubscriptionProductDao
				.getSubscriptionPricePlanForId(PRICE_PLAN_ID, catalogVersion);
		assertNotNull(pricePlanOpt.get());

	}

	@Test(expected = IllegalArgumentException.class)
	public void throwErrorIfArgumentsAreInvalid()
	{
		defaultSapSubscriptionProductDao.getSubscriptionPricePlanForId(null, null);
	}

	@Test
	public void provideEmptySubscriptionForNoModelFound()
	{
		when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class)))
				.thenThrow(new ModelNotFoundException("No model found"));

		final Optional<SubscriptionPricePlanModel> pricePlanOpt = defaultSapSubscriptionProductDao
				.getSubscriptionPricePlanForId(PRICE_PLAN_ID, catalogVersion);
		assertEquals(pricePlanOpt, Optional.empty());
	}

	@Test
	public void provideEmptySubscriptionForAmbigiousModelFound()
	{
		when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class)))
				.thenThrow(new AmbiguousIdentifierException("Ambigious identifier"));

		final Optional<SubscriptionPricePlanModel> pricePlanOpt = defaultSapSubscriptionProductDao
				.getSubscriptionPricePlanForId(PRICE_PLAN_ID, catalogVersion);
		assertEquals(pricePlanOpt, Optional.empty());
	}

	private CatalogVersionModel getDummyCatalogVersion()
	{
		catalogVersion = new CatalogVersionModel();
		final CatalogModel catalog = new CatalogModel();
		catalog.setId("dummyCatalogId");
		catalogVersion.setVersion("Staged");
		catalogVersion.setCatalog(catalog);
		return catalogVersion;


	}




}

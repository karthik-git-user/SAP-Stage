/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.stock.impl;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.PointOfServiceDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCosOaaCommerceStockServiceTest
{
	@Mock
	ProductModel product;

	@Mock
	BaseStoreModel baseStore;

	@Mock
	StockData stockData;

	@Mock
	DefaultSapOaaCommerceStockService defaultSapOaaCommerceStockService;

	@Mock
	private ATPService atpService;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private ATPAggregationStrategy atpAggregationStrategy;




	@InjectMocks
	private DefaultSapCosOaaCommerceStockService defaultSapCosOaaCommerceStockService;

	private final List<ATPAvailability> productAvailabilityList = new ArrayList<ATPAvailability>();
	private final ATPAvailability atpAvailability = new ATPAvailability();

	@Before
	public void setUp()
	{
		//ATPAvailability
		atpAvailability.setAtpDate(new Date());
		atpAvailability.setQuantity(2d);

		//ProductAvailabilityList
		productAvailabilityList.add(atpAvailability);


	}


	@Test
	public void getStockLevelForProductAndBaseStoreInstockTest()
	{
		//SetUp
		Long result;
		when(atpService.callRestAvailabilityServiceForProduct(anyString(), anyString(), anyObject()))
				.thenReturn(productAvailabilityList);

		when(atpAggregationStrategy.aggregateAvailability(anyString(), anyObject(), anyObject(), anyList()))
				.thenReturn(Long.valueOf("2"));

		//Execute
		result = defaultSapCosOaaCommerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);

		//Verify
		Assert.assertNull(result);
	}


	@Test
	public void getStockLevelForProductAndBaseStoreOutOfStockTest()
	{
		//SetUp
		Long result;
		when(atpService.callRestAvailabilityServiceForProduct(anyString(), anyString(), anyObject()))
				.thenReturn(productAvailabilityList);

		when(atpAggregationStrategy.aggregateAvailability(anyString(), anyObject(), anyObject(), anyList()))
				.thenReturn(Long.valueOf("0"));



		//Execute
		result = defaultSapCosOaaCommerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);

		//Verify
		Assert.assertEquals((long) 0, (long) result);
	}


	@Test
	public void getStockDataForProductAndBaseStoreInstockTest()
	{
		//SetUp
		final StockData result;

		when(atpService.callRestAvailabilityServiceForProduct(anyString(), anyString(), anyObject()))
				.thenReturn(productAvailabilityList);

		when(atpAggregationStrategy.aggregateAvailability(anyString(), anyObject(), anyObject(), anyList()))
				.thenReturn(Long.valueOf("2"));

		when(stockData.getStockLevel()).thenReturn(Long.valueOf(2));
		when(stockData.getStockLevelStatus()).thenReturn(StockLevelStatus.INSTOCK);

		//Execute
		result = defaultSapCosOaaCommerceStockService.getStockDataForProductAndBaseStore(product, baseStore, stockData);

		//Verify
		Assert.assertNotNull(result.getStockLevel());
		Assert.assertEquals(Long.valueOf(2), result.getStockLevel());
		Assert.assertEquals(StockLevelStatus.INSTOCK, result.getStockLevelStatus());
	}


	@Test
	public void getStockDataForProductAndBaseStoreOutOfStockTest()
	{
		//SetUp
		final StockData result;
		when(atpService.callRestAvailabilityServiceForProduct(anyString(), anyString(), anyObject()))
				.thenReturn(productAvailabilityList);

		when(atpAggregationStrategy.aggregateAvailability(anyString(), anyObject(), anyObject(), anyList()))
				.thenReturn(Long.valueOf("0"));

		when(stockData.getStockLevel()).thenReturn(Long.valueOf(0));
		when(stockData.getStockLevelStatus()).thenReturn(StockLevelStatus.OUTOFSTOCK);

		//Execute
		result = defaultSapCosOaaCommerceStockService.getStockDataForProductAndBaseStore(product, baseStore, stockData);

		//Verify
		Assert.assertNotNull(result.getStockLevel());
		Assert.assertEquals(Long.valueOf(0), result.getStockLevel());
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, result.getStockLevelStatus());
	}

}

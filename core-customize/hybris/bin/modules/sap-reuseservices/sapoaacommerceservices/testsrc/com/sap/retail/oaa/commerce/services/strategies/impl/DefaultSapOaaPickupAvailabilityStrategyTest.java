/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.strategies.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.PickupStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 */
@UnitTest
public class DefaultSapOaaPickupAvailabilityStrategyTest
{

	private DefaultSapOaaPickupAvailabilityStrategy classUnderTest;

	@Before
	public void setUp()
	{

		this.classUnderTest = new DefaultSapOaaPickupAvailabilityStrategy();

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testIsPickupAvailable_falseNoPointOfService()
	{

		final ProductModel product = null;

		final BaseStoreModel baseStoreMock = EasyMock.createNiceMock(BaseStoreModel.class);
		final List<PointOfServiceModel> pointOfServiceList = new ArrayList<PointOfServiceModel>();
		EasyMock.expect(baseStoreMock.getPointsOfService()).andReturn(pointOfServiceList).anyTimes();
		EasyMock.replay(baseStoreMock);


		final PickupStrategy pickupStrategyMock = createPickupStrategyMock(PickupInStoreMode.BUY_AND_COLLECT);
		classUnderTest.setPickupStrategy(pickupStrategyMock);


		assertFalse(classUnderTest.isPickupAvailableForProduct(product, baseStoreMock).booleanValue());
	}


	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testIsPickupAvailable_falsePickupNotActivated()
	{

		final ProductModel product = null;

		final BaseStoreModel baseStoreMock = createBaseStoreMock();

		final PickupStrategy pickupStrategyMock = createPickupStrategyMock(PickupInStoreMode.DISABLED);
		classUnderTest.setPickupStrategy(pickupStrategyMock);


		assertFalse(classUnderTest.isPickupAvailableForProduct(product, baseStoreMock).booleanValue());
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testIsPickupAvailable_trueInStock()
	{

		final ProductModel product = null;

		final BaseStoreModel baseStoreMock = createBaseStoreMock();

		final PickupStrategy pickupStrategyMock = createPickupStrategyMock(PickupInStoreMode.BUY_AND_COLLECT);
		classUnderTest.setPickupStrategy(pickupStrategyMock);

		final CommerceStockService commerceStockServiceMock = createCommerceStockServiceMock(product, baseStoreMock,
				StockLevelStatus.INSTOCK);
		classUnderTest.setCommerceStockService(commerceStockServiceMock);

		assertTrue(classUnderTest.isPickupAvailableForProduct(product, baseStoreMock).booleanValue());
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testIsPickupAvailable_falseOutOfStock()
	{

		final ProductModel product = null;

		final BaseStoreModel baseStoreMock = createBaseStoreMock();

		final PickupStrategy pickupStrategyMock = createPickupStrategyMock(PickupInStoreMode.BUY_AND_COLLECT);
		classUnderTest.setPickupStrategy(pickupStrategyMock);

		final CommerceStockService commerceStockServiceMock = createCommerceStockServiceMock(product, baseStoreMock,
				StockLevelStatus.OUTOFSTOCK);
		classUnderTest.setCommerceStockService(commerceStockServiceMock);

		assertFalse(classUnderTest.isPickupAvailableForProduct(product, baseStoreMock).booleanValue());
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testIsPickupAvailable_falseEmptyStoreStockMap()
	{

		final ProductModel product = null;

		final BaseStoreModel baseStoreMock = createBaseStoreMock();

		final PickupStrategy pickupStrategyMock = createPickupStrategyMock(PickupInStoreMode.BUY_AND_COLLECT);
		classUnderTest.setPickupStrategy(pickupStrategyMock);

		final CommerceStockService commerceStockServiceMock = createCommerceStockServiceMockEmptyStoreStockMap(product,
				baseStoreMock);
		classUnderTest.setCommerceStockService(commerceStockServiceMock);

		assertFalse(classUnderTest.isPickupAvailableForProduct(product, baseStoreMock).booleanValue());
	}

	private PickupStrategy createPickupStrategyMock(final PickupInStoreMode pickupInStoreMode)
	{
		final PickupStrategy pickupStrategyMock = EasyMock.createNiceMock(PickupStrategy.class);
		EasyMock.expect(pickupStrategyMock.getPickupInStoreMode()).andReturn(pickupInStoreMode).anyTimes();
		EasyMock.replay(pickupStrategyMock);
		return pickupStrategyMock;
	}

	private BaseStoreModel createBaseStoreMock()
	{
		final BaseStoreModel baseStoreMock = EasyMock.createNiceMock(BaseStoreModel.class);
		final List<PointOfServiceModel> pointOfServiceList = new ArrayList<PointOfServiceModel>();
		pointOfServiceList.add(new PointOfServiceModel());
		EasyMock.expect(baseStoreMock.getPointsOfService()).andReturn(pointOfServiceList).anyTimes();
		EasyMock.replay(baseStoreMock);
		return baseStoreMock;
	}

	private CommerceStockService createCommerceStockServiceMock(final ProductModel product, final BaseStoreModel baseStore,
			final StockLevelStatus stockLevelStatus)
	{
		final CommerceStockService commerceStockServiceMock = EasyMock.createNiceMock(CommerceStockService.class);
		final Map<PointOfServiceModel, StockLevelStatus> storeStockLevelStatusMap = new HashMap<PointOfServiceModel, StockLevelStatus>();
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setName("OAS1");
		storeStockLevelStatusMap.put(pointOfService, stockLevelStatus);
		EasyMock.expect(commerceStockServiceMock.getPosAndStockLevelStatusForProduct(product, baseStore))
				.andReturn(storeStockLevelStatusMap);
		EasyMock.replay(commerceStockServiceMock);
		return commerceStockServiceMock;
	}



	private CommerceStockService createCommerceStockServiceMockEmptyStoreStockMap(final ProductModel product,
			final BaseStoreModel baseStore)
	{
		final CommerceStockService commerceStockServiceMock = EasyMock.createNiceMock(CommerceStockService.class);
		final Map<PointOfServiceModel, StockLevelStatus> storeStockLevelStatusMap = new HashMap<PointOfServiceModel, StockLevelStatus>();
		EasyMock.expect(commerceStockServiceMock.getPosAndStockLevelStatusForProduct(product, baseStore))
				.andReturn(storeStockLevelStatusMap);
		EasyMock.replay(commerceStockServiceMock);
		return commerceStockServiceMock;
	}

}

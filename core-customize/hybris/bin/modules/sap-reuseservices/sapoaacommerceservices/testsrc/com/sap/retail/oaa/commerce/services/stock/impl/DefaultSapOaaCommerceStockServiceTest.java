/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.stock.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMockBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 *
 */
@UnitTest
public class DefaultSapOaaCommerceStockServiceTest
{
	private static final String CART_GUID = "7ade6a19-e073-49c5-8082-1af99c0873c6";
	private static final String ARTICLE_1 = "article1";
	private static final String ARTICLE_2 = "article2";
	private static final String STORE_1 = "store1";
	private static final String STORE_2 = "store2";
	private static final Date ATP_DATE = new Date();
	private static final Double ATP_QTY_1 = new Double(300);

	private DefaultSapOaaCommerceStockService classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSapOaaCommerceStockService();
		classUnderTest.setStockLevelStatusStrategy(new DefaultSapOaaStockLevelStatusStrategy());

	}

	@Test
	public void getAvailableStockLevelTest()
	{

		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProduct(EasyMock.anyObject(String.class),
								EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class))).andReturn(availabilityList)
				.anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);
		Assert.assertNotNull(classUnderTest.getAtpService());

		//Mock ATPStrategy
		final ATPAggregationStrategy atpStrategyMock = EasyMock.createNiceMock(ATPAggregationStrategy.class);
		EasyMock
				.expect(
						atpStrategyMock.aggregateAvailability(EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(PointOfServiceModel.class), EasyMock.anyObject(List.class)))
				.andReturn(Long.valueOf(300)).anyTimes();
		EasyMock.replay(atpStrategyMock);
		classUnderTest.setAtpAggregationStrategy(atpStrategyMock);
		Assert.assertNotNull(classUnderTest.getAtpAggregationStrategy());

		final List<CartEntryModel> cartEntryModel = new ArrayList<>();
		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntryModel.add(cartEntry);


		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		EasyMock
				.expect(
						cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class),
								EasyMock.anyObject(ProductModel.class))).andReturn(cartEntryModel).anyTimes();
		EasyMock.replay(cartServiceMock);


		final CartModel cartModel = new CartModel();

		final Long actualStockAvailability = classUnderTest.getAvailableStockLevel(cartModel.getGuid(), "4711", new ProductModel(),
				null);

		Assert.assertEquals(ATP_QTY_1.longValue(), actualStockAvailability.longValue());
	}

	@Test
	public void getStockLevelForProductAndPointOfServiceOfflineTest()
	{
		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setName("test");

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		atpServiceMock.callRestAvailabilityServiceForProductAndSource(EasyMock.anyObject(ProductModel.class),
				EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();
		EasyMock.replay(atpServiceMock);

		classUnderTest.setAtpService(atpServiceMock);

		Assert.assertEquals(0, classUnderTest.getStockLevelForProductAndPointOfService(new ProductModel(), pos).longValue());
	}

	@Test
	public void getPosAndStockLevelStatusForProductOfflineTest()
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setName("test");
		pos.setType(PointOfServiceTypeEnum.STORE);

		final List<PointOfServiceModel> posList = new ArrayList();
		posList.add(pos);

		baseStore.setPointsOfService(posList);

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		atpServiceMock.callRestAvailabilityServiceForProductAndSources(EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(List.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();
		EasyMock.replay(atpServiceMock);

		classUnderTest.setAtpService(atpServiceMock);

		Assert.assertEquals(0, classUnderTest.getPosAndStockLevelStatusForProduct(new ProductModel(), baseStore).size());
	}


	@Test
	public void getAvailableStockLevelNoATPServiceAvailableTest()
	{
		final long qtyToAdd = 1000000;

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock.expect(
				atpServiceMock.callRestAvailabilityServiceForProduct(EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class))).andThrow(new ATPException());
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		// if the ATP Service is unavailable we expect requested value = stock value
		final CartModel cartModel = new CartModel();
		boolean reached = false;
		try
		{
			final Long actualStockAvailability = classUnderTest.getAvailableStockLevel(cartModel.getGuid(), "4711",
					new ProductModel(), null);
			Assert.assertEquals(qtyToAdd, actualStockAvailability.longValue());
		}
		catch (final ATPException e)
		{
			reached = true;
		}
		Assert.assertTrue(reached);

	}

	@Test
	public void getAvailableStockForPointOfServiceNoATPServiceAvailableTest()
	{
		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock.expect(
				atpServiceMock.callRestAvailabilityServiceForProductAndSource(EasyMock.anyObject(ProductModel.class),
						EasyMock.anyObject(String.class))).andThrow(new ATPException());
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		//Mock ATPStrategy
		final ATPAggregationStrategy atpStrategyMock = EasyMock.createNiceMock(ATPAggregationStrategy.class);
		EasyMock
				.expect(
						atpStrategyMock.aggregateAvailability(EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(PointOfServiceModel.class), EasyMock.anyObject(List.class)))
				.andReturn(Long.valueOf(0)).anyTimes();
		EasyMock.replay(atpStrategyMock);
		classUnderTest.setAtpAggregationStrategy(atpStrategyMock);

		final Long stock = classUnderTest.getStockLevelForProductAndPointOfService(new ProductModel(), new PointOfServiceModel());

		Assert.assertEquals(0, stock.longValue());
	}

	@Test
	public void getAvailableStockForPointOfServiceTest()
	{
		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProductAndSource(EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(String.class))).andReturn(availabilityList).anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);


		//Mock ATPStrategy
		final ATPAggregationStrategy atpStrategyMock = EasyMock.createNiceMock(ATPAggregationStrategy.class);
		EasyMock
				.expect(
						atpStrategyMock.aggregateAvailability(EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(PointOfServiceModel.class), EasyMock.anyObject(List.class)))
				.andReturn(Long.valueOf(0)).anyTimes();
		EasyMock.replay(atpStrategyMock);
		classUnderTest.setAtpAggregationStrategy(atpStrategyMock);

		final Long actualStockAvailability = classUnderTest.getStockLevelForProductAndPointOfService(new ProductModel(),
				new PointOfServiceModel());

		Assert.assertEquals(0, actualStockAvailability.longValue());

	}

	@Test
	public void getAvailabilityForProductTest()
	{
		final ProductModel product = new ProductModel();
		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProduct(EasyMock.anyObject(String.class),
								EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class))).andReturn(availabilityList)
				.anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		final List<ATPAvailability> atpResultList = classUnderTest.getAvailabilityForProduct(CART_GUID, "4711", product);
		Assert.assertNotNull(atpResultList);
		Assert.assertEquals(availabilityList, atpResultList);
	}

	@Test
	public void getAvailabilityForProductAndSourcTest()
	{

		final ProductModel product = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = createStore();

		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProductAndSource(EasyMock.anyObject(String.class),
								EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(String.class))).andReturn(availabilityList).anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		final List<ATPAvailability> atpResultList = classUnderTest.getAvailabilityForProductAndSource(CART_GUID, "4711", product,
				pointOfServiceModel.getName());
		Assert.assertNotNull(atpResultList);
		Assert.assertEquals(availabilityList, atpResultList);
	}


	@Test
	public void getAvailabilityForProductsTest()
	{
		final List<ProductModel> productList = new ArrayList<>();
		final ProductModel product1 = new ProductModel();
		product1.setCode(ARTICLE_1);
		final ProductModel product2 = new ProductModel();
		product1.setCode(ARTICLE_2);
		productList.add(product1);
		productList.add(product2);

		final String productUnit = "PCE";

		final List<ATPProductAvailability> atpProductAvailabilityList = new ArrayList<>();

		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		final ATPProductAvailability atpProductAvailability1 = new ATPProductAvailability();
		atpProductAvailability1.setAvailabilityList(availabilityList);
		atpProductAvailability1.setArticleId(ARTICLE_1);
		atpProductAvailabilityList.add(atpProductAvailability1);

		final ATPProductAvailability atpProductAvailability2 = new ATPProductAvailability();
		atpProductAvailability2.setAvailabilityList(availabilityList);
		atpProductAvailability2.setArticleId(ARTICLE_2);
		atpProductAvailabilityList.add(atpProductAvailability2);

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProducts(EasyMock.anyObject(String.class),
								EasyMock.anyObject(List.class), EasyMock.anyObject(String.class), EasyMock.anyObject(List.class)))
				.andReturn(atpProductAvailabilityList).anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		final List<ATPProductAvailability> atpResultList = classUnderTest.getAvailabilityForProducts(CART_GUID, "4711",
				productUnit, productList);
		Assert.assertNotNull(atpResultList);
		Assert.assertEquals(ARTICLE_1, atpResultList.get(0).getArticleId());
		Assert.assertEquals(availabilityList, atpResultList.get(0).getAvailabilityList());
		Assert.assertEquals(ARTICLE_2, atpResultList.get(1).getArticleId());
		Assert.assertEquals(availabilityList, atpResultList.get(1).getAvailabilityList());
	}


	@Test
	public void getAvailabilityForProductAndSourcesTest()
	{
		final List<String> sourcesList = new ArrayList<>();
		sourcesList.add(STORE_1);
		sourcesList.add(STORE_2);

		final ProductModel product = new ProductModel();
		product.setCode(ARTICLE_1);

		final List<ATPProductAvailability> atpProductAvailabilityList = new ArrayList<>();

		final List<ATPAvailability> availabilityList = getDefaultATPAvailabilityList();

		final ATPProductAvailability atpProductAvailability1 = new ATPProductAvailability();
		atpProductAvailability1.setAvailabilityList(availabilityList);
		atpProductAvailability1.setArticleId(ARTICLE_1);
		atpProductAvailability1.setSourceId(STORE_1);
		atpProductAvailabilityList.add(atpProductAvailability1);

		final ATPProductAvailability atpProductAvailability2 = new ATPProductAvailability();
		atpProductAvailability2.setAvailabilityList(availabilityList);
		atpProductAvailability2.setArticleId(ARTICLE_1);
		atpProductAvailability2.setSourceId(STORE_2);
		atpProductAvailabilityList.add(atpProductAvailability2);

		// Mock ATPService
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock
				.expect(
						atpServiceMock.callRestAvailabilityServiceForProductAndSources(EasyMock.anyObject(String.class),
								EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(List.class)))
				.andReturn(atpProductAvailabilityList).anyTimes();
		EasyMock.replay(atpServiceMock);
		classUnderTest.setAtpService(atpServiceMock);

		final List<ATPProductAvailability> atpResultList = classUnderTest.getAvailabilityForProductAndSources(CART_GUID, "4711",
				product, sourcesList);
		Assert.assertNotNull(atpResultList);
		Assert.assertEquals(ARTICLE_1, atpResultList.get(0).getArticleId());
		Assert.assertEquals(STORE_1, atpResultList.get(0).getSourceId());
		Assert.assertEquals(availabilityList, atpResultList.get(0).getAvailabilityList());
		Assert.assertEquals(ARTICLE_1, atpResultList.get(1).getArticleId());
		Assert.assertEquals(STORE_2, atpResultList.get(1).getSourceId());
		Assert.assertEquals(availabilityList, atpResultList.get(1).getAvailabilityList());
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelStatusForProductAndBaseStoreTest_NoOfStock()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		final StockService stockServiceMock = EasyMock.createNiceMock(StockService.class);
		final Collection<StockLevelModel> stockCollection = new ArrayList<StockLevelModel>();
		EasyMock
				.expect(stockServiceMock.getStockLevels(EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(Collection.class)))
				.andReturn(stockCollection).anyTimes();
		EasyMock.replay(stockServiceMock);

		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK,
				classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore));
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelStatusForProductAndBaseStoreTest_LowStock()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		final Collection<StockLevelModel> stockCollection = createStockCollection("Y", 150);
		final StockService stockServiceMock = createStockServiceMock(stockCollection);
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		Assert.assertEquals(StockLevelStatus.LOWSTOCK, classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore));
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelStatusForProductAndBaseStoreTest_InStock()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		final Collection<StockLevelModel> stockCollection = createStockCollection("G", 150);
		final StockService stockServiceMock = createStockServiceMock(stockCollection);
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		Assert.assertEquals(StockLevelStatus.INSTOCK, classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore));

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelStatusForProductAndBaseStoreTest_OutOftock()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		final Collection<StockLevelModel> stockCollection = createStockCollection("R", 150);
		final StockService stockServiceMock = createStockServiceMock(stockCollection);
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK,
				classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore));
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelForProductAndBaseStoreTest_Null()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		// create partial mock to mock the internal method calls
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaCommerceStockService.class)
				.addMockedMethod("getStockLevelStatusForProductAndBaseStore").createMock();

		EasyMock.expect(classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore))
				.andReturn(StockLevelStatus.INSTOCK).anyTimes();
		EasyMock.replay(classUnderTest);


		Assert.assertNull(classUnderTest.getStockLevelForProductAndBaseStore(product, baseStore));

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelForProductAndBaseStoreTest_OutOfStock()
	{
		final BaseStoreModel baseStore = null;
		final ProductModel product = null;

		// create partial mock to mock the internal method calls
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaCommerceStockService.class)
				.addMockedMethod("getStockLevelStatusForProductAndBaseStore").createMock();

		EasyMock.expect(classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore))
				.andReturn(StockLevelStatus.OUTOFSTOCK).anyTimes();
		EasyMock.replay(classUnderTest);


		Assert.assertEquals(classUnderTest.getStockLevelForProductAndBaseStore(product, baseStore).longValue(), 0);

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelForProductAndBaseStoreTest_LowStock()
	{
		final BaseStoreModel baseStore = null;

		final ProductModel product = null;

		// create partial mock to mock the internal method calls
		final IMockBuilder<DefaultSapOaaCommerceStockService> mockBuilder = EasyMock
				.createMockBuilder(DefaultSapOaaCommerceStockService.class);
		mockBuilder.addMockedMethod("getStockLevelStatusForProductAndBaseStore");
		classUnderTest = mockBuilder.createMock();

		EasyMock.expect(classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore))
				.andReturn(StockLevelStatus.LOWSTOCK).anyTimes();




		final Collection<StockLevelModel> stockCollection = createStockCollection("Y", 5);
		final StockService stockServiceMock = createStockServiceMock(stockCollection);
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		EasyMock.replay(classUnderTest);

		Assert.assertEquals(classUnderTest.getStockLevelForProductAndBaseStore(product, baseStore).longValue(), 5);

	}


	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getStockLevelForProductAndBaseStoreTest_NoStockLevel()
	{


		final BaseStoreModel baseStore = null;

		final ProductModel product = null;

		// create partial mock to mock the internal method calls
		final IMockBuilder<DefaultSapOaaCommerceStockService> mockBuilder = EasyMock
				.createMockBuilder(DefaultSapOaaCommerceStockService.class);
		mockBuilder.addMockedMethod("getStockLevelStatusForProductAndBaseStore");
		classUnderTest = mockBuilder.createMock();

		EasyMock.expect(classUnderTest.getStockLevelStatusForProductAndBaseStore(product, baseStore))
				.andReturn(StockLevelStatus.LOWSTOCK).anyTimes();




		final Collection<StockLevelModel> stockCollection = createEmptyStockCollection();
		final StockService stockServiceMock = createStockServiceMock(stockCollection);
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = createWarehouseSelectionStrategyMock(baseStore);

		classUnderTest.setWarehouseSelectionStrategy(warehouseSelectionStrategyMock);
		classUnderTest.setStockService(stockServiceMock);

		EasyMock.replay(classUnderTest);

		Assert.assertEquals(classUnderTest.getStockLevelForProductAndBaseStore(product, baseStore).longValue(), 0);

	}


	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getPosAndStockLevelStatusForProductTest_InStock()
	{

		final ProductModel product = null;



		final PointOfServiceModel pointOfService = createStore();
		final List<PointOfServiceModel> pointsOfService = new ArrayList<PointOfServiceModel>();
		pointsOfService.add(pointOfService);


		final BaseStoreModel baseStoreMock = createBaseStoreMock(pointsOfService);


		final PointOfServiceDao pointOfServiceDaoMock = createPointOfServiceDaoMock(pointOfService);
		classUnderTest.setPointOfServiceDao(pointOfServiceDaoMock);
		Assert.assertNotNull(classUnderTest.getPointOfServiceDao());

		//Mock ATPStrategy
		final ATPAggregationStrategy atpStrategyMock = EasyMock.createNiceMock(ATPAggregationStrategy.class);
		EasyMock
				.expect(
						atpStrategyMock.aggregateAvailability(EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(PointOfServiceModel.class), EasyMock.anyObject(List.class)))
				.andReturn(Long.valueOf(50)).anyTimes();
		EasyMock.replay(atpStrategyMock);
		classUnderTest.setAtpAggregationStrategy(atpStrategyMock);


		final List<ATPProductAvailability> atpProductAvailabilityList = createAtpProductAvailabilityList(new Double(50));

		final ATPService atpServiceMock = createAtpServiceMock(atpProductAvailabilityList);
		classUnderTest.setAtpService(atpServiceMock);

		final Map<PointOfServiceModel, StockLevelStatus> posAndStockLevelStatusForProduct = classUnderTest
				.getPosAndStockLevelStatusForProduct(product, baseStoreMock);
		Assert.assertNotNull(posAndStockLevelStatusForProduct);
		Assert.assertEquals(posAndStockLevelStatusForProduct.get(pointOfService), StockLevelStatus.INSTOCK);

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getPosAndStockLevelStatusForProductTest_OutOfStock()
	{

		final ProductModel product = null;

		final PointOfServiceModel pointOfService = createStore();
		final List<PointOfServiceModel> pointsOfService = new ArrayList<PointOfServiceModel>();
		pointsOfService.add(pointOfService);


		final BaseStoreModel baseStoreMock = createBaseStoreMock(pointsOfService);


		final PointOfServiceDao pointOfServiceDaoMock = createPointOfServiceDaoMock(pointOfService);
		classUnderTest.setPointOfServiceDao(pointOfServiceDaoMock);


		final List<ATPProductAvailability> atpProductAvailabilityList = createAtpProductAvailabilityList(new Double(0));

		final ATPService atpServiceMock = createAtpServiceMock(atpProductAvailabilityList);
		classUnderTest.setAtpService(atpServiceMock);

		//Mock ATPStrategy
		final ATPAggregationStrategy atpStrategyMock = EasyMock.createNiceMock(ATPAggregationStrategy.class);
		EasyMock
				.expect(
						atpStrategyMock.aggregateAvailability(EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class),
								EasyMock.anyObject(PointOfServiceModel.class), EasyMock.anyObject(List.class)))
				.andReturn(Long.valueOf(0)).anyTimes();
		EasyMock.replay(atpStrategyMock);
		classUnderTest.setAtpAggregationStrategy(atpStrategyMock);

		final Map<PointOfServiceModel, StockLevelStatus> posAndStockLevelStatusForProduct = classUnderTest
				.getPosAndStockLevelStatusForProduct(product, baseStoreMock);
		Assert.assertNotNull(posAndStockLevelStatusForProduct);
		Assert.assertEquals(posAndStockLevelStatusForProduct.get(pointOfService), StockLevelStatus.OUTOFSTOCK);

	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void getPosAndStockLevelStatusForProductTest_ATPException()
	{

		final ProductModel product = null;

		final PointOfServiceModel pointOfService = createStore();
		final List<PointOfServiceModel> pointsOfService = new ArrayList<PointOfServiceModel>();
		pointsOfService.add(pointOfService);


		final BaseStoreModel baseStoreMock = createBaseStoreMock(pointsOfService);


		final PointOfServiceDao pointOfServiceDaoMock = createPointOfServiceDaoMock(pointOfService);
		classUnderTest.setPointOfServiceDao(pointOfServiceDaoMock);

		final List<ATPProductAvailability> atpProductAvailabilityList = createAtpProductAvailabilityList(new Double(0));

		final ATPService atpServiceMock = createAtpServiceMockException(atpProductAvailabilityList);
		classUnderTest.setAtpService(atpServiceMock);


		final Map<PointOfServiceModel, StockLevelStatus> posAndStockLevelStatusForProduct = classUnderTest
				.getPosAndStockLevelStatusForProduct(product, baseStoreMock);
		Assert.assertNotNull(posAndStockLevelStatusForProduct);
		Assert.assertTrue(posAndStockLevelStatusForProduct.isEmpty());

	}



	/**
	 * @param atpProductAvailabilityList
	 * @return atpServiceMock
	 */
	private ATPService createAtpServiceMockException(final List<ATPProductAvailability> atpProductAvailabilityList)
	{
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock.expect(
				atpServiceMock.callRestAvailabilityServiceForProductAndSources(EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(List.class)))
				.andThrow(new ATPException());
		EasyMock.replay(atpServiceMock);
		return atpServiceMock;
	}

	/**
	 * @param atpProductAvailabilityList
	 * @return atpServiceMock
	 */
	private ATPService createAtpServiceMock(final List<ATPProductAvailability> atpProductAvailabilityList)
	{
		final ATPService atpServiceMock = EasyMock.createNiceMock(ATPService.class);
		EasyMock.expect(
				atpServiceMock.callRestAvailabilityServiceForProductAndSources(EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(List.class)))
				.andReturn(atpProductAvailabilityList);
		EasyMock.replay(atpServiceMock);
		return atpServiceMock;
	}

	/**
	 * @param quantity
	 * @return atpProductAvailabilityList
	 */
	private List<ATPProductAvailability> createAtpProductAvailabilityList(final Double quantity)
	{
		final List<ATPProductAvailability> atpProductAvailabilityList = new ArrayList<ATPProductAvailability>();
		final ATPProductAvailability atpProductAvailability = new ATPProductAvailability();
		atpProductAvailability.setArticleId("Article1");
		atpProductAvailability.setSourceId("OAS1");
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability availability = new ATPAvailability();
		availability.setQuantity(quantity);
		availabilityList.add(availability);
		atpProductAvailability.setAvailabilityList(availabilityList);
		atpProductAvailabilityList.add(atpProductAvailability);
		return atpProductAvailabilityList;
	}

	/**
	 * @param pointOfService
	 * @return pointOfServiceDaoMock
	 */
	private PointOfServiceDao createPointOfServiceDaoMock(final PointOfServiceModel pointOfService)
	{
		final PointOfServiceDao pointOfServiceDaoMock = EasyMock.createNiceMock(PointOfServiceDao.class);
		EasyMock.expect(pointOfServiceDaoMock.getPosByName(EasyMock.anyObject(String.class))).andReturn(pointOfService);
		EasyMock.replay(pointOfServiceDaoMock);
		return pointOfServiceDaoMock;
	}

	/**
	 * @param pointsOfService
	 * @return baseStoreMock
	 */
	private BaseStoreModel createBaseStoreMock(final List<PointOfServiceModel> pointsOfService)
	{
		final BaseStoreModel baseStoreMock = EasyMock.createNiceMock(BaseStoreModel.class);
		EasyMock.expect(baseStoreMock.getPointsOfService()).andReturn(pointsOfService).anyTimes();
		EasyMock.replay(baseStoreMock);
		return baseStoreMock;
	}

	/**
	 * @return store
	 */
	private PointOfServiceModel createStore()
	{
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setName(STORE_1);
		pointOfService.setType(PointOfServiceTypeEnum.STORE);
		return pointOfService;
	}

	private List<ATPAvailability> getDefaultATPAvailabilityList()
	{
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		availabilityList.add(getDefaultATPAvailability_1());
		return availabilityList;
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	private ATPAvailability getDefaultATPAvailability_1()
	{
		final ATPAvailability availModel = new ATPAvailability();
		availModel.setAtpDate(ATP_DATE);
		availModel.setQuantity(ATP_QTY_1);
		return availModel;
	}

	/**
	 * @param roughStockStatus
	 * @param availableQuantity
	 * @return stockCollection
	 */
	private Collection<StockLevelModel> createStockCollection(final String roughStockStatus, final int availableQuantity)
	{
		final Collection<StockLevelModel> stockCollection = new ArrayList<StockLevelModel>();
		final StockLevelModel stockLevel = new StockLevelModel();
		stockLevel.setAvailable(availableQuantity);
		stockLevel.setSapoaa_roughStockIndicator(roughStockStatus);
		stockCollection.add(stockLevel);
		return stockCollection;
	}


	/**
	 * @return stockCollection
	 */
	private Collection<StockLevelModel> createEmptyStockCollection()
	{
		final Collection<StockLevelModel> stockCollection = new ArrayList<StockLevelModel>();
		return stockCollection;
	}


	/**
	 * @param baseStore
	 * @return warehouseSelectionStrategyMock
	 */
	private WarehouseSelectionStrategy createWarehouseSelectionStrategyMock(final BaseStoreModel baseStore)
	{
		final WarehouseSelectionStrategy warehouseSelectionStrategyMock = EasyMock.createNiceMock(WarehouseSelectionStrategy.class);
		EasyMock.expect(warehouseSelectionStrategyMock.getWarehousesForBaseStore(baseStore)).andReturn(
				new ArrayList<WarehouseModel>());
		EasyMock.replay(warehouseSelectionStrategyMock);
		return warehouseSelectionStrategyMock;
	}

	/**
	 * @param stockCollection
	 * @return stockServiceMock
	 */
	private StockService createStockServiceMock(final Collection<StockLevelModel> stockCollection)
	{
		final StockService stockServiceMock = EasyMock.createNiceMock(StockService.class);
		EasyMock
				.expect(stockServiceMock.getStockLevels(EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(Collection.class)))
				.andReturn(stockCollection).anyTimes();
		EasyMock.replay(stockServiceMock);
		return stockServiceMock;
	}
}

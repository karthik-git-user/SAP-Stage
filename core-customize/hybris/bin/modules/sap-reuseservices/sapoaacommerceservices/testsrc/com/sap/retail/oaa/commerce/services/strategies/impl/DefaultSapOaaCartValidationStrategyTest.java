/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.strategies.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.atp.strategy.impl.DefaultATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 *
 */
@UnitTest
public class DefaultSapOaaCartValidationStrategyTest
{
	private static final String CART_GUID = "7ade6a19-e073-49c5-8082-1af99c0873c6";
	private static final String CART_ID = "et5qGeBzScWAghr5nAhzxg==";
	private static final String CART_ITEM_ID_1 = "000000000000000123";
	private static final String CART_ITEM_ID_2 = "000000000000000124";
	private static final Integer CART_ITEM_ENTRY_NO_1 = Integer.valueOf(0);
	private static final Integer CART_ITEM_ENTRY_NO_2 = Integer.valueOf(1);
	private static final Long CART_ITEM_QTY_1 = Long.valueOf(10);
	private static final Long CART_ITEM_QTY_2 = Long.valueOf(10);
	private static final String SOURCE_STORE = "sourceStore1";
	private static final String UNIT_PCE = "PC";


	private DefaultSapOaaCartValidationStrategy classUnderTest = null;
	private ATPAggregationStrategy aggregationStrategy;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSapOaaCartValidationStrategy();

		// Mocking the Model Service
		final ModelService modelServiceMock = EasyMock.createNiceMock(ModelService.class);
		modelServiceMock.remove(EasyMock.anyObject(CartEntryModel.class));
		EasyMock.expectLastCall().anyTimes();
		modelServiceMock.refresh(EasyMock.anyObject(CartModel.class));
		EasyMock.expectLastCall().anyTimes();
		EasyMock.replay(modelServiceMock);
		classUnderTest.setModelService(modelServiceMock);

		aggregationStrategy = new DefaultATPAggregationStrategy();
		classUnderTest.setAtpAggregationStrategy(aggregationStrategy);
	}

	@Test
	public void validateCartTest()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();

		// Cart
		final CartModel cartModel = getDefaultCartModel();
		parameter.setCart(cartModel);

		// expected result
		final List<CommerceCartModification> expCartModificationList = new ArrayList<>();
		expCartModificationList.add(new CommerceCartModification());

		// create partial mock to mock the internal method calls
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaCartValidationStrategy.class).addMockedMethod("cleanCart")
				.addMockedMethod("checkAvailabilityOfCartItems").createMock();
		classUnderTest.cleanCart(cartModel);
		EasyMock.expectLastCall();
		EasyMock.expect(classUnderTest.checkAvailabilityOfCartItems(EasyMock.anyObject(CartModel.class)))
				.andReturn(expCartModificationList).anyTimes();
		EasyMock.replay(classUnderTest);

		final List<CommerceCartModification> actCartModificationList = classUnderTest.validateCart(parameter);

		Assert.assertNotNull(actCartModificationList);
		Assert.assertEquals(expCartModificationList, actCartModificationList);
	}

	@Test
	public void validateCartSourcingFailsTest()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();

		// Cart
		final CartModel cartModel = getDefaultCartModel();
		cartModel.setDeliveryAddress(new AddressModel());
		parameter.setCart(cartModel);

		// create partial mock to mock the internal method calls
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaCartValidationStrategy.class).addMockedMethod("cleanCart")
				.addMockedMethod("checkAvailabilityOfCartItems").createMock();

		classUnderTest.cleanCart(cartModel);
		EasyMock.expectLastCall();
		EasyMock.expect(classUnderTest.checkAvailabilityOfCartItems(EasyMock.anyObject(CartModel.class)))
				.andReturn(new ArrayList<CommerceCartModification>());

		EasyMock.replay(classUnderTest);

		final List<CommerceCartModification> actModificationList = classUnderTest.validateCart(parameter);

		Assert.assertNotNull(actModificationList);
		Assert.assertEquals(0, actModificationList.size());
	}

	@Test
	public void validateCartBackendDownCASTest()
	{
		// Cart Item
		final CartEntryModel item1 = getDefaultCartEntryModel_1();

		final List<AbstractOrderEntryModel> entriesList = new ArrayList<>();
		entriesList.add(item1);

		final List<CartEntryModel> entriesList2 = new ArrayList<>();
		entriesList2.add(item1);

		// Cart
		final CartModel cartModel = getDefaultCartModel();
		cartModel.setDeliveryAddress(new AddressModel());
		cartModel.setEntries(entriesList);

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);

		//Mock Call for Click and Ship and throw BackendDown Exception
		oaaStockServiceMock.getAvailabilityForProduct(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(ProductModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();

		//Mock Call for Click and Collect and throw BackendDown Exception
		oaaStockServiceMock.getAvailabilityForProductAndSource(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();

		EasyMock.replay(oaaStockServiceMock);

		//Mock Product Service
		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		productServiceMock.getProductForCode(EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(new ProductModel());
		EasyMock.replay(productServiceMock);

		//Mock Cart Service
		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class), EasyMock.anyObject(ProductModel.class));
		EasyMock.expectLastCall().andReturn(entriesList2).anyTimes();
		EasyMock.replay(cartServiceMock);

		classUnderTest.setCartService(cartServiceMock);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		final List<CommerceCartModification> actModificationList = classUnderTest.checkAvailabilityOfCartItems(cartModel);

		Assert.assertNotNull(actModificationList);
		Assert.assertEquals(1, actModificationList.size());
		final CommerceCartModification modification = actModificationList.get(0);
		Assert.assertEquals(item1.getQuantity().longValue(), modification.getQuantityAdded());
	}

	@Test
	public void validateCartBackendDownCACTest()
	{
		// Cart Item
		final CartEntryModel item1 = getDefaultCartEntryModelWithPointOfService_1();

		final List<AbstractOrderEntryModel> entriesList = new ArrayList<>();
		entriesList.add(item1);

		final List<CartEntryModel> entriesList2 = new ArrayList<>();
		entriesList2.add(item1);

		// Cart
		final CartModel cartModel = getDefaultCartModel();
		cartModel.setDeliveryAddress(new AddressModel());
		cartModel.setEntries(entriesList);

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);

		//Mock Call for Click and Ship and throw BackendDown Exception
		oaaStockServiceMock.getAvailabilityForProduct(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(ProductModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();

		//Mock Call for Click and Collect and throw BackendDown Exception
		oaaStockServiceMock.getAvailabilityForProductAndSource(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException()).anyTimes();

		EasyMock.replay(oaaStockServiceMock);

		//Mock Product Service
		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		productServiceMock.getProductForCode(EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(new ProductModel());
		EasyMock.replay(productServiceMock);

		//Mock Cart Service
		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class), EasyMock.anyObject(ProductModel.class));
		EasyMock.expectLastCall().andReturn(entriesList2).anyTimes();
		EasyMock.replay(cartServiceMock);

		classUnderTest.setCartService(cartServiceMock);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		final List<CommerceCartModification> actModificationList = classUnderTest.checkAvailabilityOfCartItems(cartModel);

		Assert.assertNotNull(actModificationList);
		Assert.assertEquals(1, actModificationList.size());
		final CommerceCartModification modification = actModificationList.get(0);
		//We expect, that there is no quantity available
		Assert.assertEquals(0, modification.getQuantityAdded());
	}

	@Test
	public void validateCartEmptyListTest()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		parameter.setCart(cartModel);

		final List<CommerceCartModification> actModificationList = classUnderTest.validateCart(parameter);

		Assert.assertNotNull(actModificationList);
		Assert.assertEquals(0, actModificationList.size());
	}

	@Test
	public void validateCartEntryNoProductServiceTest()
	{
		final CartModel cartModel = getDefaultCartModel();

		final AbstractOrderEntryModel item1 = new CartEntryModel();
		final ProductModel productModel = new ProductModel();
		productModel.setCode(CART_ITEM_ID_1);
		item1.setProduct(productModel);

		final long aggregatedATPQty = 10;

		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		EasyMock.expect(productServiceMock.getProductForCode(EasyMock.anyObject(String.class)))
				.andThrow(new UnknownIdentifierException("Test Exception"));
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);


		final CommerceCartModification cartModification = classUnderTest.validateCartEntry(cartModel, item1, aggregatedATPQty);

		Assert.assertNotNull(cartModification);
		Assert.assertEquals(CommerceCartModificationStatus.UNAVAILABLE, cartModification.getStatusCode());
		Assert.assertEquals(0, cartModification.getQuantity());
		Assert.assertEquals(0, cartModification.getQuantityAdded());
	}

	@Test
	public void validateCartEntrySuccessTest()
	{
		// Cart
		final CartModel cartModel = getDefaultCartModel();

		// Cart Item
		final CartEntryModel item1 = getDefaultCartEntryModel_1();

		// ATP Result
		final List<ATPAvailability> atpAvailabilities = new ArrayList<>();

		final ATPAvailability atpAvailModel = new ATPAvailability();
		atpAvailModel.setAtpDate(new Date());
		atpAvailModel.setQuantity(new Double(CART_ITEM_QTY_1.doubleValue()));
		atpAvailabilities.add(atpAvailModel);

		final long aggregatedATPQty = CART_ITEM_QTY_1.longValue();

		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		EasyMock.expect(productServiceMock.getProductForCode(EasyMock.anyObject(String.class))).andReturn(new ProductModel());
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);

		final List<CartEntryModel> entriesList = new ArrayList<>();
		entriesList.add(item1);

		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		EasyMock.expect(
				cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class), EasyMock.anyObject(ProductModel.class)))
				.andReturn(entriesList).anyTimes();
		EasyMock.replay(cartServiceMock);
		classUnderTest.setCartService(cartServiceMock);

		final CommerceCartModification cartModification = classUnderTest.validateCartEntry(cartModel, item1, aggregatedATPQty);

		Assert.assertNotNull(cartModification);
		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, cartModification.getStatusCode());
		Assert.assertEquals(CART_ITEM_QTY_1.longValue(), cartModification.getQuantity());
		Assert.assertEquals(CART_ITEM_QTY_1.longValue(), cartModification.getQuantityAdded());
		assertEqualsAbstractOrderEntryModel(item1, cartModification.getEntry());
	}

	@Test
	public void validateCartEntryLowStockTest()
	{
		final Double ATP_QUANTITY_ITEM_1 = new Double("5");

		// Cart
		final CartModel cartModel = getDefaultCartModel();

		// Cart Item
		final CartEntryModel item1 = getDefaultCartEntryModel_1();

		// ATP Result
		final List<ATPAvailability> atpAvailabilities = new ArrayList<>();

		final ATPAvailability atpAvailModel = new ATPAvailability();
		atpAvailModel.setAtpDate(new Date());
		atpAvailModel.setQuantity(ATP_QUANTITY_ITEM_1);
		atpAvailabilities.add(atpAvailModel);

		final long aggregatedATPQty = ATP_QUANTITY_ITEM_1.longValue();

		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		EasyMock.expect(productServiceMock.getProductForCode(EasyMock.anyObject(String.class))).andReturn(new ProductModel());
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);

		final List<CartEntryModel> entriesList = new ArrayList<>();
		entriesList.add(item1);

		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		EasyMock.expect(
				cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class), EasyMock.anyObject(ProductModel.class)))
				.andReturn(entriesList).anyTimes();
		EasyMock.replay(cartServiceMock);
		classUnderTest.setCartService(cartServiceMock);

		final CommerceCartModification cartModification = classUnderTest.validateCartEntry(cartModel, item1, aggregatedATPQty);

		Assert.assertNotNull(cartModification);
		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, cartModification.getStatusCode());
		Assert.assertEquals(CART_ITEM_QTY_1.longValue(), cartModification.getQuantity());
		Assert.assertEquals(ATP_QUANTITY_ITEM_1.longValue(), cartModification.getQuantityAdded());
		assertEqualsAbstractOrderEntryModel(item1, cartModification.getEntry());
	}


	@Test
	public void validateCartEntryNoStockTest()
	{
		// Cart
		final CartModel cartModel = getDefaultCartModel();

		// Cart Item
		final CartEntryModel item1 = getDefaultCartEntryModel_1();

		final long aggregatedATPQty = 0;

		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		EasyMock.expect(productServiceMock.getProductForCode(EasyMock.anyObject(String.class))).andReturn(new ProductModel());
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);

		final List<CartEntryModel> entriesList = new ArrayList<>();
		entriesList.add(item1);

		final CartService cartServiceMock = EasyMock.createNiceMock(CartService.class);
		EasyMock.expect(
				cartServiceMock.getEntriesForProduct(EasyMock.anyObject(CartModel.class), EasyMock.anyObject(ProductModel.class)))
				.andReturn(entriesList).anyTimes();
		EasyMock.replay(cartServiceMock);
		classUnderTest.setCartService(cartServiceMock);

		final CommerceCartModification cartModification = classUnderTest.validateCartEntry(cartModel, item1, aggregatedATPQty);

		Assert.assertNotNull(cartModification);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, cartModification.getStatusCode());
		Assert.assertEquals(CART_ITEM_QTY_1.longValue(), cartModification.getQuantity());
		Assert.assertEquals(0, cartModification.getQuantityAdded());
	}

	@Test
	public void checkAvailabilityOfCartItemsShipToAndPOSNoStockTest()
	{
		// Cart Model
		final CartModel cart = getDefaultCartModel();
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		cart.getEntries().get(0).getProduct().setUnit(unitModel);
		cart.getEntries().get(1).getProduct().setUnit(unitModel);

		// set 2 more entries with a Point of Service
		cart.getEntries().add(getDefaultCartEntryModelWithPointOfService_1());
		cart.getEntries().get(2).getProduct().setUnit(unitModel);
		cart.getEntries().add(getDefaultCartEntryModelWithPointOfService_2());
		cart.getEntries().get(3).getProduct().setUnit(unitModel);

		// Product lists
		final List<ProductModel> shipToProductList = new ArrayList<>();
		shipToProductList.add(cart.getEntries().get(0).getProduct());
		shipToProductList.add(cart.getEntries().get(1).getProduct());

		// ATP Availability lines
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability atpAvail = new ATPAvailability();
		atpAvail.setQuantity(new Double("0"));
		atpAvail.setAtpDate(new Date());
		availabilityList.add(atpAvail);

		// CommerceCartModification = NoStock available
		final CommerceCartModification noStockAvailableStatus = new CommerceCartModification();
		noStockAvailableStatus.setStatusCode(CommerceCartModificationStatus.NO_STOCK);

		// create partial mock to mock the internal method calls
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaCartValidationStrategy.class).addMockedMethod("validateCartEntry")
				.createMock();
		EasyMock
				.expect(classUnderTest.validateCartEntry(EasyMock.anyObject(CartModel.class),
						EasyMock.anyObject(AbstractOrderEntryModel.class), EasyMock.anyLong()))
				.andReturn(noStockAvailableStatus).anyTimes();

		classUnderTest.setAtpAggregationStrategy(aggregationStrategy);

		// Stock Service Mock
		final DefaultSapOaaCommerceStockService stockServiceMock = EasyMock.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock.expect(stockServiceMock.getAvailabilityForProduct(EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class))).andReturn(availabilityList).anyTimes();
		EasyMock
				.expect(stockServiceMock.getAvailabilityForProductAndSource(EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(String.class)))
				.andReturn(availabilityList).anyTimes();

		EasyMock.replay(stockServiceMock);
		classUnderTest.setOaaStockService(stockServiceMock);
		EasyMock.replay(classUnderTest);

		final List<CommerceCartModification> modificationList = classUnderTest.checkAvailabilityOfCartItems(cart);
		Assert.assertNotNull(modificationList);
		Assert.assertFalse(modificationList.isEmpty());
		Assert.assertEquals(cart.getEntries().size(), modificationList.size());

		for (final CommerceCartModification modification : modificationList)
		{
			Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, modification.getStatusCode());
		}
	}

	@Test
	public void checkAvailabilityOfCartItemsExceptionTest()
	{

		// Cart Model
		final CartModel cart = getDefaultCartModel();
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		cart.getEntries().get(0).getProduct().setUnit(unitModel);
		cart.getEntries().get(1).getProduct().setUnit(unitModel);

		// set 2 more entries with a Point of Service
		cart.getEntries().add(getDefaultCartEntryModelWithPointOfService_1());
		cart.getEntries().get(2).getProduct().setUnit(unitModel);
		cart.getEntries().add(getDefaultCartEntryModelWithPointOfService_2());
		cart.getEntries().get(3).getProduct().setUnit(unitModel);

		// Stock Service Mock
		final DefaultSapOaaCommerceStockService stockServiceMock = EasyMock.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock.expect(stockServiceMock.getAvailabilityForProduct(EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class))).andThrow(new ATPException()).anyTimes();
		EasyMock
				.expect(stockServiceMock.getAvailabilityForProductAndSource(EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(String.class)))
				.andThrow(new ATPException()).anyTimes();
		EasyMock.replay(stockServiceMock);
		classUnderTest.setOaaStockService(stockServiceMock);

		final List<CommerceCartModification> modificationList = classUnderTest.checkAvailabilityOfCartItems(cart);
		Assert.assertNotNull(modificationList);
		Assert.assertNotEquals(cart.getEntries().size(), modificationList.size());

		for (final CommerceCartModification modification : modificationList)
		{
			Assert.assertNotEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		}
	}

	private void assertEqualsAbstractOrderEntryModel(final AbstractOrderEntryModel expected, final AbstractOrderEntryModel actual)
	{
		Assert.assertEquals(expected.getQuantity().longValue(), actual.getQuantity().longValue());
		Assert.assertEquals(expected.getEntryNumber().toString(), actual.getEntryNumber().toString());
		Assert.assertEquals(expected.getProduct().getCode(), actual.getProduct().getCode());
		Assert.assertEquals(expected.getUnit().getCode(), actual.getUnit().getCode());
	}

	private CartModel getDefaultCartModel()
	{
		// Cart
		final CartModel cartModel = new CartModel();
		final List<AbstractOrderEntryModel> cartEntries = new ArrayList<>();

		// Item 1
		final CartEntryModel item1 = getDefaultCartEntryModel_1();
		cartEntries.add(item1);

		// Item 2
		final CartEntryModel item2 = getDefaultCartEntryModel_2();
		cartEntries.add(item2);

		cartModel.setEntries(cartEntries);
		cartModel.setGuid(CART_GUID);
		cartModel.setCode(CART_ID);
		return cartModel;
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	private CartEntryModel getDefaultCartEntryModel_1()
	{
		// Item 1
		final CartEntryModel item1 = new CartEntryModel();
		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode(CART_ITEM_ID_1);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		item1.setEntryNumber(CART_ITEM_ENTRY_NO_1);
		item1.setProduct(productModel1);
		item1.setQuantity(CART_ITEM_QTY_1);
		item1.setUnit(unitModel);
		return item1;
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	private CartEntryModel getDefaultCartEntryModel_2()
	{
		// Item 2
		final CartEntryModel item2 = new CartEntryModel();
		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode(CART_ITEM_ID_2);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		item2.setEntryNumber(CART_ITEM_ENTRY_NO_2);
		item2.setProduct(productModel2);
		item2.setQuantity(CART_ITEM_QTY_2);
		item2.setUnit(unitModel);
		return item2;
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	private CartEntryModel getDefaultCartEntryModelWithPointOfService_1()
	{
		// Item 1 with PointOfService
		final CartEntryModel item1 = getDefaultCartEntryModel_1();
		final PointOfServiceModel deliveryPointOfService = new PointOfServiceModel();
		deliveryPointOfService.setName(SOURCE_STORE);
		item1.setDeliveryPointOfService(deliveryPointOfService);
		return item1;
	}

	@SuppressWarnings("PMD.MethodNamingConventions")
	private CartEntryModel getDefaultCartEntryModelWithPointOfService_2()
	{
		// Item 2 with PointOfService
		final CartEntryModel item2 = getDefaultCartEntryModel_2();
		final PointOfServiceModel deliveryPointOfService = new PointOfServiceModel();
		deliveryPointOfService.setName(SOURCE_STORE);
		item2.setDeliveryPointOfService(deliveryPointOfService);
		return item2;
	}
}

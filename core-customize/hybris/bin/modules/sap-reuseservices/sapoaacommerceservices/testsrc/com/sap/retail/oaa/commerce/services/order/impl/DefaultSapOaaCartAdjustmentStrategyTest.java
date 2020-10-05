/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;


/**
 *
 */
@UnitTest
public class DefaultSapOaaCartAdjustmentStrategyTest
{
	private static final String CART_GUID = "7ade6a19-e073-49c5-8082-1af99c0873c6";
	private static final String POS_NAME = "pointOfServiceName";
	private static final Double STOCK_LEVEL_QTY = new Double(10.0);

	private DefaultSapOaaCartAdjustmentStrategy classUnderTest = null;


	@Before
	public void setup()
	{
		classUnderTest = new DefaultSapOaaCartAdjustmentStrategy();
	}

	@Test
	public void determineAllowedCartAdjustmentForProductZeroTest()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = 10;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		final Long stockLevelQty = Long.valueOf("0");

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andReturn(stockLevelQty).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment without PointOfService set
		Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, null);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(0, allowedCartAdjustmentQty.longValue());

		// Check allowed cart adjustment with PointOfService set
		allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
				cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(0, allowedCartAdjustmentQty.longValue());
	}

	@Test
	public void determineAllowedCartAdjustmentForProductTest()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = 100;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		final Long stockLevelQty = Long.valueOf(STOCK_LEVEL_QTY.longValue());

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andReturn(stockLevelQty).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment without PointOfService set
		Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, null);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(STOCK_LEVEL_QTY.longValue(), allowedCartAdjustmentQty.longValue());

		// Check allowed cart adjustment with PointOfService set
		allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
				cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(STOCK_LEVEL_QTY.longValue(), allowedCartAdjustmentQty.longValue());
	}

	@Test
	public void determineAllowedCartAdjustmentClickAndCollectOffline()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = 100;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);

		final BackendDownException BackendDownException = EasyMock.createNiceMock(BackendDownException.class);

		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andThrow(BackendDownException).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment with PointOfService set
		final Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(0, allowedCartAdjustmentQty.longValue());
	}

	@Test
	public void determineAllowedCartAdjustmentClickAndShipOffline()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final long quantityToAdd = 100;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);

		final BackendDownException BackendDownException = EasyMock.createNiceMock(BackendDownException.class);

		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andThrow(BackendDownException).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment with PointOfService set
		final Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, null);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(quantityToAdd, allowedCartAdjustmentQty.longValue());
	}

	@Test
	public void determineAllowedCartAdjustmentForProductNoServiceAvailableTest()
	{
		// In case an ATPException occurs, the user can still add the requested quantity
		// because no reservation is needed at that time in process
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = 100;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andThrow(new ATPException()).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment without PointOfService set
		Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, null);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(0, allowedCartAdjustmentQty.longValue());

		// Check allowed cart adjustment with PointOfService set
		allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
				cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(0, allowedCartAdjustmentQty.longValue());
	}


	@Test
	public void determineAllowedCartAdjustmentForProductMaxOrderQuantityTest()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final Integer maxOrderQuantity = Integer.valueOf(10);
		productModel.setMaxOrderQuantity(maxOrderQuantity);
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = 100;
		final long cartItemQty = 0; // indicates how many articles are already in the cart

		final Long stockLevelQty = Long.valueOf(STOCK_LEVEL_QTY.longValue());

		// Mock defaultSapOaaStockService
		final DefaultSapOaaCommerceStockService oaaStockServiceMock = EasyMock
				.createNiceMock(DefaultSapOaaCommerceStockService.class);
		EasyMock
				.expect(oaaStockServiceMock.getAvailableStockLevel(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(ProductModel.class), EasyMock.anyObject(PointOfServiceModel.class)))
				.andReturn(stockLevelQty).anyTimes();
		EasyMock.replay(oaaStockServiceMock);
		classUnderTest.setOaaStockService(oaaStockServiceMock);

		// Check allowed cart adjustment
		final Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(maxOrderQuantity.longValue(), allowedCartAdjustmentQty.longValue());
	}

	@Test
	public void determineAllowedCartAdjustmentForProductAddingNegativeValueTest()
	{
		final CartModel cartModel = getDefaultCartModel();
		final ProductModel productModel = new ProductModel();
		final PointOfServiceModel pointOfServiceModel = getDefaultPointOfServiceModel();
		final long quantityToAdd = -10;
		final long cartItemQty = 10; // indicates how many articles are already in the cart

		// Check allowed cart adjustment without PointOfService set
		Long allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel,
				quantityToAdd, cartItemQty, null);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(quantityToAdd, allowedCartAdjustmentQty.longValue());

		// Check allowed cart adjustment with PointOfService set
		allowedCartAdjustmentQty = classUnderTest.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
				cartItemQty, pointOfServiceModel);
		Assert.assertNotNull(allowedCartAdjustmentQty);
		Assert.assertEquals(quantityToAdd, allowedCartAdjustmentQty.longValue());
	}

	// -- stubs and helper methods -- //
	private PointOfServiceModel getDefaultPointOfServiceModel()
	{
		final PointOfServiceModel pointOfServiceModel = new PointOfServiceModel();
		pointOfServiceModel.setName(POS_NAME);
		return pointOfServiceModel;
	}

	private CartModel getDefaultCartModel()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setGuid(CART_GUID);
		return cartModel;
	}
}

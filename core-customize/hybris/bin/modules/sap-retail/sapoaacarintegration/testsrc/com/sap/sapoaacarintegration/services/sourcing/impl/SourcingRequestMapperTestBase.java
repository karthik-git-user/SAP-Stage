/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request.CartItem;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request.CartItems;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.CartRequest;

/**
 *
 */
public class SourcingRequestMapperTestBase
{

	/**
	 *
	 */
	protected static final String DELIVERY_POINT_OF_SERVICE_NAME = "OAS1";
	protected static final String OAA_PROFILE = "OAA_PROF";
	protected static final String OAA_CONSUMER_ID = "OAA_CON";
	protected static final String CART_ID = "et5qGeBzScWAghr5nAhzxg==";
	protected static final String CART_GUID = "7ade6a19-e073-49c5-8082-1af99c0873c6";
	protected static final String CART_ITEM_ID_1 = "000000000000000123";
	protected static final String CART_ITEM_ID_2 = "000000000000000124";
	protected static final Integer CART_ITEM_1_ENTRY_NO = Integer.valueOf(1);
	protected static final Integer CART_ITEM_2_ENTRY_NO = Integer.valueOf(2);
	protected static final String UNIT_PCE = "ST";
	protected static final Long CART_ITEM_1_QTY = Long.valueOf(1);
	protected static final Long CART_ITEM_2_QTY = Long.valueOf(2);
	protected static final Double CART_TOTAL_PRICE = new Double(20);
	protected static final Double CART_DELIVERY_COST = new Double(4);
	protected static final String CART_CURRENCY = "EUR";
	protected static final Double CART_ITEM_1_TOTAL_PRICE = new Double(7);
	protected static final Double CART_ITEM_2_TOTAL_PRICE = new Double(9);
	protected static final String DELIVERY_MODE = "ANY_DELIVERY_MODE";
	protected static final String CONFIG_PROPERTY_SAP_DELIVERY_MODES = "sapDeliveryModes";
	protected CartModel getCart()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setGuid(CART_GUID);
		cartModel.setCode(CART_ID);
	
		cartModel.setTotalPrice(CART_TOTAL_PRICE);
		cartModel.setDeliveryCost(CART_DELIVERY_COST);
	
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(CART_CURRENCY);
		cartModel.setCurrency(currencyModel);
	
		final DeliveryModeModel deliveryModeModel = new DeliveryModeModel();
		deliveryModeModel.setCode(DELIVERY_MODE);
		cartModel.setDeliveryMode(deliveryModeModel);
	
		final CountryModel countryModel = new CountryModel();
		countryModel.setSapCode("TestSAPCode");
		final AddressModel addressModel = new AddressModel();
		addressModel.setCountry(countryModel);
		addressModel.setDistrict("TestDistrict");
		addressModel.setPobox("TestPobox");
		addressModel.setPostalcode("TestPobox");
		addressModel.setStreetname("TestStreetName");
		addressModel.setStreetnumber("TestStreetNumber");
		addressModel.setTown("TestTown");
		final RegionModel regionModel = new RegionModel();
		regionModel.setIsocodeShort(RegionModel.ISOCODESHORT);
		addressModel.setRegion(regionModel);
		cartModel.setDeliveryAddress(addressModel);
	
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
	
		final AbstractOrderEntryModel item1 = new AbstractOrderEntryModel();
		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode(CART_ITEM_ID_1);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		item1.setEntryNumber(CART_ITEM_1_ENTRY_NO);
		item1.setProduct(productModel1);
		item1.setQuantity(CART_ITEM_1_QTY);
		item1.setUnit(unitModel);
		item1.setTotalPrice(CART_ITEM_1_TOTAL_PRICE);
		final PointOfServiceModel deliveryPointOfService = new PointOfServiceModel();
		deliveryPointOfService.setName(DELIVERY_POINT_OF_SERVICE_NAME);
		item1.setDeliveryPointOfService(deliveryPointOfService);
		entries.add(item1);
	
		final AbstractOrderEntryModel item2 = new AbstractOrderEntryModel();
		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode(CART_ITEM_ID_2);
		item2.setEntryNumber(CART_ITEM_2_ENTRY_NO);
		item2.setProduct(productModel2);
		item2.setQuantity(CART_ITEM_2_QTY);
		item2.setUnit(unitModel);
		item2.setTotalPrice(CART_ITEM_2_TOTAL_PRICE);
		entries.add(item2);
	
		cartModel.setEntries(entries);
	
		return cartModel;
	}
	protected void assertCart(final CartRequest cartReq)
	{
		assertNotNullObjAndObjToString(cartReq);
	
		final String cartId = cartReq.getExternalId();
		Assert.assertEquals(CART_ID, cartId);
	
		Assert.assertEquals(CART_TOTAL_PRICE, cartReq.getTotalPrice());
		Assert.assertEquals(CART_DELIVERY_COST, cartReq.getDeliveryCost());
		Assert.assertEquals(CART_CURRENCY, cartReq.getCurrencyIsoCode());
	
		final CartItems cartItems = cartReq.getItems();
		assertNotNullObjAndObjToString(cartItems);
	
		final List<CartItem> cartItemList = cartItems.getItems();
		assertNotNullObjAndObjToString(cartItemList);
		Assert.assertEquals(2, cartItemList.size());
	
		final CartItem cartItem1 = cartItemList.get(0);
		Assert.assertEquals(CART_ITEM_ID_1, cartItem1.getArticleId());
		Assert.assertEquals(CART_ITEM_1_QTY.toString(), cartItem1.getQuantity());
		final String cartGuidWoDashes = CART_GUID.replaceAll("-", "");
		Assert.assertEquals(cartGuidWoDashes + "-" + CART_ITEM_1_ENTRY_NO.toString(), cartItem1.getExternalId());
		Assert.assertEquals(CART_ITEM_1_TOTAL_PRICE, cartItem1.getItemTotalPrice());
		Assert.assertEquals(DELIVERY_POINT_OF_SERVICE_NAME, cartItem1.getSource());
		Assert.assertEquals(cartItem1.getSourcePreselected(), "X");
	
		final CartItem cartItem2 = cartItemList.get(1);
		Assert.assertEquals(CART_ITEM_ID_2, cartItem2.getArticleId());
		Assert.assertEquals(CART_ITEM_2_QTY.toString(), cartItem2.getQuantity());
		Assert.assertEquals(cartGuidWoDashes + "-" + CART_ITEM_2_ENTRY_NO.toString(), cartItem2.getExternalId());
		Assert.assertEquals(CART_ITEM_2_TOTAL_PRICE, cartItem2.getItemTotalPrice());
		Assert.assertNull(cartItem2.getSource());
		Assert.assertNull(cartItem2.getSourcePreselected());
	}
	protected void assertNotNullObjAndObjToString(final Object obj)
	{
		Assert.assertNotNull(obj);
		Assert.assertNotNull(OAA_CONSUMER_ID, obj.toString());
	}

}

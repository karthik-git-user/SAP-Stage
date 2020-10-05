/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.ppengine.client.dto.DiscountBase;
import com.sap.ppengine.client.dto.ItemDomainSpecific;
import com.sap.ppengine.client.dto.LineItemDomainSpecific;
import com.sap.ppengine.client.dto.PriceCalculateBase;
import com.sap.ppengine.client.dto.PriceCalculateResponse;
import com.sap.ppengine.client.dto.PriceDerivationRuleBase;
import com.sap.ppengine.client.dto.QuantityCommonData;
import com.sap.ppengine.client.dto.RetailPriceModifierBase.Amount;
import com.sap.ppengine.client.dto.RetailPriceModifierDomainSpecific;
import com.sap.ppengine.client.dto.SaleBase;
import com.sap.ppengine.client.dto.ShoppingBasketBase;
import com.sap.ppengine.client.dto.UnitPriceCommonData;
import com.sap.ppengine.client.util.RequestHelper;
import com.sap.ppengine.client.util.RequestHelperImpl;
import com.sap.retail.sapppspricing.impl.DefaultPriceCalculateToOrderMapper;
import com.sap.retail.sapppspricing.impl.PPSAccessorHelper;


@UnitTest
public class DefaultPriceCalculateToOrderMapperTest
{

	public class PPSAccessorHelperForTest extends PPSAccessorHelper
	{

		@Override
		public RequestHelper getHelper()
		{
			return new RequestHelperImpl();
		}
	}

	private DefaultPriceCalculateToOrderMapper cut;

	private static final String DEFAULT_PREFIX = "DEF";
	private List<LineItemDomainSpecific> lineItems;
	private LineItemDomainSpecific lineItem;
	private DiscountBase discount;
	private Amount amount;
	private PriceDerivationRuleBase priceDerivRule;
	private CurrencyModel currency;
	private RetailPriceModifierDomainSpecific priceModifier;

	@Before
	public void setUp()
	{
		priceModifier = new RetailPriceModifierDomainSpecific();
		lineItems = new ArrayList<>();
		lineItem = new LineItemDomainSpecific();
		discount = new DiscountBase();
		amount = new Amount();

		priceDerivRule = new PriceDerivationRuleBase();
		currency = new CurrencyModel();

		cut = new DefaultPriceCalculateToOrderMapper();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsItemDiscountFalse1() throws Exception
	{
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse2() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse3() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse4() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		amount.setValue(BigDecimal.TEN);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse5() throws Exception
	{
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}

	@Test
	public void testIsItemDiscountFalse6() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertFalse(cut.isItemDiscount(priceModifier));
	}


	@Test
	public void testIsDistibutedItemDiscountFalse() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		amount.setValue(BigDecimal.ZERO);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse2() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse3() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscountFalse4() throws Exception
	{
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertFalse(cut.isDistributedItemDiscount(priceModifier));
	}

	@Test
	public void testIsDistibutedItemDiscount() throws Exception
	{
		priceModifier.getItemLink().add(BigInteger.ONE);
		amount.setValue(BigDecimal.ONE);
		priceModifier.setAmount(amount);
		assertTrue(cut.isDistributedItemDiscount(priceModifier));
	}


	@Test
	public void testGetOrder() throws Exception
	{
		assertEquals(0, cut.getOrder());
	}


	@Test
	public void testCodeForDiscountOneItemDiscount() throws Exception
	{
		amount.setValue(BigDecimal.ONE);
		fillPriceDerivationRule("PO");
		fillRetailPriceModifier();
		assertEquals("ABC", cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));

	}

	@Test
	public void testCodeForDiscountOneItemDiscountNoTypeCode() throws Exception
	{
		priceDerivRule.setTransactionControlBreakCode("PO");
		priceDerivRule.setPromotionPriceDerivationRuleTypeCode("");
		priceModifier.getPriceDerivationRule().add(priceDerivRule);
		assertEquals("DEF" + "_" + null, cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));
	}

	@Test
	public void testCodeForDiscountPriceRuleIsEmpty() throws Exception
	{
		assertEquals("DEF" + "_" + null, cut.codeForDiscount(null, priceModifier, DEFAULT_PREFIX));
	}

	@Test
	public void testConvertToEntryDiscountsNoItemDiscount() throws Exception
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setCurrency(currency);
		final ItemDomainSpecific item = new ItemDomainSpecific();
		item.getRetailPriceModifier().add(priceModifier);
		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(null, item, order);
		assertTrue(discountValues.isEmpty());
	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscount() throws Exception
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		final ItemDomainSpecific item = new ItemDomainSpecific();
		amount.setValue(BigDecimal.ONE);

		fillPriceDerivationRule("PO");
		fillRetailPriceModifier();
		item.getRetailPriceModifier().add(priceModifier);

		final QuantityCommonData quantity = new QuantityCommonData();
		item.getQuantity().add(quantity);
		item.getQuantity().get(0).setValue(BigDecimal.ONE);

		setOrderCurrency(order);

		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(null, item, order);
		assertEquals(1, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		final Object doubleValue = 1.0;
		assertEquals(doubleValue, discountValues.get(0).getValue());

	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscountAndOneHeaderDiscount() throws Exception
	{
		final List<LineItemDomainSpecific> lineItemList = new LinkedList<LineItemDomainSpecific>();

		final LineItemDomainSpecific lineItemDiscount = new LineItemDomainSpecific();
		final DiscountBase discountItem = new DiscountBase();
		discountItem.setSequenceNumber(BigInteger.ONE);
		final PriceDerivationRuleBase priceDerivRuleDiscount = new PriceDerivationRuleBase();
		priceDerivRuleDiscount.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode");
		discountItem.getPriceDerivationRule().add(priceDerivRuleDiscount);
		lineItemDiscount.setDiscount(discountItem);
		lineItemList.add(lineItemDiscount);

		final LineItemDomainSpecific lineItemSale = new LineItemDomainSpecific();
		final SaleBase saleItem = new SaleBase();
		amount.setValue(BigDecimal.ONE);
		final RetailPriceModifierDomainSpecific priceModifierItemDiscount = new RetailPriceModifierDomainSpecific();
		fillPriceDerivationRule("PO");
		priceModifierItemDiscount.getPriceDerivationRule().add(priceDerivRule);
		priceModifierItemDiscount.setAmount(amount);
		saleItem.getRetailPriceModifier().add(priceModifierItemDiscount);
		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount.setAmount(amount);
		priceModifierDistributedItemDiscount.getItemLink().add(BigInteger.ONE);
		saleItem.getRetailPriceModifier().add(priceModifierDistributedItemDiscount);
		final QuantityCommonData quantity = new QuantityCommonData();
		saleItem.getQuantity().add(quantity);
		saleItem.getQuantity().get(0).setValue(BigDecimal.ONE);
		lineItemSale.setSale(saleItem);
		lineItemList.add(lineItemSale);

		final AbstractOrderModel order = new AbstractOrderModel();
		setOrderCurrency(order);

		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(lineItemList, saleItem, order);

		final Object doubleValue = 1.0;
		assertEquals(2, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(0).getValue());

		assertEquals("HeaderTypeCode", discountValues.get(1).getCode());
		assertEquals("USD", discountValues.get(1).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(1).getValue());

	}

	@Test
	public void testConvertToEntryDiscountsOneItemDiscountAndTwoHeaderDiscount() throws Exception
	{
		final List<LineItemDomainSpecific> lineItemList = new LinkedList<LineItemDomainSpecific>();
		final PriceDerivationRuleBase priceDerivRuleDiscount1 = new PriceDerivationRuleBase();
		priceDerivRuleDiscount1.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode1");
		final PriceDerivationRuleBase priceDerivRuleDiscount2 = new PriceDerivationRuleBase();
		priceDerivRuleDiscount2.setPromotionPriceDerivationRuleTypeCode("HeaderTypeCode2");

		final LineItemDomainSpecific lineItemDiscount1 = new LineItemDomainSpecific();
		final DiscountBase discountItem1 = new DiscountBase();
		discountItem1.setSequenceNumber(BigInteger.ONE);
		discountItem1.getPriceDerivationRule().add(priceDerivRuleDiscount1);
		lineItemDiscount1.setDiscount(discountItem1);

		final LineItemDomainSpecific lineItemDiscount2 = new LineItemDomainSpecific();
		final DiscountBase discountItem2 = new DiscountBase();
		discountItem2.setSequenceNumber(BigInteger.TEN);
		discountItem2.getPriceDerivationRule().add(priceDerivRuleDiscount2);
		lineItemDiscount2.setDiscount(discountItem2);

		lineItemList.add(lineItemDiscount1);
		lineItemList.add(lineItemDiscount2);

		final LineItemDomainSpecific lineItemSale = new LineItemDomainSpecific();
		final SaleBase saleItem = new SaleBase();
		amount.setValue(BigDecimal.ONE);
		final QuantityCommonData quantity = new QuantityCommonData();
		saleItem.getQuantity().add(quantity);
		saleItem.getQuantity().get(0).setValue(BigDecimal.ONE);

		final RetailPriceModifierDomainSpecific priceModifierItemDiscount = new RetailPriceModifierDomainSpecific();
		fillPriceDerivationRule("PO");
		priceModifierItemDiscount.getPriceDerivationRule().add(priceDerivRule);
		priceModifierItemDiscount.setAmount(amount);
		saleItem.getRetailPriceModifier().add(priceModifierItemDiscount);

		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount1 = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount1.setAmount(amount);
		priceModifierDistributedItemDiscount1.getItemLink().add(BigInteger.TEN);
		saleItem.getRetailPriceModifier().add(priceModifierDistributedItemDiscount1);

		final RetailPriceModifierDomainSpecific priceModifierDistributedItemDiscount2 = new RetailPriceModifierDomainSpecific();
		priceModifierDistributedItemDiscount2.setAmount(amount);
		priceModifierDistributedItemDiscount2.getItemLink().add(BigInteger.ONE);
		saleItem.getRetailPriceModifier().add(priceModifierDistributedItemDiscount2);

		lineItemSale.setSale(saleItem);

		lineItemList.add(lineItemSale);


		final AbstractOrderModel order = new AbstractOrderModel();
		setOrderCurrency(order);

		final List<DiscountValue> discountValues = cut.convertToEntryDiscounts(lineItemList, saleItem, order);

		final Object doubleValue = 1.0;
		assertEquals(3, discountValues.size());
		assertEquals("ABC", discountValues.get(0).getCode());
		assertEquals("USD", discountValues.get(0).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(0).getValue());

		assertEquals("HeaderTypeCode2", discountValues.get(1).getCode());
		assertEquals("USD", discountValues.get(1).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(1).getValue());

		assertEquals("HeaderTypeCode1", discountValues.get(2).getCode());
		assertEquals("USD", discountValues.get(2).getCurrencyIsoCode());
		assertEquals(doubleValue, discountValues.get(2).getValue());


	}


	private void setOrderCurrency(final AbstractOrderModel order)
	{
		currency.setIsocode("USD");
		order.setCurrency(currency);
	}

	@Test(expected = RuntimeException.class)
	public void testMapResponseToCartEntriesNoSaleItemsInResponse() throws Exception
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		orderEntries.add(orderEntry);
		order.setEntries(orderEntries);

		fillPriceDerivationRule("SU");

		amount.setValue(BigDecimal.ONE);
		discount.setAmount(amount);
		discount.getPriceDerivationRule().add(priceDerivRule);

		lineItem.setDiscount(discount);
		lineItems.add(lineItem);

		cut.mapResponseToCartEntries(lineItems, order);
	}

	@Test
	public void testMapResponseToCartEntries() throws Exception
	{

		final AbstractOrderEntryModel orderEntry = Mockito.spy(new OrderEntryModel());

		final ArgRecorderAnswer<Void> answer = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answer).when(orderEntry).setDiscountValues(Mockito.anyListOf(DiscountValue.class));


		final AbstractOrderModel order = new AbstractOrderModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final ProductModel productModel = new ProductModel();
		amount.setValue(BigDecimal.ONE);

		orderEntry.setProduct(productModel);
		orderEntries.add(orderEntry);
		order.setEntries(orderEntries);

		setOrderCurrency(order);

		fillPriceDerivationRule("PO");
		fillRetailPriceModifier();

		final SaleBase sale = new SaleBase();
		final UnitPriceCommonData price = new UnitPriceCommonData();
		price.setValue(BigDecimal.TEN);
		sale.setRegularSalesUnitPrice(price);

		final QuantityCommonData quantity = new QuantityCommonData();
		quantity.setValue(BigDecimal.TEN);
		sale.getQuantity().add(quantity);
		sale.getRetailPriceModifier().add(priceModifier);

		lineItem.setSale(sale);
		lineItems.add(lineItem);

		cut.setAccessorHelper(new PPSAccessorHelperForTest());

		cut.mapResponseToCartEntries(lineItems, order);

		final List<DiscountValue> arg = answer.getArg(0);
		assertEquals(1, arg.size());
		final Object doubleValue = 0.1;
		assertEquals(doubleValue, arg.get(0).getValue());
		assertEquals("ABC", arg.get(0).getCode());
	}




	@Test
	public void testMapCallsSupMethods()
	{
		final PriceCalculateResponse response = new PriceCalculateResponse();
		final PriceCalculateBase priceCalculateBase = new PriceCalculateBase();
		final ShoppingBasketBase shoppingBasketBase = new ShoppingBasketBase();
		final List<LineItemDomainSpecific> lineItems = shoppingBasketBase.getLineItem();
		lineItems.add(lineItem);
		priceCalculateBase.setShoppingBasket(shoppingBasketBase);
		response.getPriceCalculateBody().add(priceCalculateBase);
		final AbstractOrderModel order = null;

		final DefaultPriceCalculateToOrderMapper cutSpy = Mockito.spy(cut);

		Mockito.doNothing().when(cutSpy).mapResponseToCartEntries(lineItems, order);

		cutSpy.map(response, order);
		Mockito.verify(cutSpy).mapResponseToCartEntries(lineItems, order);
	}

	private void fillRetailPriceModifier()
	{
		priceModifier.getPriceDerivationRule().add(priceDerivRule);
		priceModifier.setAmount(amount);
	}

	private void fillPriceDerivationRule(final String ruleControlCode)
	{
		priceDerivRule.setTransactionControlBreakCode(ruleControlCode);
		priceDerivRule.setPromotionPriceDerivationRuleTypeCode("ABC");
	}
}

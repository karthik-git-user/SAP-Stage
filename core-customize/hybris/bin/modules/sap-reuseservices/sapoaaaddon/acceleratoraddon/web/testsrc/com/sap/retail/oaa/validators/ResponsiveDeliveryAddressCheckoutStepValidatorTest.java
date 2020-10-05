/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;


/**
 *
 */
@UnitTest
public class ResponsiveDeliveryAddressCheckoutStepValidatorTest
{

	private final ResponsiveDeliveryAddressCheckoutStepValidator classUnderTest = new ResponsiveDeliveryAddressCheckoutStepValidator();

	/**
	 * Creates Mocks and adds them to the Test class
	 *
	 * @param hasValidCart
	 * @param hasShippingItems
	 * @param hasPickUpItems
	 * @param sourcingSuccessful
	 */
	protected void addMocksToClassUnderTest(final boolean hasValidCart, final boolean hasShippingItems,
			final boolean hasPickUpItems, final boolean sourcingSuccessful)
	{
		//Mock OAA Checkout Facade - no pick up items, but shipping items
		final OaaCheckoutFacade oaaCheckoutFacadeMock = EasyMock.createNiceMock(OaaCheckoutFacade.class);
		EasyMock.expect(Boolean.valueOf(oaaCheckoutFacadeMock.doSourcingForSessionCart()))
				.andReturn(new Boolean(sourcingSuccessful));
		EasyMock.replay(oaaCheckoutFacadeMock);
		classUnderTest.setOaaCheckoutFacade(oaaCheckoutFacadeMock);

		//Mock Checkout Flow Facade
		final CheckoutFlowFacade checkoutFlowFacade = EasyMock.createNiceMock(CheckoutFlowFacade.class);
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasValidCart())).andReturn(new Boolean(hasValidCart));
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasShippingItems())).andReturn(new Boolean(hasShippingItems));
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasPickUpItems())).andReturn(new Boolean(hasPickUpItems));
		EasyMock.replay(checkoutFlowFacade);
		classUnderTest.setCheckoutFlowFacade(checkoutFlowFacade);
	}

	@Test
	public void testValidateOnEnter_Success()
	{
		addMocksToClassUnderTest(true, true, true, true);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.SUCCESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_pickUpOnlySuccess()
	{
		addMocksToClassUnderTest(true, false, true, true);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.REDIRECT_TO_PAYMENT_METHOD, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_shippingOnlySuccess()
	{
		addMocksToClassUnderTest(true, true, false, true);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.SUCCESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noValidCart()
	{
		addMocksToClassUnderTest(false, true, true, true);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_sourcingError()
	{
		addMocksToClassUnderTest(true, true, true, false);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_pickupOnlySourcingError()
	{
		addMocksToClassUnderTest(true, false, true, false);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_shippingOnlySourcingError()
	{
		addMocksToClassUnderTest(true, true, false, false);

		//Mock redirect Attributes
		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		//Expect that you are redirected to the cart
		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}
}

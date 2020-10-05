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
public class ResponsivePaymentCheckoutStepValidatorTest
{

	private final ResponsivePaymentCheckoutStepValidator classUnderTest = new ResponsivePaymentCheckoutStepValidator();

	/**
	 * Creates Mocks and adds them to the Test class
	 *
	 * @param hasValidCart
	 * @param hasNoDeliveryAddress
	 * @param hasNoDeliveryMode
	 * @param sourcingSuccessful
	 */
	protected void addMocksToClassUnderTest(final boolean hasValidCart, final boolean hasNoDeliveryAddress,
			final boolean hasNoDeliveryMode, final boolean sourcingSuccessful)
	{
		//Mock OAA Checkout Facade - no pick up items, but shipping items
		final OaaCheckoutFacade oaaCheckoutFacadeMock = EasyMock.createNiceMock(OaaCheckoutFacade.class);
		EasyMock.expect(Boolean.valueOf(oaaCheckoutFacadeMock.doSourcingForSessionCart()))
				.andReturn(new Boolean(sourcingSuccessful)).anyTimes();
		EasyMock.replay(oaaCheckoutFacadeMock);
		classUnderTest.setOaaCheckoutFacade(oaaCheckoutFacadeMock);

		//Mock Checkout Flow Facade
		final CheckoutFlowFacade checkoutFlowFacade = EasyMock.createNiceMock(CheckoutFlowFacade.class);
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasValidCart())).andReturn(new Boolean(hasValidCart)).anyTimes();
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasNoDeliveryAddress())).andReturn(new Boolean(hasNoDeliveryAddress))
				.anyTimes();
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasNoDeliveryMode())).andReturn(new Boolean(hasNoDeliveryMode))
				.anyTimes();
		EasyMock.replay(checkoutFlowFacade);
		classUnderTest.setCheckoutFlowFacade(checkoutFlowFacade);
	}

	@Test
	public void testValidateOnEnter_Success()
	{
		addMocksToClassUnderTest(true, false, false, true);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.SUCCESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noValidCart()
	{
		addMocksToClassUnderTest(false, false, false, true);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noDeliveryAddress()
	{
		addMocksToClassUnderTest(true, true, false, true);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noDeliveryMode()
	{
		addMocksToClassUnderTest(true, false, true, true);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_METHOD, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_sourcingError()
	{
		addMocksToClassUnderTest(true, false, false, false);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


/**
 *
 */
@UnitTest
public class ResponsiveSummaryCheckoutStepValidatorTest
{

	private final ResponsiveSummaryCheckoutStepValidator classUnderTest = new ResponsiveSummaryCheckoutStepValidator();

	/**
	 * Creates Mocks and adds them to the Test class
	 *
	 * @param hasValidCart
	 * @param hasNoDeliveryAddress
	 * @param hasNoDeliveryMode
	 * @param hasNoPaymentInfo
	 */
	protected void addMocksToClassUnderTest(final boolean hasValidCart, final boolean hasNoDeliveryAddress,
			final boolean hasNoDeliveryMode, final boolean hasNoPaymentInfo)
	{
		//OAA Facade not needed, because sourcing is not called in Summary step
		//		//Mock OAA Checkout Facade - no pick up items, but shipping items
		//		final OaaCheckoutFacade oaaCheckoutFacadeMock = EasyMock.createNiceMock(OaaCheckoutFacade.class);
		//		EasyMock.expect(Boolean.valueOf(oaaCheckoutFacadeMock.doSourcingForSessionCart()))
		//				.andReturn(new Boolean(sourcingSuccessful)).anyTimes();
		//		EasyMock.replay(oaaCheckoutFacadeMock);
		//		classUnderTest.setOaaCheckoutFacade(oaaCheckoutFacadeMock);

		//Mock Checkout Flow Facade
		final CartData cartData = new CartData();
		final DeliveryModeData deliveryMode = new DeliveryModeData();
		cartData.setDeliveryMode(deliveryMode);

		final CheckoutFlowFacade checkoutFlowFacade = EasyMock.createNiceMock(CheckoutFlowFacade.class);
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasValidCart())).andReturn(new Boolean(hasValidCart)).anyTimes();
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasNoDeliveryAddress())).andReturn(new Boolean(hasNoDeliveryAddress))
				.anyTimes();
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasNoDeliveryMode())).andReturn(new Boolean(hasNoDeliveryMode))
				.anyTimes();
		EasyMock.expect(Boolean.valueOf(checkoutFlowFacade.hasNoPaymentInfo())).andReturn(new Boolean(hasNoPaymentInfo)).anyTimes();
		EasyMock.expect(checkoutFlowFacade.getCheckoutCart()).andReturn(cartData).anyTimes();
		EasyMock.replay(checkoutFlowFacade);
		classUnderTest.setCheckoutFlowFacade(checkoutFlowFacade);
	}

	@Test
	public void testValidateOnEnter_Success()
	{
		addMocksToClassUnderTest(true, false, false, false);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.SUCCESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noValidCart()
	{
		addMocksToClassUnderTest(false, false, false, false);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_CART, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noDeliveryAddress()
	{
		addMocksToClassUnderTest(true, true, false, false);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noDeliveryMode()
	{
		addMocksToClassUnderTest(true, false, true, false);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_DELIVERY_METHOD, classUnderTest.validateOnEnter(redirect));
	}

	@Test
	public void testValidateOnEnter_noPaymentInfo()
	{
		addMocksToClassUnderTest(true, false, false, true);

		final RedirectAttributes redirect = new RedirectAttributesModelMap();

		Assert.assertEquals(ValidationResults.REDIRECT_TO_PAYMENT_METHOD, classUnderTest.validateOnEnter(redirect));
	}

}

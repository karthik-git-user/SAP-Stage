/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.validators;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartData;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;
import com.sap.retail.oaa.constants.SapoaaaddonWebConstants;


/**
 * Validator for Summary in Checkout
 */
public class ResponsiveSummaryCheckoutStepValidator extends AbstractCheckoutStepValidator
{
	private OaaCheckoutFacade oaaCheckoutFacade;

	@Override
	public ValidationResults validateOnEnter(final RedirectAttributes redirectAttributes)
	{
		if (!getCheckoutFlowFacade().hasValidCart())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					SapoaaaddonWebConstants.OAA_CHECKOUT_ERROR);
			return ValidationResults.REDIRECT_TO_CART;
		}

		final ValidationResults cartResult = checkCartAndDelivery(redirectAttributes);
		if (cartResult != null)
		{
			return cartResult;
		}

		final ValidationResults paymentResult = checkPaymentMethodAndPickup(redirectAttributes);
		if (paymentResult != null)
		{
			return paymentResult;
		}

		return ValidationResults.SUCCESS;
	}

	/**
	 * Performs Sourcing for current session cart and redirects to the corresponding checkout step.
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults doSourcingAndRedirect(final RedirectAttributes redirectAttributes)
	{
		if (oaaCheckoutFacade.doSourcingForSessionCart())
		{
			return ValidationResults.SUCCESS;
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					SapoaaaddonWebConstants.OAA_CHECKOUT_ERROR);
			return ValidationResults.REDIRECT_TO_CART;
		}
	}

	/**
	 * Checks the payment method and pickup information
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults checkPaymentMethodAndPickup(final RedirectAttributes redirectAttributes)
	{
		if (getCheckoutFlowFacade().hasNoPaymentInfo())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.paymentDetails.notprovided");
			return ValidationResults.REDIRECT_TO_PAYMENT_METHOD;
		}

		final CartData cartData = getCheckoutFlowFacade().getCheckoutCart();

		if (!getCheckoutFlowFacade().hasShippingItems())
		{
			cartData.setDeliveryAddress(null);
		}

		if (!getCheckoutFlowFacade().hasPickUpItems() && "pickup".equals(cartData.getDeliveryMode().getCode()))
		{
			return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
		}
		return null;
	}

	/**
	 * Checks the cart delivery method and mode
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults checkCartAndDelivery(final RedirectAttributes redirectAttributes)
	{

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
					"checkout.multi.deliveryMethod.notprovided");
			return ValidationResults.REDIRECT_TO_DELIVERY_METHOD;
		}
		return null;
	}

	/**
	 * Sets the OaaCheckoutFacade.
	 *
	 * @param oaaCheckoutFacade
	 */
	public void setOaaCheckoutFacade(final OaaCheckoutFacade oaaCheckoutFacade)
	{
		this.oaaCheckoutFacade = oaaCheckoutFacade;
	}

	/**
	 * Returns the OaaCheckoutFacade.
	 *
	 * @return OaaCheckoutFacade
	 */
	protected OaaCheckoutFacade getOaaCheckoutFacade()
	{
		return this.oaaCheckoutFacade;
	}
}

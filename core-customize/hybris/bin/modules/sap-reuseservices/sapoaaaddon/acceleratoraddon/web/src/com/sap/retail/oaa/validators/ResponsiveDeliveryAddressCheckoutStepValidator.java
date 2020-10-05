/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.validators;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;
import com.sap.retail.oaa.constants.SapoaaaddonWebConstants;


/**
 * Validator for Delivery Address in Checkout
 */
public class ResponsiveDeliveryAddressCheckoutStepValidator extends AbstractCheckoutStepValidator
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

		return doSourcingAndRedirect(redirectAttributes);
	}

	/**
	 * Performs Sourcing for current session cart and redirects to the next checkout step.
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults doSourcingAndRedirect(final RedirectAttributes redirectAttributes)
	{
		//  When the cart has only pickup items and sourcing is successful forward to payment step
		if (!getCheckoutFlowFacade().hasShippingItems() && getCheckoutFlowFacade().hasPickUpItems())
		{
			return doSourcingAndRedirectPickUpItemsOnly(redirectAttributes);
		}
		else
		{
			return doSourcingAndRedirectShipToAndPickUpItems(redirectAttributes);
		}
	}

	/**
	 * Performs Sourcing for current session cart and redirects to the corresponding checkout step.
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults doSourcingAndRedirectShipToAndPickUpItems(final RedirectAttributes redirectAttributes)
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
	 * Performs Sourcing for pickup items in the current session cart and redirects to the corresponding checkout step.
	 *
	 * @param redirectAttributes
	 * @return ValidationResults
	 */
	protected ValidationResults doSourcingAndRedirectPickUpItemsOnly(final RedirectAttributes redirectAttributes)
	{
		if (oaaCheckoutFacade.doSourcingForSessionCart())
		{
			return ValidationResults.REDIRECT_TO_PAYMENT_METHOD;
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					SapoaaaddonWebConstants.OAA_CHECKOUT_ERROR);
			return ValidationResults.REDIRECT_TO_CART;
		}
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

	/**
	 * Sets the OaaCheckoutFacade.
	 *
	 * @param oaaCheckoutFacade
	 */
	public void setOaaCheckoutFacade(final OaaCheckoutFacade oaaCheckoutFacade)
	{
		this.oaaCheckoutFacade = oaaCheckoutFacade;
	}
}
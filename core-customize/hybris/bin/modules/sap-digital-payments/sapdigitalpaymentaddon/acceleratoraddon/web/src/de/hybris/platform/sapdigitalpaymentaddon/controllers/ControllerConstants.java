/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentaddon.controllers;

/**
 *
 * Controller constant class for sapdigitalpaymentaddon extension
 */
public interface ControllerConstants
{
	String ADDON_PREFIX = "addon:/sapdigitalpaymentaddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Pages
		{

			interface MultiStepCheckout
			{
				String AddPaymentMethodPage = "pages/checkout/multi/addPaymentMethodPage";
				String AddEditBillingAddressPage = ADDON_PREFIX + "pages/checkout/multi/addEditBillingAddressPage";
				String AddEditCardDetailsPage = ADDON_PREFIX + "pages/checkout/multi/addEditCardDetailsPage";
				String DigitalPaymentGeneralErrorPage = ADDON_PREFIX + "pages/checkout/multi/digitalPaymentGeneralErrorPage";
				String AccountSapDigitalPaymentInfoPage = ADDON_PREFIX + "pages/account/accountSapDigitalPaymentInfoPage";
			}

			interface Error
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage";
			}
		}
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.controllers;

/**
 */
public interface SaprevenueclouddpaddonControllerConstants
{
	// implement here controller constants used by this extension
    String ADDON_PREFIX = "addon:/saprevenueclouddpaddon/";


    interface Views
    {
        interface Fragments
        {
            interface DigitalPayments
            {
                String CHANGE_PAYMENT_DETAILS_POPUP = ADDON_PREFIX + "fragments/account/changePaymentDetailsPopup";
                String CHANGE_PAYMENT_SAVED_CARDS_POPUP = ADDON_PREFIX + "fragments/account/changePaymentSavedCardsPopup";
            }

        }

    }

}

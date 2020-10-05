/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.controllers;

/**
 */
public interface SapsubscriptionaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/sapsubscriptionaddon/";

	interface Views
	{
		interface Fragments
		{
			interface Subscriptions
			{
				String CancelSubscriptionPopup = ADDON_PREFIX + "fragments/account/cancelSubscriptionPopup";
				String EXTEND_SUBSCRIPTION_POPUP = ADDON_PREFIX + "fragments/account/extendSubscriptionPopup";
				String WithdrawSubscriptionPopup = ADDON_PREFIX + "fragments/account/withdrawSubscriptionPopup";

			}

		}
	}

}

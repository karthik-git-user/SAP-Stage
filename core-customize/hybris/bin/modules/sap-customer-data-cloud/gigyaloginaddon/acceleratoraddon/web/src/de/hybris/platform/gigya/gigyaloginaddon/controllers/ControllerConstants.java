/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.controllers;

public interface ControllerConstants {
	final String ADDON_PREFIX = "addon:/gigyaloginaddon/";

	String GLT_COOKIE = "glt_";

	String GLT_EXP_COOKIE = "gltexp_";

	String LOGION_TOKEN = "-LoginToken";
	
	String SAME_SITE_ATTRIBUTE_LAX = "; SameSite=Lax";
	
	String GLT_EXP_COOKIE_SAMESITE_ATTR_VAL="gigyaloginaddon.gltexp.cookie.samesite.value";

	interface Views {
		interface Pages {
			interface Account {
				String AccountLoginPage = ADDON_PREFIX + "pages/account/accountLoginPage";// NOSONAR
			}

			interface Checkout {
				String CheckoutLoginPage = ADDON_PREFIX + "pages/checkout/checkoutLoginPage";// NOSONAR
			}
		}

		interface Fragments {
			interface Checkout // NOSONAR
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup"; // NOSONAR
			}
		}
	}
}

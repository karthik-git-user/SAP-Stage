/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.facade;

import de.hybris.platform.commercefacades.user.data.AddressData;


/**
 * HOP facade interface. Facade is responsible for getting all necessary information required to build request and ..
 * response from the implemented hosted order page.
 */
public interface CisSapDigitalPaymentFacade
{


	/**
	 * Gets the sap-digital-payment-card-registration-session-id from the session.
	 *
	 * @return sap-digital-payment-card-registration-session-id set in session
	 */
	String getSapDigitalPaymentRegisterCardSession();


	/**
	 * Removes the sap-digital-payment-card-registration-session-id from the session.
	 */
	void removeSapDigitalPaymentRegisterCardSession();


	/**
	 * Sets the sap-digital-payment-card-registration-session-id to the session.
	 *
	 */
	void setSapDigitalPaymentRegisterCardSession(String sessionId);


	/**
	 * Add the payment address to the cart
	 *
	 * @param paymentAddress
	 *           - payment address to be added to the cart
	 */
	void addPaymentAddressToCart(AddressData paymentAddress);

}

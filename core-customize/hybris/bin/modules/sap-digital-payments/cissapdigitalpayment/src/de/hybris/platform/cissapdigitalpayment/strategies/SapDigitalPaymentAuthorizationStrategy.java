/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;


/**
 * Defines the method for payment amount authorization stategy
 */
public interface SapDigitalPaymentAuthorizationStrategy
{
	/**
	 * Payment authorization method for {@link SapDigitalPaymentAuthorizationStrategy}
	 * 
	 * @param parameter
	 *           - {@link CommerceCheckoutParameter} commerce checkout parameter
	 *
	 * @return boolean - determine whether the authorization call is success or not
	 */
	boolean authorizePayment(final CommerceCheckoutParameter parameter);


}

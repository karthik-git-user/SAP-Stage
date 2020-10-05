/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.checkout;

import de.hybris.platform.commercefacades.order.CheckoutFacade;


/**
 * Checkout Facade for OAA Logic. Adds Sourcing for Session Cart.
 */
public interface OaaCheckoutFacade extends CheckoutFacade
{
	/**
	 * Call Sourcing REST Service and persist the result (Schedule Lines) in the given cart.
	 *
	 *@return {@link Boolean}
	 */
	public boolean doSourcingForSessionCart();
}
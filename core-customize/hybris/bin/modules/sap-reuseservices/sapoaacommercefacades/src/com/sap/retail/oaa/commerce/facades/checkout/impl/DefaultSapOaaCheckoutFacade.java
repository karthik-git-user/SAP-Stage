/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.checkout.impl;

import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;

import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;
import com.sap.retail.oaa.commerce.services.sourcing.strategy.SourcingStrategy;


/**
 * Default Checkout Facade for OAA.
 *
 */
public class DefaultSapOaaCheckoutFacade extends DefaultCheckoutFacade implements OaaCheckoutFacade
{
	private SourcingStrategy sourcingStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade#doSourcingForSessionCart()
	 */
	@Override
	public boolean doSourcingForSessionCart()
	{
		return sourcingStrategy.doSourcing(getCart());
	}

	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected CartModel getCart()
	{
		return super.getCart();
	}

	/**
	 * @return the sourcingStrategy
	 */
	public SourcingStrategy getSourcingStrategy()
	{
		return sourcingStrategy;
	}

	/**
	 * @param sourcingStrategy
	 *           the sourcingStrategy to set
	 */
	public void setSourcingStrategy(final SourcingStrategy sourcingStrategy)
	{
		this.sourcingStrategy = sourcingStrategy;
	}
}

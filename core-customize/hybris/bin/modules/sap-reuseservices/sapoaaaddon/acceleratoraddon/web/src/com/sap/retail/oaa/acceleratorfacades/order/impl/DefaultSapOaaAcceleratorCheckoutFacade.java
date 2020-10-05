/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.acceleratorfacades.order.impl;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;

import com.sap.retail.oaa.acceleratorfacades.order.OaaAcceleratorCheckoutFacade;
import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;


/**
 * Default accelerator checkout facade for enabling OAA in express checkout functionality.
 */
public class DefaultSapOaaAcceleratorCheckoutFacade extends DefaultAcceleratorCheckoutFacade implements
		OaaAcceleratorCheckoutFacade
{
	private OaaCheckoutFacade oaaCheckoutFacade;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade#performExpressCheckout()
	 */
	@Override
	public ExpressCheckoutResult performExpressCheckout()
	{
		final ExpressCheckoutResult expressCheckoutResult = this.callSuperPerformExpressCheckout();

		if (expressCheckoutResult.equals(AcceleratorCheckoutFacade.ExpressCheckoutResult.SUCCESS)
				&& !doSourcingForExpressCheckoutCart())
		{
			// since hybris has declared ExpressCheckoutResult as an Enum it's not
			// possible at this point to return a more suitable info...
			return ExpressCheckoutResult.ERROR_NOT_AVAILABLE;
		}
		return expressCheckoutResult;
	}

	/**
	 * Encapsulated super-call of performExpressCheckout().
	 *
	 * @return ExpressCheckoutResult
	 */
	protected ExpressCheckoutResult callSuperPerformExpressCheckout()
	{
		return super.performExpressCheckout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.retail.oaa.acceleratorfacades.order.OaaAcceleratorCheckoutFacade#doSourcingForExpressCheckoutCart()
	 */
	@Override
	public boolean doSourcingForExpressCheckoutCart()
	{
		return oaaCheckoutFacade.doSourcingForSessionCart();
	}

	/**
	 * @return the oaaCheckoutFacade
	 */
	public OaaCheckoutFacade getOaaCheckoutFacade()
	{
		return oaaCheckoutFacade;
	}

	/**
	 * @param oaaCheckoutFacade
	 *           the oaaCheckoutFacade to set
	 */
	public void setOaaCheckoutFacade(final OaaCheckoutFacade oaaCheckoutFacade)
	{
		this.oaaCheckoutFacade = oaaCheckoutFacade;
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.acceleratorfacades.order;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;


/**
 * Accelerator checkout facade for enabling OAA in express checkout functionality.
 */
public interface OaaAcceleratorCheckoutFacade extends AcceleratorCheckoutFacade
{
	
	/**
	 * Call the REST Service in Customer Activity Repository (CAR) and persist the result (Schedule Lines) in the given
	 * cart.
	 *
	 * @return boolean
	 */
	public boolean doSourcingForExpressCheckoutCart();

}

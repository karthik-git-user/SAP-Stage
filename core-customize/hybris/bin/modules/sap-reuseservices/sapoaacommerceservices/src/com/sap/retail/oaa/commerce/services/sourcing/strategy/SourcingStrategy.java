/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.strategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Strategy for Omni Channel Availability Sourcing
 */
public interface SourcingStrategy
{
	/**
	 * Call the Sourcing Service with given Parameters
	 *
	 * @param abstractOrderModel
	 *
	 */
	boolean doSourcing(AbstractOrderModel abstractOrderModel);

}

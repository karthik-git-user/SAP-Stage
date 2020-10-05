/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudcustomer.event;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;


/**
 *
 */
public class SapRevenueCloudCustomerUpdateEvent extends AbstractEvent implements ClusterAwareEvent
{
	Customer customerJson;

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return sourceNodeId == targetNodeId;
	}

	/**
	 * @return the customerJson
	 */
	public Customer getCustomerJson()
	{
		return customerJson;
	}

	/**
	 * @param customerJson
	 *           the customerJson to set
	 */
	public void setCustomerJson(final Customer customerJson)
	{
		this.customerJson = customerJson;
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysapcpircomsfulfillment.actions;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.yacceleratorordermanagement.actions.consignment.RedirectConsignmentByDeliveryModeAction;

import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SAPRCRedirectConsignmentByDeliveryModeAction extends RedirectConsignmentByDeliveryModeAction
{
	private static final Logger LOG = LoggerFactory.getLogger(SAPRCRedirectConsignmentByDeliveryModeAction.class);

	Predicate<ConsignmentEntryModel> isSubscriptionProduct = entry -> entry.getOrderEntry().getProduct()
			.getSubscriptionCode() != null ? Boolean.TRUE : Boolean.FALSE;


	@Override
	public String execute(final ConsignmentProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		String transition = "SHIP";

		final ConsignmentModel consignment = process.getConsignment();

		final Set<ConsignmentEntryModel> entries = consignment.getConsignmentEntries();

		if(entries.stream().allMatch(isSubscriptionProduct))
		{
			transition = "SUBSCRIPTION";
		}
		else
		{
			transition = super.execute(process);
		}
		return transition;
	}

}

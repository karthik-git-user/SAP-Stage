/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.populator;

import java.time.Instant;
import java.time.Period;
import java.util.Optional;


import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.util.Config;

public class DefaultSapServiceOrderPopulator implements Populator<OrderModel, OrderData> {
	
	@Override
	public void populate(OrderModel source, OrderData target)
	{
		for(final OrderEntryData entry:  target.getEntries())
		{
			if(SapServiceOrderUtil.isServiceEntry(entry))
			{
				entry.setReturnableQty(0);
				entry.setCancellableQty(0);
			}
		}
		
		if(isServiceOnlyOrder(target))
		{
			target.setCancellable(false);
		}
		
        if(source.getSapOrders() != null && !source.getSapOrders().isEmpty())
        {
        	Optional<SAPOrderModel> serviceSAPOrderOptional = source.getSapOrders().stream()
        			.filter(SapServiceOrderUtil::isServiceSAPOrder).findFirst();
        	if(serviceSAPOrderOptional.isPresent())
        	{
        		ConsignmentModel serviceConsignment = serviceSAPOrderOptional.get().getConsignments().stream().findFirst().get();
        		
        		target.setServiceCancellable(isServiceOrderCancellable(source, serviceConsignment));
        		target.setServiceReschedulable(isServiceOrderReschedulable(source, serviceConsignment));
        	}
        	
        	
        }
	}

	protected boolean isServiceOnlyOrder(OrderData target)
	{
		return target.getEntries().stream().allMatch(SapServiceOrderUtil::isServiceEntry);
	}
	
	protected boolean isServiceOrderReschedulable(OrderModel order, ConsignmentModel consignment)
	{
		return SapServiceOrderUtil.areAllConsignmentEntriesReady(consignment) && isModificationAllowedByCutoffDays(order);
	}
	
	protected boolean isServiceOrderCancellable(OrderModel order, ConsignmentModel consignment)
	{
		return SapServiceOrderUtil.areAllConsignmentEntriesReady(consignment) && isModificationAllowedByCutoffDays(order);
	}
	
	protected boolean isModificationAllowedByCutoffDays(OrderModel order)
	{
		Instant currentDate = Instant.now();
		Instant requestedStartDate = order.getRequestedServiceStartDate().toInstant();
		int cutoffDays = getModificationCutoffDays(order);
		
		return currentDate.isBefore(requestedStartDate.minus(Period.ofDays(cutoffDays)));
	}
	
	protected int getModificationCutoffDays(OrderModel order)
	{
		SAPConfigurationModel sapConfig = order.getStore().getSAPConfiguration();
		return sapConfig.getServiceOrderModificationCutoffDays() != null ?
				sapConfig.getServiceOrderModificationCutoffDays() :
				Integer.parseInt(Config.getParameter(SapserviceorderConstants.DEFAULT_MODIFICATION_CUTOFF_DAYS));
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.util;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

public class SapServiceOrderUtil
{
	private SapServiceOrderUtil()
	{
		
	}
	
	public static boolean isServiceProduct(ProductModel product)
	{
		return product.getServiceCode()!= null && !product.getServiceCode().isEmpty();
	}
	
	public static boolean isServiceProduct(ProductData product)
	{
		return product.getServiceCode()!= null && !product.getServiceCode().isEmpty();
	}
	
	public static boolean isServiceEntry(OrderEntryData entry)
	{
		return isServiceProduct(entry.getProduct());
	}
	
	public static boolean isServiceEntry(AbstractOrderEntryModel entry)
	{
		return isServiceProduct(entry.getProduct());
	}
	
	public static boolean isServiceEntry(ConsignmentEntryModel entry)
	{
		return isServiceProduct(entry.getOrderEntry().getProduct());
	}
	
	public static boolean isServiceConsignment(ConsignmentModel consignment)
	{
		if(consignment.getSapOrder() != null)
		{
			return isServiceSAPOrder(consignment.getSapOrder());
		}
		else
		{
			return consignment.getConsignmentEntries().stream().allMatch(SapServiceOrderUtil::isServiceEntry);
		}
	}
	
	public static boolean isServiceSAPOrder(SAPOrderModel sapOrder)
	{
		return sapOrder.getSapOrderType().equals(SAPOrderType.SERVICE);
	}
	
	public static boolean areAllConsignmentEntriesReady(ConsignmentModel consignment)
	{
		final Predicate<ConsignmentEntryModel> isConsignmentEntryReady = entry -> entry.getStatus().equals(ConsignmentEntryStatus.READY);
		return consignment.getConsignmentEntries().stream().allMatch(isConsignmentEntryReady);
	}

	public static void logOutboundErrorReason(Logger LOG, final Throwable error)
	{
		if(error instanceof HttpServerErrorException.InternalServerError)
		{
			LOG.error("An internal server error occurred while processing the service order through SCPI.");
		}
		else if(error instanceof HttpServerErrorException.ServiceUnavailable || error instanceof HttpServerErrorException.GatewayTimeout)
		{
			LOG.error("An error occurred while connecting to SCPI.");
		}
		else if(error instanceof HttpClientErrorException.Unauthorized)
		{
			LOG.error("An authorization error occurred while connecting to SCPI.");
		}
		else if(error instanceof HttpClientErrorException.NotFound)
		{
			LOG.error("An error occurred while locating the service order integration flow in SCPI.");
		}
		LOG.error("Error: ", error);
	}
}
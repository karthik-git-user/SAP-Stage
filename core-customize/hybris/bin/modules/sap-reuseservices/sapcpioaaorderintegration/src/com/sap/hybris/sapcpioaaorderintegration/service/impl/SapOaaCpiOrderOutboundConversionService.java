/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderOutboundConversionService;
import de.hybris.platform.site.BaseSiteService;


/**
 * SapOaaCpiOrderOutboundConversionService
 */
public class SapOaaCpiOrderOutboundConversionService extends SapCpiOmmOrderOutboundConversionService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(SapOaaCpiOrderOutboundConversionService.class);

	private List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderMappers;
	private SourcingService sourcingService;
	private BaseSiteService baseSiteService;


	@Override
	public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(final OrderModel orderModel)
	{
		if (orderModel.getSapCosSystemUsed() == null || !orderModel.getSapCosSystemUsed()) {
			performSourcingAndConfirmReservation(orderModel);
		} 
		
		final SAPCpiOutboundOrderModel oaaCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();
		getSapCpiOrderMappers().forEach(mapper -> mapper.map(orderModel, oaaCpiOutboundOrderModel));
		return oaaCpiOutboundOrderModel;
	}

	protected void performSourcingAndConfirmReservation(final OrderModel orderModel) {

		doSourcingAndReservation(orderModel);
		for (final AbstractOrderEntryModel abstractOrderEntryModel : orderModel.getEntries())
		{
			LOGGER.info("Product : '{}'  sourced from : '{}'", 
					abstractOrderEntryModel.getProduct().getCode(), abstractOrderEntryModel.getSapSource().getName());
		}

	}

	protected void doSourcingAndReservation(final OrderModel order) {
		try
		{
			baseSiteService.setCurrentBaseSite(order.getSite(), false);
			sourcingService.callRestService(order, false, true);
			LOGGER.info("Reservation to Backend successful");
		}
		catch (final SourcingException e)
		{
			order.setStatus(OrderStatus.SUSPENDED);
			LOGGER.error("Error when executing Sourcing and reservation",e);
			throw new SourcingException(e);
		}
		catch (final BackendDownException e)
		{
			order.setStatus(OrderStatus.SUSPENDED);
			LOGGER.error("Backend is down or not responding",e);
			throw new SourcingException(e);
		}
	}

	@Override
	protected List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> getSapCpiOrderMappers()
	{
		return sapCpiOrderMappers;
	}

	@Override
	public void setSapCpiOrderMappers(
			final List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderExchangeMappers)
	{
		this.sapCpiOrderMappers = sapCpiOrderExchangeMappers;
	}

	/**
	 * @return the sourcingService
	 */
	public SourcingService getSourcingService() {
		return sourcingService;
	}

	/**
	 * @param sourcingService the sourcingService to set
	 */
	public void setSourcingService(SourcingService sourcingService) {
		this.sourcingService = sourcingService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	/**
	 * @param baseSiteService the baseSiteService to set
	 */
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

}

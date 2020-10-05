/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.sourcing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.impl.DefaultSourcingService;


/**
 * Service  for Sourcing and Reservation to COS
 */
public class SapOaaSourcingService extends DefaultSourcingService{

	private static final Logger LOGGER = LoggerFactory.getLogger(SapOaaSourcingService.class);

	private BaseStoreService baseStoreService;
	private WarehouseService warehouseService;
	private SourcingService oaaSourcingService;
	private BaseSiteService baseSiteService;
	private ModelService modelService;

	@Override
	public SourcingResults sourceOrder(AbstractOrderModel abstractOrderModel) {
		OrderModel order = (OrderModel) abstractOrderModel;

		if (order.getSapCosSystemUsed() != null && order.getSapCosSystemUsed()) {
			doSourcingAndReservation(order);
		}

		final SourcingResults results = new SourcingResults();
		final Set<SourcingResult> sourcingResults = new HashSet<SourcingResult>();
		constructSourcingResult(order, sourcingResults);

		results.setResults(sourcingResults);
		results.setComplete(true);
		
		return results;

	}

	protected void doSourcingAndReservation(final OrderModel order) {
		try
		{
			order.setIsCosOrderAcknowledgedByBackend(Boolean.FALSE);
			baseSiteService.setCurrentBaseSite(order.getSite(), false);
			oaaSourcingService.callRestService(order, false, true);
			LOGGER.info("Reservation to Backend successful");
		}
		catch (final SourcingException e)
		{
			order.setStatus(OrderStatus.SUSPENDED);
			getModelService().save(order);
			LOGGER.error("Error when executing Sourcing and reservation",e);
			throw new SourcingException(e);
		}
	}

	protected void constructSourcingResult(final OrderModel order, final Set<SourcingResult> sourcingResults)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntryModels = new ArrayList<AbstractOrderEntryModel>();
		final SourcingResult result = new SourcingResult();
		final Map<AbstractOrderEntryModel, Long> allocation = new HashMap<AbstractOrderEntryModel, Long>();

		for (final AbstractOrderEntryModel abstractOrderEntryModel : order.getEntries())
		{
			long allocationQuantity = 0l;

			final List<ScheduleLineModel> list = abstractOrderEntryModel.getScheduleLines();
			for (final ScheduleLineModel scheduleLineModel : list)
			{
				allocationQuantity = (long) (allocationQuantity + scheduleLineModel.getConfirmedQuantity());
			}

			allocation.put(abstractOrderEntryModel, allocationQuantity);

			if (null != abstractOrderEntryModel.getProduct() && null != abstractOrderEntryModel.getSapSource()) {
				LOGGER.info("Product : '{}'  sourced from : '{}'  Quantity : '{}'",
						abstractOrderEntryModel.getProduct().getCode(),
						abstractOrderEntryModel.getSapSource().getName(), allocationQuantity);
			}
			abstractOrderEntryModels.add(abstractOrderEntryModel);
		}
		result.setAllocation(allocation);

		if (!warehouseService.getWarehouses(abstractOrderEntryModels).isEmpty())
		{
			result.setWarehouse(warehouseService.getWarehouses(abstractOrderEntryModels).get(0));
		}
		else if (!warehouseService.getDefWarehouse().isEmpty())
		{
			result.setWarehouse(warehouseService.getDefWarehouse().get(0));
		}
		else
		{
			LOGGER.warn("Could not find the virtual warehouse");
		}

		sourcingResults.add(result);
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	public SourcingService getOaaSourcingService() {
		return oaaSourcingService;
	}

	public void setOaaSourcingService(SourcingService sourcingService) {
		this.oaaSourcingService = sourcingService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}



}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultCartItemResponse;
import com.sap.retail.oaa.model.model.ScheduleLineModel;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResultHandler;


/**
 * Default Handler for COS Sourcing Results
 */
public class DefaultCosSourcingResultHandler implements CosSourcingResultHandler
{

	private static final Logger LOG = Logger.getLogger(DefaultCosSourcingResultHandler.class);


	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private GenericDao<PointOfServiceModel> pointOfServiceGenericDao;
	private ServiceUtils serviceUtils;


	/**
	 * Validates and persists the {@link SourcingResponse} to the Cart.
	 */
	@Override
	public void persistCosSourcingResultInCart(final SourcingResponse response, final AbstractOrderModel cart)
	{
		validateResponse(response);
		persistSourcingResultInCart(response, cart);

	}

	/**
	 * Persists the {@link SourcingResponse} to the Cart.
	 */
	public void persistSourcingResultInCart(final SourcingResponse response, final AbstractOrderModel cart)
	{

		cart.setSapBackendReservation(Boolean.TRUE);
		cart.setSapCosSystemUsed(Boolean.TRUE);

		for (final SourcingResultCartItemResponse item : response.getSourcingResults().getSourcingResults().get(0).getCartItems()
				.getSourcingResultCartItems())
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (null == entry.getCosOrderItemId() || !entry.getCosOrderItemId().equals(item.getExternalId()))
				{
					continue;
				}
				entry.setScheduleLines(getScheduleLinesForEntry(entry, item));

				entry.setSapBackendReservation(Boolean.TRUE);

				// DC or Store case
				if (isItemDeliveredInternally(item))
				{
					entry.setSapSource(getPosForEntry(item.getSource()));
				}
				else //Vendor case
				{

					entry.setSapSource(getPosForEntry(item.getPurchSite()));
					entry.setSapVendor(item.getSource());
				}

				modelService.save(entry);
			}
		}

		modelService.save(cart);
	}

	@Override
	public void populateScheduleLinesForPickupProduct(final AbstractOrderEntryModel entryModel)
	{
		final List<ScheduleLineModel> scheduleLines = new ArrayList<>();

		final ScheduleLineModel scheduleLine = modelService.create(ScheduleLineModel.class);
		scheduleLine.setConfirmedQuantity((double) (entryModel.getQuantity()));
		scheduleLine.setConfirmedDate(new Date());
		modelService.save(scheduleLine);
		scheduleLines.add(scheduleLine);
		entryModel.setScheduleLines(scheduleLines);

		modelService.save(entryModel);

	}

	/**
	 * Validates the sourcing response.
	 *
	 * @param sourcing
	 */
	protected void validateResponse(final SourcingResponse sourcing)
	{
		if (serviceUtils.logMessageResponseAndCheckMessageType(LOG, sourcing.getMessages()))
		{
			throw new SourcingException("Sourcing rest service has returned an error message.");
		}

		if (!hasData(sourcing))
		{
			throw new SourcingException("Sourcing rest service has not returned sufficent data.");
		}

		if (!sourcing.isReservationSuccessfulBoolean())
		{
			throw new SourcingException("Sourcing rest service could not make reservation for cart.");
		}
	}

	/**
	 * Checks if the sourcing response has sufficient data.
	 *
	 * @param sourcing
	 * @return boolean
	 */
	protected boolean hasData(final SourcingResponse sourcing)
	{
		if (sourcing.getSourcingResults() == null || sourcing.getSourcingResults().getSourcingResults() == null
				|| sourcing.getSourcingResults().getSourcingResults().isEmpty())
		{
			return false;
		}

		return hasSourcingResult(sourcing);
	}

	/**
	 * Check if the sourcing has returned result
	 *
	 * @param sourcing
	 * @return boolean
	 */
	public boolean hasSourcingResult(final SourcingResponse sourcing)
	{
		return !(sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems() == null
				|| sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems().getSourcingResultCartItems() == null
				|| sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems().getSourcingResultCartItems().isEmpty());

	}



	/**
	 * @param item
	 * @return boolean
	 */
	private boolean isItemDeliveredInternally(final SourcingResultCartItemResponse item)
	{
		return item.getPurchSite() == null || item.getPurchSite().isEmpty();
	}

	protected List<ScheduleLineModel> getScheduleLinesForEntry(final AbstractOrderEntryModel entry,
			final SourcingResultCartItemResponse item)
	{
		final List<ScheduleLineModel> scheduleLines = new ArrayList<>();
		for (final AvailabilityItemResponse availabilityItem : item.getAvailability().getAvailabilityItems())
		{
			scheduleLines.add(getScheduleLine(entry, availabilityItem));
		}
		return scheduleLines;
	}

	protected ScheduleLineModel getScheduleLine(final AbstractOrderEntryModel entry,
			final AvailabilityItemResponse availabilityItem)
	{
		final ScheduleLineModel scheduleLine = modelService.create(ScheduleLineModel.class);
		scheduleLine.setConfirmedQuantity(availabilityItem.getQuantity());
		scheduleLine.setConfirmedDate(serviceUtils.parseStringToDate(availabilityItem.getAtpDate()));
		scheduleLine.setOwner(entry);
		modelService.save(scheduleLine);
		return scheduleLine;
	}




	protected PointOfServiceModel getPosForEntry(final String source)
	{
		final Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", source);
		paramMap.put("baseStore", baseStoreService.getCurrentBaseStore());
		final List<PointOfServiceModel> posModels = getPointOfServiceGenericDao().find(paramMap);

		if (posModels == null || posModels.isEmpty())
		{
			throw new SourcingException("No Point of Service found for source: " + source + " and basestore: "
					+ baseStoreService.getCurrentBaseStore().getName());
		}

		return posModels.get(0);
	}


	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the pointOfServiceGenericDao
	 */
	public GenericDao<PointOfServiceModel> getPointOfServiceGenericDao()
	{
		return pointOfServiceGenericDao;
	}

	/**
	 * @param pointOfServiceGenericDao
	 *           the pointOfServiceGenericDao to set
	 */
	public void setPointOfServiceGenericDao(final GenericDao<PointOfServiceModel> pointOfServiceGenericDao)
	{
		this.pointOfServiceGenericDao = pointOfServiceGenericDao;
	}

	/**
	 * @return the baseStoreService
	 */

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the serviceUtils
	 */
	public ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}

	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultCartItemResponse;
import com.sap.retail.oaa.model.model.ScheduleLineModel;
import com.sap.sapoaacarintegration.services.sourcing.SourcingResultHandler;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 * Default sourcing result handler
 */
public class DefaultSourcingResultHandler implements SourcingResultHandler
{
	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private GenericDao<PointOfServiceModel> pointOfServiceGenericDao;
	private ServiceUtils serviceUtils;

	private static final Logger LOG = Logger.getLogger(DefaultSourcingResultHandler.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.sourcing.SourcingResultHandler#persistSourcingResultInCart(com.sap.retail
	 * .oaa.commerce.services.sourcing.jaxb.pojos.in.Sourcing, de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public void persistSourcingResultInCart(final SourcingResponse sourcing, final AbstractOrderModel cart,
			final RestServiceConfiguration restServiceConfig)
	{
		validateResponse(sourcing);

		cart.setSapBackendReservation(Boolean.TRUE);

		for (final SourcingResultCartItemResponse item : sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems()
				.getSourcingResultCartItems())
		{
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (!entry.getEntryNumber().toString().equals(serviceUtils.extractItemIdFromExternalId(item.getExternalId())))
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
					checkItemCategoryForDropShippment(restServiceConfig);

					entry.setSapSource(getPosForEntry(item.getPurchSite()));
					entry.setSapVendor(item.getSource());
				}

				modelService.save(entry);
			}
		}

		modelService.save(cart);
	}

	/**
	 * @param restServiceConfig
	 */
	private void checkItemCategoryForDropShippment(final RestServiceConfiguration restServiceConfig)
	{
		if (restServiceConfig.getItemCategory() == null || restServiceConfig.getItemCategory().isEmpty())
		{
			throw new SourcingException("Item category for drop shipment is not maintained in SAP base store configuration.");
		}
	}

	/**
	 * @param item
	 * @return boolean
	 */
	private boolean isItemDeliveredInternally(final SourcingResultCartItemResponse item)
	{
		return item.getPurchSite() == null || item.getPurchSite().isEmpty();
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

		if (!sourcing.isReservationSuccessfulBoolean())
		{
			throw new SourcingException("Sourcing rest service could not make reservation for cart.");
		}

		if (!hasData(sourcing))
		{
			throw new SourcingException("Sourcing rest service has not returned sufficent data.");
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
		boolean hasResult = true;
		if (sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems() == null)
		{
			hasResult = false;
		}else if (sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems().getSourcingResultCartItems() == null
				|| sourcing.getSourcingResults().getSourcingResults().get(0).getCartItems().getSourcingResultCartItems().isEmpty())
		{
			hasResult = false;
		}
		return hasResult;
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
	protected ModelService getModelService()
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
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
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
	 * @return the pointOfServiceGenericDao
	 */
	protected GenericDao<PointOfServiceModel> getPointOfServiceGenericDao()
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
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
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
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.sapoaacosintegration.services.reservation.CosReservationResultHandler;

/**
 * Default Result Handler for reservation Service.
 */
public class DefaultCosReservationResultHandler implements CosReservationResultHandler
{
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.reservation.ReservationResultHandler#updateReservation(de.hybris.platform.
	 * core.model.order.AbstractOrderModel,
	 * com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse)
	 */
	@Override
	public void updateReservation(final AbstractOrderModel order, final ReservationResponse reservationResponse)
	{
		order.setSapBackendReservation(Boolean.TRUE);
		order.setCosReservationExpireFlag(Boolean.TRUE);

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			entry.setSapBackendReservation(Boolean.TRUE);
			modelService.save(entry);
		}

		modelService.save(order);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.reservation.ReservationResultHandler#deleteReservation(de.hybris.platform.
	 * core.model.order.AbstractOrderModel)
	 */
	@Override
	public void deleteReservation(final AbstractOrderModel order)
	{
		order.setSapBackendReservation(Boolean.FALSE);
		order.setCosReservationExpireFlag(Boolean.TRUE);
		order.setCosReservationId("");

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			entry.setSapBackendReservation(Boolean.FALSE);
			modelService.save(entry);
		}

		modelService.save(order);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.reservation.ReservationResultHandler#deleteReservationItem(de.hybris.platform
	 * .core.model.order.AbstractOrderEntryModel)
	 */
	@Override
	public void deleteReservationItem(final AbstractOrderEntryModel orderEntry)
	{
		orderEntry.setSapBackendReservation(Boolean.FALSE);
		modelService.save(orderEntry);
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
}

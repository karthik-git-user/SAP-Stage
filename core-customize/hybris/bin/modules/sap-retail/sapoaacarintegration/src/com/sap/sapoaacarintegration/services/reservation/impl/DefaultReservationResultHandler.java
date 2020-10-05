/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.impl;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Default Result Handler for reservation Service.
 */
public class DefaultReservationResultHandler implements ReservationResultHandler
{
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler#updateReservation(de.hybris.platform.
	 * core.model.order.AbstractOrderModel,
	 * com.sap.sapoaacommerceservices.services.reservation.jaxb.pojos.response.ReservationResponse)
	 */
	@Override
	public void updateReservation(final AbstractOrderModel order, final ReservationResponse reservationResponse)
	{
		//Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler#deleteReservation(de.hybris.platform.
	 * core.model.order.AbstractOrderModel)
	 */
	@Override
	public void deleteReservation(final AbstractOrderModel order)
	{
		order.setSapBackendReservation(Boolean.FALSE);

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			entry.setSapBackendReservation(Boolean.FALSE);
			getModelService().save(entry);
		}

		getModelService().save(order);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler#deleteReservationItem(de.hybris.platform
	 * .core.model.order.AbstractOrderEntryModel)
	 */
	@Override
	public void deleteReservationItem(final AbstractOrderEntryModel orderEntry)
	{
		orderEntry.setSapBackendReservation(Boolean.FALSE);
		getModelService().save(orderEntry);
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

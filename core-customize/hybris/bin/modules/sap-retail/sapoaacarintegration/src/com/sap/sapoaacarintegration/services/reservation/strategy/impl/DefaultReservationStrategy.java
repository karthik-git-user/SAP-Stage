/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.strategy.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacarintegration.services.reservation.ReservationService;
import com.sap.sapoaacarintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;



/**
 * Default Implementation for ReservationStrategy
 */
public class DefaultReservationStrategy implements ReservationStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultReservationStrategy.class);

	private ReservationService reservationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy#updateReservation(de.hybris.platform
	 * .core.model.order.AbstractOrderModel, java.lang.String)
	 */
	@Override
	public ReservationResponse updateReservation(final AbstractOrderModel abstractOrderModel, final String reservationStatus)
	{
		LOG.info("Update Reservation: " + abstractOrderModel.getGuid());

		try
		{
			return getReservationService().updateReservation(abstractOrderModel, reservationStatus);
		}
		catch (final ReservationException e)
		{
			LOG.error("Could not update Reservation: " + abstractOrderModel.getGuid(), e);
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy#deleteReservation(de.hybris.platform
	 * .core.model.order.AbstractOrderModel)
	 */
	@Override
	public boolean deleteReservation(final AbstractOrderModel abstractOrderModel)
	{
		// No Reservation can be deleted return Successful
		if (!isOrderValidForDeletion(abstractOrderModel))
		{
			LOG.info("No valid order found for reservation deletion");
			return true;
		}

		LOG.info("Delete Reservation: " + abstractOrderModel.getGuid());

		try
		{
			getReservationService().deleteReservation(abstractOrderModel);
		}
		catch (final ReservationException e)
		{
			LOG.error("Could not delete Reservation: " + abstractOrderModel.getGuid(), e);
			return false;
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}

		// In the default implementation it will return true - even when the CAR is not responding
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy#deleteReservationItem(de.hybris.
	 * platform.core.model.order.AbstractOrderModel, de.hybris.platform.core.model.order.AbstractOrderEntryModel)
	 */
	@Override
	public boolean deleteReservationItem(final AbstractOrderModel abstractOrderModel,
			final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		// No Reservation or entry can be deleted return Successful
		if (!isOrderValidForDeletion(abstractOrderModel) || !isOrderEntryValidForDeletion(abstractOrderEntryModel))
		{
			LOG.info("No valid order entry found for reservation deletion");
			return true;
		}

		LOG.info("Delete Reservation Item: " + abstractOrderEntryModel.getEntryNumber().toString() + " for Reservation: "
				+ abstractOrderModel.getGuid());

		try
		{
			getReservationService().deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);
		}
		catch (final ReservationException e)
		{
			LOG.error("Could not delete Reservation Item: " + abstractOrderEntryModel.getEntryNumber().toString()
					+ " for Reservation: " + abstractOrderModel.getGuid(), e);
			return false;
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}

		// In the default implementation it will return true - even when the CAR is not responding
		return true;
	}

	protected boolean isOrderEntryValidForDeletion(final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		boolean isValid = true;
		if (abstractOrderEntryModel == null || abstractOrderEntryModel.getEntryNumber() == null
				|| abstractOrderEntryModel.getSapBackendReservation() == null
				|| abstractOrderEntryModel.getSapBackendReservation().equals(Boolean.FALSE))
		{
			isValid = false;
		}
		return isValid;
	}

	protected boolean isOrderValidForDeletion(final AbstractOrderModel abstractOrderModel)
	{
		boolean isValid = true;
		// Check Order Model & GUID
		if ((abstractOrderModel != null && StringUtils.isNotEmpty(abstractOrderModel.getGuid()))
				 && (abstractOrderModel.getSapBackendReservation()!=null && abstractOrderModel.getSapBackendReservation().equals(Boolean.TRUE))){
			isValid = false;
		}
		// Check SAP CAR Reservation

		return isValid;
	}

	/**
	 * @return the reservationService
	 */
	public ReservationService getReservationService()
	{
		return reservationService;
	}

	/**
	 * @param reservationService
	 *           the reservationService to set
	 */
	public void setReservationService(final ReservationService reservationService)
	{
		this.reservationService = reservationService;
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;


/**
 * Result Handler for reservation REST Service
 */
public interface CosReservationResultHandler
{

	/**
	 * Result Handler when reservation is deleted
	 *
	 * @param order
	 */
	void deleteReservation(AbstractOrderModel order);

	/**
	 * Result Handler when reservation item is deleted
	 *
	 * @param orderEntry
	 */
	void deleteReservationItem(AbstractOrderEntryModel orderEntry);

	/**
	 * Result Handler when reservation is updated
	 *
	 * @param order
	 * @param reservationResponse
	 */
	void updateReservation(AbstractOrderModel order, ReservationResponse reservationResponse);
}

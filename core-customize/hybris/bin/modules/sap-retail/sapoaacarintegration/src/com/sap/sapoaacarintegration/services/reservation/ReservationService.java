/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;



/**
 * Reservation Service which calls the OAA reservation service to update a reservation to an order, after the order is
 * confirmed.
 */
public interface ReservationService
{

	/**
	 * Calls the REST service to update the temporary reservation. This only updates the Status and the Order ID of the
	 * temporary reservation in the Backend, other changes which where done in the order are not changed in the
	 * reservation - new Item, changed items etc.
	 *
	 * @param abstractOrderModel
	 * The order to be used as CartModel or OrderModel during CAR Reservation.
	 * 
	 * @param reservationStatus
	 *	reservationStatus used for CAR Reservation Status
	 * 
	 * @return reservationResponse
	 */
	ReservationResponse updateReservation(final AbstractOrderModel abstractOrderModel, String reservationStatus);

	/**
	 * Deletes entire reservation in CAR.
	 *
	 * @param abstractOrderModel
	 * The order to be used as CartModel or OrderModel during CAR Reservation.
	 */
	void deleteReservation(final AbstractOrderModel abstractOrderModel);

	/**
	 * Deletes reservation entry in CAR.
	 *
	 * @param abstractOrderModel
	 * The order to be used as CartModel or OrderModel during CAR Reservation.
	 * @param abstractOrderEntryModel
	 * The order entry to be used as CartModel or OrderModel during CAR Reservation. 
	 */
	void deleteReservationItem(final AbstractOrderModel abstractOrderModel, final AbstractOrderEntryModel abstractOrderEntryModel);
}

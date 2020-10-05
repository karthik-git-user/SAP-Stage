/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;


/**
 * Reservation REST Service
 */
public interface ReservationService
{



	/**
	 * Calls the REST service to update the temporary reservation. This only updates the Status and the Order ID of the
	 * temporary reservation in the Backend, other changes which where done in the order are not changed in the
	 * reservation - new Item, changed items etc.
	 *
	 * @param abstractOrderModel
	 *           The order to be used as CartModel or OrderModel during COS Reservation.
	 * @param reservationStatus
	 *           reservationStatus used for COS Reservation Status
	 *
	 * @return reservationResponse
	 */
	ReservationResponse updateReservation(final AbstractOrderModel abstractOrderModel, String reservationStatus);

	/**
	 * Deletes entire reservation in COS.
	 *
	 * @param abstractOrderModel
	 *           The order to be used as CartModel or OrderModel during COS Reservation Delete.
	 */
	void deleteReservation(final AbstractOrderModel abstractOrderModel);

	/**
	 * Deletes reservation entry in COS.
	 *
	 * @param abstractOrderModel
	 *           The order to be used as CartModel or OrderModel during COS Reservation Delete.
	 * @param abstractOrderEntryModel
	 *           The order entry to be used as CartModel or OrderModel during COS Reservation Delete.
	 */
	void deleteReservationItem(final AbstractOrderModel abstractOrderModel, final AbstractOrderEntryModel abstractOrderEntryModel);


	/**
	 *
	 * Calls the REST service to update the temporary reservation for available Cart Items.
	 *
	 * @param order
	 *           The order to be used as CartModel or OrderModel during COS Reservation.
	 * @param reservationStatus
	 *           reservationStatus used for COS Reservation Status
	 * @param cartItemId
	 *           cartItemId Used during COS update Reservation.
	 * @return reservationResponse
	 */
	ReservationResponse updateReservationForCartItem(AbstractOrderModel order, String reservationStatus,
			String cartItemId);
}

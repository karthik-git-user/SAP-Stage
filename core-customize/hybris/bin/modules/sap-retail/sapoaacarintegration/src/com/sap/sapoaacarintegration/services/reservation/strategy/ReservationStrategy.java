/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.strategy;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;



/**
 * Strategy for Omni Channel Availability Reservations
 */
public interface ReservationStrategy
{

	/**
	 * Updates the temporary reservation in the SAP Customer Activity Repository
	 *
	 * @param abstractOrderModel
	 * @Param reservationStatus *
	 * @return ReservationResponse: PoJo with the Response of the temporary reservation REST service
	 *
	 */
	ReservationResponse updateReservation(AbstractOrderModel abstractOrderModel, String reservationStatus);

	/**
	 * Deletes the temporary reservation in the SAP Customer Activity Repository system, based on the given Order.
	 *
	 * @param abstractOrderModel
	 * @return boolean
	 *
	 */
	boolean deleteReservation(AbstractOrderModel abstractOrderModel);

	/**
	 * Deletes the temporary reservation item in the SAP Customer Activity Repository system, based on the given order
	 * and order item.
	 *
	 * @param abstractOrderModel
	 * @param abstractOrderEntryModel
	 * @return boolean
	 *
	 */
	boolean deleteReservationItem(AbstractOrderModel abstractOrderModel, AbstractOrderEntryModel abstractOrderEntryModel);

}

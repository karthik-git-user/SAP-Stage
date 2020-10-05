/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;

import de.hybris.platform.core.model.order.AbstractOrderModel;



/**
 * Request Mapper for reservation REST Service
 */
public interface ReservationRequestMapper
{

	/**
	 * Map to request structure for OAA reservation REST Service
	 *
	 * @param abstractOrderModel
	 * @param reservationStatus
	 * @param restConfiguration
	 * @return ReservationAbapRequest
	 */
	ReservationAbapRequest mapOrderModelToReservationRequest(AbstractOrderModel abstractOrderModel, String reservationStatus,
			RestServiceConfiguration restConfiguration);

}

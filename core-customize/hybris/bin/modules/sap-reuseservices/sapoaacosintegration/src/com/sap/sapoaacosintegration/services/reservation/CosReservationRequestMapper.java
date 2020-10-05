/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequest;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequestItem;



/**
 * Request Mapper for reservation REST Service
 */
public interface CosReservationRequestMapper
{

	/**
	 * Map to request structure for COS reservation REST Service
	 *
	 * @param abstractOrderModel
	 * @param cartItemId
	 * @param reservationStatus
	 * @return ReservationAbapRequest
	 */
	List<CosReservationRequestItem> mapOrderModelToReservationRequest(AbstractOrderModel abstractOrderModel,
			CosReservationRequest request, String cartItemId);

}

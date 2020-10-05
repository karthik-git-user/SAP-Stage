/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosReservationResponse
{
	private String reservationId;

	/**
	 * @return the reservationId
	 */
	@JsonGetter("reservationId")
	public String getReservationId()
	{
		return reservationId;
	}

	/**
	 * @param reservationId
	 *           the reservationId to set
	 */
	@JsonSetter("reservationId")
	public void setReservationId(final String reservationId)
	{
		this.reservationId = reservationId;
	}




}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class CosSourcingResponse
{
	private List<List<CosSourcingResponseItem>> sourcings;
	private String reservationId;

	/**
	 * @return the responseItems
	 */
	@JsonGetter("sourcings")
	public List<List<CosSourcingResponseItem>> getSourcings()
	{
		return sourcings;
	}

	/**
	 * @param responseItems
	 *           the responseItems to set
	 */
	@JsonSetter("sourcings")
	public void setSourcings(final List<List<CosSourcingResponseItem>> responseItems)
	{
		this.sourcings = responseItems;
	}

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

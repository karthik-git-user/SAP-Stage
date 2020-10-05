/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML creation
 */
public class ReservationValuesRequest
{
	private ReservationRequest reservation;

	public ReservationValuesRequest()
	{
		super();
	}

	/**
	 * @return the reservation
	 */
	@XmlElement(name = "RESERVATION")
	public ReservationRequest getReservation()
	{
		return reservation;
	}

	/**
	 * @param reservation
	 *           the reservation to set
	 */
	public void setReservation(final ReservationRequest reservation)
	{
		this.reservation = reservation;
	}


	@Override
	public String toString()
	{
		return "Values [Reservation=" + reservation.toString() + "]";
	}


}

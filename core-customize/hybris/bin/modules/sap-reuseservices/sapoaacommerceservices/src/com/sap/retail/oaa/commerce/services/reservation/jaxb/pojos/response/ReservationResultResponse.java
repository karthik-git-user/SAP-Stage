/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class ReservationResultResponse
{
	private String orderId;
	private String resvStatus;

	@XmlElement(name = "ORDER_ID")
	public String getOrderId()
	{
		return orderId;
	}

	/**
	 * @param orderId
	 *           the orderId to set
	 */
	public void setOrderId(final String orderId)
	{
		this.orderId = orderId;
	}

	@XmlElement(name = "STATUS")
	public String getResvStatus()
	{
		return resvStatus;
	}

	/**
	 * @param resvStatus
	 *           the resvStatus to set
	 */
	public void setResvStatus(final String resvStatus)
	{
		this.resvStatus = resvStatus;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Reservation [orderId=" + this.orderId + ", resvStatus=" + this.resvStatus + "]";
	}
}

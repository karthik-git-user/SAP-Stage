/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML creation
 */
public class ReservationRequest
{
	private String orderId;
	private String status;
	private String consumerId;
	private String salesChannel;

	public ReservationRequest()
	{
		super();
	}

	public ReservationRequest(final String orderId, final String status)
	{
		super();
		this.orderId = orderId;
		this.status = status;
	}

	/**
	 * @return the orderId
	 */
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

	/**
	 * @return the status
	 */
	@XmlElement(name = "STATUS")
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}

	@XmlElement(name = "CONSUMER_ID")
	public String getConsumerId()
	{
		return consumerId;
	}

	/**
	 * @param consumerId
	 *           the consumerId to set
	 */
	public void setConsumerId(final String consumerId)
	{
		this.consumerId = consumerId;
	}

	@XmlElement(name = "CHANNEL_ID")
	public String getSalesChannel()
	{
		return salesChannel;
	}

	public void setSalesChannel(final String salesChannel)
	{
		this.salesChannel = salesChannel;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ReservationRequest [orderId=" + orderId + ", status=" + status + ", consumerId= " + consumerId + ", salesChannel= "
				+ salesChannel + "]";
	}

}

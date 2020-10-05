/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class DeliveryInformationItem
{
	private String shippingMethod;
	private String latestArrivalTime;
	private String orderCutOffTime;

	@XmlElement(name = "SHIPPING_METHOD")
	public String getShippingMethod()
	{
		return shippingMethod;
	}

	/**
	 * @param shippingMethod
	 *           the name to set
	 */
	public void setShippingMethod(final String shippingMethod)
	{
		this.shippingMethod = shippingMethod;
	}

	@XmlElement(name = "LATEST_ARRIVAL_TIME")
	public String getLatestArrivalTime()
	{
		return latestArrivalTime;
	}

	/**
	 * @param latestArrivalTime
	 *           the value to set
	 */
	public void setLatestArrivalTime(final String latestArrivalTime)
	{
		this.latestArrivalTime = latestArrivalTime;
	}

	@XmlElement(name = "ORDER_CUT_OFF_TIME")
	public String getOrderCutOffTime()
	{
		return orderCutOffTime;
	}

	/**
	 * @param orderCutOffTime
	 *           the orderCutOffTime to set
	 */
	public void setOrderCutOffTime(final String orderCutOffTime)
	{
		this.orderCutOffTime = orderCutOffTime;
	}
}

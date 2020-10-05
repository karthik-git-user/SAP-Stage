/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class AvailabilityItemResponse
{
	private String atpDate;
	private double quantity;

	@XmlElement(name = "ATP_DATE")
	public String getAtpDate()
	{
		return atpDate;
	}

	/**
	 * @param atpDate
	 *           the atpDate to set
	 */
	public void setAtpDate(final String atpDate)
	{
		this.atpDate = atpDate;
	}

	@XmlElement(name = "QUANTITY", type = double.class)
	public double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final double quantity)
	{
		this.quantity = quantity;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "AvailabilityItemResponse [atpDate=" + atpDate + ", quantity=" + quantity + "]";
	}
}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.pojos;

import java.util.Date;


/**
 */
public class ATPAvailability
{

	private Double quantity;
	private Date atpDate;

	public ATPAvailability()
	{
		super();
	}

	public ATPAvailability(final Double quantity, final Date atpDate)
	{
		this.quantity = quantity;
		this.atpDate = atpDate;
	}

	/**
	 * @return the quantity
	 */
	public Double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final Double quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * @return the atpDate
	 */
	public Date getAtpDate()
	{
		return atpDate;
	}

	/**
	 * @param atpDate
	 *           the atpDate to set
	 */
	public void setAtpDate(final Date atpDate)
	{
		this.atpDate = atpDate;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[atpDate= " + this.atpDate.toString() + ", quantity=" + this.quantity.toString() + "]";
	}
}

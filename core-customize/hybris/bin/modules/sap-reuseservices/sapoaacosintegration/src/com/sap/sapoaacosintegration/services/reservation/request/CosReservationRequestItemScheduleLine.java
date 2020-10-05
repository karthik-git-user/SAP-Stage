/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.sap.sapoaacosintegration.services.atp.request.CosSource;


/**
 *
 */
@JsonInclude(Include.NON_NULL)
public class CosReservationRequestItemScheduleLine
{
	private CosSource source;
	private Date availableFrom;
	private Integer quantity;
	private String unit;

	/**
	 * @return the availableFrom
	 */
	@JsonGetter("availableFrom")
	public Date getAvailableFrom()
	{
		return availableFrom;
	}

	/**
	 * @param availableFrom
	 *           the availableFrom to set
	 */
	@JsonSetter("availableFrom")
	public void setAvailableFrom(final Date availableFrom)
	{
		this.availableFrom = availableFrom;
	}


	/**
	 * @return the quantity
	 */
	@JsonGetter("quantity")
	public Integer getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	@JsonSetter("quantity")
	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}


	/**
	 * @return the items
	 */
	@JsonGetter("unit")
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	@JsonSetter("unit")
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}


	/**
	 * @return the source
	 */
	@JsonGetter("source")
	public CosSource getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *           the source to set
	 */
	@JsonSetter("source")
	public void setSource(final CosSource source)
	{
		this.source = source;
	}

}

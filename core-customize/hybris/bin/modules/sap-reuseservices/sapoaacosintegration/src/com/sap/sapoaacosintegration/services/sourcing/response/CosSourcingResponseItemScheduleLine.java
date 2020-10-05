/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosSourcingResponseItemScheduleLine
{
	private CosSourcingResponseSource source;
	private Date availableFrom;
	private double quantity;
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

	@JsonGetter("source")
	public CosSourcingResponseSource getSource()
	{
		return source;
	}

	@JsonSetter("source")
	public void setSource(final CosSourcingResponseSource cosSourcingResponseSource)
	{
		this.source = cosSourcingResponseSource;
	}

	/**
	 * @return the quantity
	 */
	@JsonGetter
	public double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	@JsonSetter
	public void setQuantity(final double quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * @return the unit
	 */
	@JsonGetter
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	@JsonSetter
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}



}

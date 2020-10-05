/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class AvailabilityResponseQuantity
{
	private Double value;
	private String unit;

	/**
	 * @return the value
	 */
	@JsonGetter("value")
	public Double getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	@JsonSetter("value")
	public void setValue(final Double value)
	{
		this.value = value;
	}

	/**
	 * @return the unit
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
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosDestinationCoordinates
{
	private double latitude;
	private double longitude;

	/**
	 * @return the latitude
	 */
	@JsonGetter("latitude")
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * @param latitude
	 *           the latitude to set
	 */
	@JsonSetter("latitude")
	public void setLatitude(final double latitude)
	{
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	@JsonGetter("longitude")
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * @param longitude
	 *           the longitude to set
	 */
	@JsonSetter("longitude")
	public void setLongitude(final double longitude)
	{
		this.longitude = longitude;
	}


}

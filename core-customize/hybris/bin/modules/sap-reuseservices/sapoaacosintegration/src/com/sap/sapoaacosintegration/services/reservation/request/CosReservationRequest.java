/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.request;

import java.util.List;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
@JsonInclude(Include.NON_NULL)
public class CosReservationRequest
{
	private String reservationId;
	private Integer expiresInSeconds;
	private Boolean includedInAvailabilityRawDataOnNextDataUpdate;
	private HttpMethod methodType;
	private List<CosReservationRequestItem> items;

	/**
	 * @return the reservationId
	 */
	@JsonGetter("reservationId")
	public String getReservationId()
	{
		return reservationId;
	}

	/**
	 * @param reservationId
	 *           the reservationId to set
	 */
	@JsonSetter("reservationId")
	public void setReservationId(final String reservationId)
	{
		this.reservationId = reservationId;
	}

	/**
	 * @return the items
	 */
	@JsonGetter("expiresInSeconds")
	public Integer getExpiresInSeconds()
	{
		return expiresInSeconds;
	}

	/**
	 * @param expiresInSeconds
	 *           the expiresInSeconds to set
	 */
	@JsonSetter("expiresInSeconds")
	public void setExpiresInSeconds(final Integer expiresInSeconds)
	{
		this.expiresInSeconds = expiresInSeconds;
	}



	/**
	 * @return the items
	 */
	@JsonGetter("items")
	public List<CosReservationRequestItem> getItems()
	{
		return items;
	}

	/**
	 * @param items
	 *           the items to set
	 */
	@JsonSetter("items")
	public void setItems(final List<CosReservationRequestItem> items)
	{
		this.items = items;
	}



	/**
	 * @return the items
	 */
	@JsonGetter("includedInAvailabilityRawDataOnNextDataUpdate")
	public Boolean getIncludedInAvailabilityRawDataOnNextDataUpdate()
	{
		return includedInAvailabilityRawDataOnNextDataUpdate;
	}

	/**
	 * @param items
	 *           the items to set
	 */
	@JsonSetter("includedInAvailabilityRawDataOnNextDataUpdate")
	public void setIncludedInAvailabilityRawDataOnNextDataUpdate(final Boolean includedInAvailabilityRawDataOnNextDataUpdate)
	{
		this.includedInAvailabilityRawDataOnNextDataUpdate = includedInAvailabilityRawDataOnNextDataUpdate;
	}

	/**
	 * @return the items
	 */
	@JsonIgnore
	public HttpMethod getMethodType()
	{
		return methodType;
	}

	/**
	 * @param items
	 *           the items to set
	 */
	@JsonIgnore
	public void setMethodType(final HttpMethod methodType)
	{
		this.methodType = methodType;
	}





}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonInclude(Include.NON_NULL)
public class CosReservationRequestItem
{

	private String productId;
	private String context;
	private List<CosReservationRequestItemScheduleLine> scheduleLine;


	/**
	 * @return the productId
	 */
	@JsonGetter("productId")
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @param productId
	 *           the productId to set
	 */
	@JsonSetter("productId")
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @return the context
	 */
	@JsonGetter("context")
	public String getContext()
	{
		return context;
	}

	/**
	 * @param context
	 *           the context to set
	 */
	@JsonSetter("context")
	public void setContext(final String context)
	{
		this.context = context;
	}

	/**
	 * @return the scheduleLine
	 */
	@JsonGetter("scheduleLine")
	public List<CosReservationRequestItemScheduleLine> getScheduleLine()
	{
		return scheduleLine;
	}

	/**
	 * @param scheduleLine
	 *           the scheduleLine to set
	 */
	@JsonSetter("scheduleLine")
	public void setScheduleLine(final List<CosReservationRequestItemScheduleLine> scheduleLine)
	{
		this.scheduleLine = scheduleLine;
	}





}

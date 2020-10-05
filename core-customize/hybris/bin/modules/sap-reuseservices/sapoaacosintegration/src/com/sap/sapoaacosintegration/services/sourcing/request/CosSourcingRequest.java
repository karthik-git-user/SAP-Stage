/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosSourcingRequest
{
	private boolean createReservation;
	private String strategyId;

	private CosDestinationCoordinates destinationCoordinates;
	private List<CosSourcingItem> cosSourcingItems;




	/**
	 * @return the createReservation
	 */
	@JsonGetter("createReservation")
	public boolean isCreateReservation()
	{
		return createReservation;
	}

	/**
	 * @param createReservation
	 *           the createReservation to set
	 */
	@JsonSetter("createReservation")
	public void setCreateReservation(final boolean createReservation)
	{
		this.createReservation = createReservation;
	}

	/**
	 * @return the destinationCoordinates
	 */
	@JsonGetter("destinationCoordinates")
	public CosDestinationCoordinates getDestinationCoordinates()
	{
		return destinationCoordinates;
	}

	/**
	 * @param destinationCoordinates
	 *           the destinationCoordinates to set
	 */
	@JsonSetter("destinationCoordinates")
	public void setDestinationCoordinates(final CosDestinationCoordinates destinationCoordinates)
	{
		this.destinationCoordinates = destinationCoordinates;
	}

	/**
	 * @return the strategyId
	 */
	@JsonGetter("strategyId")
	public String getStrategyId()
	{
		return strategyId;
	}

	/**
	 * @param strategyId
	 *           the strategyId to set
	 */
	@JsonSetter("strategyId")
	public void setStrategyId(final String strategyId)
	{
		this.strategyId = strategyId;
	}


	/**
	 * @return the items
	 */
	@JsonGetter("items")
	public List<CosSourcingItem> getItems()
	{
		return cosSourcingItems;
	}

	/**
	 * @param cosSourcingItems
	 *           the items to set
	 */
	@JsonSetter("items")
	public void setItems(final List<CosSourcingItem> cosSourcingItems)
	{
		this.cosSourcingItems = cosSourcingItems;
	}

}

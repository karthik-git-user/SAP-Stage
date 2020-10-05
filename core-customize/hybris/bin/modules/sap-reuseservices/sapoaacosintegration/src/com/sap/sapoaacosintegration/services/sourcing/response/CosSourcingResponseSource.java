/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosSourcingResponseSource
{
	private String sourceId;
	private String sourceType;
	private CosSourcingResponseDestinationCoordinates sourceCoordinates;


	/**
	 * @return the sourceId
	 */
	@JsonGetter("sourceId")
	public String getSourceId()
	{
		return sourceId;
	}

	/**
	 * @param sourceId
	 *           the sourceId to set
	 */
	@JsonSetter("sourceId")
	public void setSourceId(final String sourceId)
	{
		this.sourceId = sourceId;
	}

	/**
	 * @return the sourceType
	 */
	@JsonGetter
	public String getSourceType()
	{
		return sourceType;
	}

	/**
	 * @param sourceType
	 *           the sourceType to set
	 */
	@JsonSetter
	public void setSourceType(final String sourceType)
	{
		this.sourceType = sourceType;
	}

	/**
	 * @return the sourceCoordinates
	 */
	@JsonGetter
	public CosSourcingResponseDestinationCoordinates getSourceCoordinates()
	{
		return sourceCoordinates;
	}

	/**
	 * @param sourceCoordinates
	 *           the sourceCoordinates to set
	 */
	@JsonSetter
	public void setSourceCoordinates(final CosSourcingResponseDestinationCoordinates sourceCoordinates)
	{
		this.sourceCoordinates = sourceCoordinates;
	}



}

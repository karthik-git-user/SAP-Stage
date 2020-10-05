/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.request;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */

@JsonInclude(Include.NON_NULL)
public class CosSource
{
	private String sourceId;
	private String sourceType;

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
	@JsonGetter("sourceType")
	public String getSourceType()
	{
		return sourceType;
	}

	/**
	 * @param sourceType
	 *           the sourceType to set
	 */
	@JsonSetter("sourceType")
	public void setSourceType(final String sourceType)
	{
		this.sourceType = sourceType;
	}





}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class AvailabilitiesResponse
{
	private List<AvailabilityItemResponse> atpAvailabilities;

	public AvailabilitiesResponse()
	{
		super();
		this.atpAvailabilities = new ArrayList<>();
	}

	@XmlElement(name = "AVAILABILITY_ITEM")
	public List<AvailabilityItemResponse> getAtpAvailabilities()
	{
		return atpAvailabilities;
	}

	/**
	 * @param atpAvailabilities
	 *           the atpAvailabilities to set
	 */
	public void setAtpAvailabilities(final List<AvailabilityItemResponse> atpAvailabilities)
	{
		this.atpAvailabilities = atpAvailabilities;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("AvailabilitiesResponse");
		strBuilder.append("[");
		if (atpAvailabilities != null && !atpAvailabilities.isEmpty())
		{
			for (final AvailabilityItemResponse atpAvailItem : atpAvailabilities)
			{
				strBuilder.append(atpAvailItem.toString());
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}
}

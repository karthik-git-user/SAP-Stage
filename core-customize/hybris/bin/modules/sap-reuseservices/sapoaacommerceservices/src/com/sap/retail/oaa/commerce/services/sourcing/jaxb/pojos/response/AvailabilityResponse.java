/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;


/**
 * Jaxb Pojo for XML reading
 */
public class AvailabilityResponse
{
	private List<AvailabilityItemResponse> availabilityItems;

	public AvailabilityResponse()
	{
		super();
		this.availabilityItems = new ArrayList<>();
	}

	@XmlElement(name = "AVAILIBILITY_ITEM")
	public List<AvailabilityItemResponse> getAvailabilityItems()
	{
		return availabilityItems;
	}

	/**
	 * @param availabilityItems
	 *           the availabilityItems to set
	 */
	public void setAvailabilityItems(final List<AvailabilityItemResponse> availabilityItems)
	{
		this.availabilityItems = availabilityItems;
	}
}

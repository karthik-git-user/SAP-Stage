/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class AdditionalInformations
{
	private List<AdditionalInformationItem> additionalInformationsList;

	public AdditionalInformations()
	{
		super();
		this.additionalInformationsList = new ArrayList<>();
	}


	@XmlElement(name = "ADDITIONAL_INFORMATION_ITEM")
	public List<AdditionalInformationItem> getAdditionalInformations()
	{
		return additionalInformationsList;
	}

	/**
	 * @param additionalInformations
	 *           the additionalInformations to set
	 */
	public void setAdditionalInformations(final List<AdditionalInformationItem> additionalInformations)
	{
		this.additionalInformationsList = additionalInformations;
	}
}

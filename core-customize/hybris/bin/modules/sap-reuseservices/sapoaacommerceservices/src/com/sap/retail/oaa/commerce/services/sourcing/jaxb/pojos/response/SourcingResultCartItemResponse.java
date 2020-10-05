/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class SourcingResultCartItemResponse
{
	private String externalId;
	private String source;
	private String purchSite;
	private AvailabilityResponse availability;
	private AdditionalInformations additionalInformations;
	private DeliveryInformation deliveryInformation;

	@XmlElement(name = "EXTERNAL_ID")
	public String getExternalId()
	{
		return externalId;
	}

	/**
	 * @param externalId
	 *           the externalId to set
	 */
	public void setExternalId(final String externalId)
	{
		this.externalId = externalId;
	}

	@XmlElement(name = "SOURCE")
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *           the source to set
	 */
	public void setSource(final String source)
	{
		this.source = source;
	}

	/**
	 * @return the purchSite
	 */
	@XmlElement(name = "PUR_SITE")
	/**
	 * @return the purchSite
	 */
	public String getPurchSite()
	{
		return purchSite;
	}

	/**
	 * @param purchSite
	 *           the purchSite to set
	 */
	public void setPurchSite(final String purchSite)
	{
		this.purchSite = purchSite;
	}

	@XmlElement(name = "AVAILABILITY")
	public AvailabilityResponse getAvailability()
	{
		return availability;
	}

	/**
	 * @param availability
	 *           the availability to set
	 */
	public void setAvailability(final AvailabilityResponse availability)
	{
		this.availability = availability;
	}


	@XmlElement(name = "ADDITIONAL_INFORMATION")
	public AdditionalInformations getAdditionalInformations()
	{
		return additionalInformations;
	}

	/**
	 * @param additionalInformations
	 *           the additionalInformations to set
	 */
	public void setAdditionalInformations(final AdditionalInformations additionalInformations)
	{
		this.additionalInformations = additionalInformations;
	}

	@XmlElement(name = "DELIVERY_INFORMATION")
	public DeliveryInformation getDeliveryInformation()
	{
		return deliveryInformation;
	}

	/**
	 * @param deliveryInformation
	 *           the deliveryInformation to set
	 */
	public void setDeliveryInformation(final DeliveryInformation deliveryInformation)
	{
		this.deliveryInformation = deliveryInformation;
	}
}

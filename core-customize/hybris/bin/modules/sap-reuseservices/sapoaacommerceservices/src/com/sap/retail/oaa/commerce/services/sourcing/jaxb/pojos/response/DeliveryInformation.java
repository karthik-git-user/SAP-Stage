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
public class DeliveryInformation
{
	private List<DeliveryInformationItem> deliveryInformationItem;

	public DeliveryInformation()
	{
		super();
		this.deliveryInformationItem = new ArrayList<>();
	}


	@XmlElement(name = "DELIVERY_INFORMATION_ITEM")
	public List<DeliveryInformationItem> getDeliveryInformationItem()
	{
		return deliveryInformationItem;
	}

	/**
	 * @param deliveryInformationItem
	 *           the additionalInformations to set
	 */
	public void setDeliveryInformationItem(final List<DeliveryInformationItem> deliveryInformationItem)
	{
		this.deliveryInformationItem = deliveryInformationItem;
	}
}

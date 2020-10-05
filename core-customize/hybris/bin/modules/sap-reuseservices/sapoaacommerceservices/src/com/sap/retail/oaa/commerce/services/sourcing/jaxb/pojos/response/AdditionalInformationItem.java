/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class AdditionalInformationItem
{
	private String name;
	private String value;

	@XmlElement(name = "NAME")
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	@XmlElement(name = "VALUE")
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}

}

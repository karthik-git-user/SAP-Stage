/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML creation
 */
public class DeliveryAddressRequest
{
	private String city1;
	private String city2;
	private String region;
	private String postCode1;
	private String poBox;
	private String street;
	private String houseNum1;
	private String country;

	public DeliveryAddressRequest()
	{
		super();
	}

	@XmlElement(name = "CITY1")
	public String getCity1()
	{
		return city1;
	}

	/**
	 * @param city1
	 *           the city1 to set
	 */
	public void setCity1(final String city1)
	{
		this.city1 = city1;
	}

	@XmlElement(name = "CITY2")
	public String getCity2()
	{
		return city2;
	}

	/**
	 * @param city2
	 *           the city2 to set
	 */
	public void setCity2(final String city2)
	{
		this.city2 = city2;
	}

	@XmlElement(name = "REGION")
	public String getRegion()
	{
		return region;
	}

	/**
	 * @param region
	 *           the region to set
	 */
	public void setRegion(final String region)
	{
		this.region = region;
	}

	@XmlElement(name = "POST_CODE1")
	public String getPostCode1()
	{
		return postCode1;
	}

	/**
	 * @param postCode1
	 *           the postCode1 to set
	 */
	public void setPostCode1(final String postCode1)
	{
		this.postCode1 = postCode1;
	}

	@XmlElement(name = "PO_BOX")
	public String getPoBox()
	{
		return poBox;
	}

	/**
	 * @param poBox
	 *           the poBox to set
	 */
	public void setPoBox(final String poBox)
	{
		this.poBox = poBox;
	}

	@XmlElement(name = "STREET")
	public String getStreet()
	{
		return street;
	}

	/**
	 * @param street
	 *           the street to set
	 */
	public void setStreet(final String street)
	{
		this.street = street;
	}

	@XmlElement(name = "HOUSE_NUM1")
	public String getHouseNum1()
	{
		return houseNum1;
	}

	/**
	 * @param houseNum1
	 *           the houseNum1 to set
	 */
	public void setHouseNum1(final String houseNum1)
	{
		this.houseNum1 = houseNum1;
	}

	@XmlElement(name = "COUNTRY")
	public String getCountry()
	{
		return country;
	}

	/**
	 * @param country
	 *           the country to set
	 */
	public void setCountry(final String country)
	{
		this.country = country;
	}

	@Override
	public String toString()
	{
		return "DeliveryAddress [city1=" + city1 + ", city2=" + city2 + ", region=" + region + ", postCode1=" + postCode1
				+ ", poBox=" + poBox + ", street=" + street + ", houseNum1=" + houseNum1 + ", country=" + country + "]";
	}
}

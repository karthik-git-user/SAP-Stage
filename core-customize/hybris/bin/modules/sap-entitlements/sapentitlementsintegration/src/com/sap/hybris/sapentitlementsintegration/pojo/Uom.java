/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public  class Uom  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	@JsonProperty("Name") 	
	private String name;

	@JsonProperty("Value") 	
	private String value;
	
	@JsonProperty("Name") 	
	public void setName(final String name)
	{
		this.name = name;
	}

	@JsonProperty("Name") 	
	public String getName() 
	{
		return name;
	}
	
	@JsonProperty("Value") 	
	public void setValue(final String value)
	{
		this.value = value;
	}

	@JsonProperty("Value") 	
	public String getValue() 
	{
		return value;
	}
	


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("data")
public  class Data  implements Serializable 
{

	/** Default serialVersionUID value. */
	private static final long serialVersionUID = 1L;

	@JsonProperty("Status") 	
	private String status;

	@JsonProperty("Response") 	
	private List<Entitlement> response;

	@JsonProperty("Status") 	
	public void setStatus(final String status)
	{
		this.status = status;
	}

	@JsonProperty("Status") 	
	public String getStatus() 
	{
		return status;
	}
	
	@JsonProperty("Response") 	
	public void setResponse(final List<Entitlement> response)
	{
		this.response = response;
	}

	@JsonProperty("Response") 	
	public List<Entitlement> getResponse() 
	{
		return response;
	}
	


}

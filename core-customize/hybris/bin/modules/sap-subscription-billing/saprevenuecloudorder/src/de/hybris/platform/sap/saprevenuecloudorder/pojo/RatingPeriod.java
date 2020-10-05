/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @deprecated since 1905.09
 */
@Deprecated(since = "1905.09", forRemoval = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingPeriod
{
	private String start;
    private String end;
  
    public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

}

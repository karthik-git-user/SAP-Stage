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
public class ConsumedQuantity
{
    private Integer value ;
    private Integer valueWithDecimals;
    private String unit;

    
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getValueWithDecimals() {
		return valueWithDecimals;
	}
	public void setValueWithDecimals(Integer valueWithDecimals) {
		this.valueWithDecimals = valueWithDecimals;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
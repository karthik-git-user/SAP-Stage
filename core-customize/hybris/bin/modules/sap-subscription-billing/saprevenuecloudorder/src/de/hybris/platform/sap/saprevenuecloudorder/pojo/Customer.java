/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @deprecated since 1905.09
 */
@Deprecated(since = "1905.09", forRemoval = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

	private String id;
	private String customerNumber;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Customer{" + "id='" + id + '\'' + '}';
	}
}

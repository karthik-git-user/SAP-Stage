/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "paymentMethod", "paymentCardToken"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment
{

	@JsonProperty("paymentMethod")
	private String paymentMethod;
	@JsonProperty("paymentCardToken")
	private String paymentCardToken;

	@JsonProperty("paymentMethod")
	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	@JsonProperty("paymentMethod")
	public void setPaymentMethod(final String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	@JsonProperty("paymentCardToken")
	public String getPaymentCardToken()
	{
		return paymentCardToken;
	}

	@JsonProperty("paymentCardToken")
	public void setPaymentCardToken(final String paymentCardToken)
	{
		this.paymentCardToken = paymentCardToken;
	}

	@Override
	public String toString() {
		return "Payment{" +
				"paymentMethod='" + paymentMethod + '\'' +
				", paymentCardToken='" + paymentCardToken + '\'' +
				'}';
	}
}

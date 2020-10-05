/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "metaData", "payment", "changedBy" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePaymentData
{

	@JsonProperty("metaData")
	private MetaData metaData;
	@JsonProperty("payment")
	private Payment payment;
	@JsonProperty("changedBy")
	private String changedBy;

	@JsonProperty("metaData")
	public MetaData getMetaData() {
		return metaData;
	}

	@JsonProperty("metaData")
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	@JsonProperty("payment")
	public Payment getPayment() {
		return payment;
	}

	@JsonProperty("payment")
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@JsonProperty("changedBy")
	public String getChangedBy() {
		return changedBy;
	}

	@JsonProperty("changedBy")
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
}
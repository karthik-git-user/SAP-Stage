/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "metaData", "requestedCancellationDate", "cancellationReason" })
public class CancelSubscription
{

	@JsonProperty("metaData")
	private MetaData metaData;
	@JsonProperty("requestedCancellationDate")
	private String requestedCancellationDate = "";
	@JsonProperty("cancellationReason")
	private String cancellationReason = "";

	@JsonProperty("metaData")
	public MetaData getMetaData() {
		return metaData;
	}

	@JsonProperty("metaData")
	public void setMetaData(final MetaData metaData) {
		this.metaData = metaData;
	}

	@JsonProperty("requestedCancellationDate")
	public String getRequestedCancellationDate() {
		return requestedCancellationDate;
	}

	@JsonProperty("requestedCancellationDate")
	public void setRequestedCancellationDate(final String requestedCancellationDate) {
		this.requestedCancellationDate = requestedCancellationDate;
	}

	@JsonProperty("cancellationReason")
	public String getCancellationReason() {
		return cancellationReason;
	}

	@JsonProperty("cancellationReason")
	public void setCancellationReason(final String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}
}

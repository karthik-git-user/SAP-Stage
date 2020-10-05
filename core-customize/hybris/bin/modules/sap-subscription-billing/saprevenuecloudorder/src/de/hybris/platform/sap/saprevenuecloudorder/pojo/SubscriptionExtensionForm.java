/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "metaData", "extensionDate", "unlimited" })
public class SubscriptionExtensionForm
{

	@JsonProperty("metaData")
	private MetaData metaData;

	@JsonProperty("extensionDate")
	private Date extensionDate;

	@JsonProperty("numberOfBillingPeriods")
	private Integer numberOfBillingPeriods;

	@JsonProperty("changedAt")
	private Date changedAt;

	@JsonProperty("changedBy")
	private String changedBy;

	@JsonProperty("unlimited")
	private Boolean unlimited;

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final MetaData metaData) {
		this.metaData = metaData;
	}

	public Date getExtensionDate()
	{
		return extensionDate;
	}

	public void setExtensionDate(final Date extensionDate)
	{
		this.extensionDate = extensionDate;
	}

	public Boolean getUnlimited()
	{
		return unlimited;
	}

	public void setUnlimited(final Boolean unlimited)
	{
		this.unlimited = unlimited;
	}

	public Integer getNumberOfBillingPeriods() {
		return numberOfBillingPeriods;
	}

	public void setNumberOfBillingPeriods(Integer numberOfBillingPeriods) {
		this.numberOfBillingPeriods = numberOfBillingPeriods;
	}

	public Date getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
}

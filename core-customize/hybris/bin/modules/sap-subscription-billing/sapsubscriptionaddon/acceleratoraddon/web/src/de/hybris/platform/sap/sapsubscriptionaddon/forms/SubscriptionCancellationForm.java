/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

import java.util.Date;

public class SubscriptionCancellationForm {

	private String cancellationReason;
	private String version;
	private String cancellationDate;
	private String ratePlanId;
	private String subscriptionEndDate;
	private Date validUntilDate;

	public Date getValidUntilDate() {
		return validUntilDate;
	}

	public void setValidUntilDate(Date validUntilDate) {
		this.validUntilDate = validUntilDate;
	}

	public String getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public void setSubscriptionEndDate(String subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}

	private String subscriptionCode;

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(String cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getRatePlanId() {
		return ratePlanId;
	}

	public void setRatePlanId(String ratePlanId) {
		this.ratePlanId = ratePlanId;
	}

}

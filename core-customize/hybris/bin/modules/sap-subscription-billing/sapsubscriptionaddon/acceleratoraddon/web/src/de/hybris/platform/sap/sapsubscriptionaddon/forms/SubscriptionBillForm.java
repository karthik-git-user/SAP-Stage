/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

public class SubscriptionBillForm {

	private String subscriptionId;
	private String toDate;
	private String fromDate;
	
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}



}

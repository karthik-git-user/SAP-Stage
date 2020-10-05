/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancellationPolicy {

	private String withdrawalPeriodEndDate;

	
	public String getWithdrawalPeriodEndDate() {
		return withdrawalPeriodEndDate;
	}


	public void setWithdrawalPeriodEndDate(String withdrawalPeriodEndDate) {
		this.withdrawalPeriodEndDate = withdrawalPeriodEndDate;
	}


	@Override
	public String toString() {
		return "CancellationPolicy{" + "withdrawalPeriodEndDate='" + withdrawalPeriodEndDate + '}';
	}
}

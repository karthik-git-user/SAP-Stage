/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

import java.util.Date;

public class SubscriptionWithdrawalForm {

	private String version;
	private Date withdrawnAt;
	private Date withdrawalPeriodEndDate;
	private String subscriptionCode;

	public Date getWithdrawnAt() {
		return withdrawnAt;
	}

	public void setWithdrawnAt(Date withdrawnAt) {
		this.withdrawnAt = withdrawnAt;
	}


	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public Date getWithdrawalPeriodEndDate() {
		return withdrawalPeriodEndDate;
	}

	public void setWithdrawalPeriodEndDate(Date withdrawalPeriodEndDate) {
		this.withdrawalPeriodEndDate = withdrawalPeriodEndDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

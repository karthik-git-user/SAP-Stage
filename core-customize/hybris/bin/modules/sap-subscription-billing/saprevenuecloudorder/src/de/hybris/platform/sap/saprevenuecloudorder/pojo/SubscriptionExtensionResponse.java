/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionExtensionResponse
{

	@JsonProperty("validUntil")
	private String validUntil;

	@JsonProperty("validUntilIsUnlimited")
	private Boolean validUntilIsUnlimited;

	public String getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(String validUntil) {
		this.validUntil = validUntil;
	}

	public Boolean isValidUntilIsUnlimited() {
		return validUntilIsUnlimited;
	}

	public void setValidUntilIsUnlimited(boolean validUntilIsUnlimited) {
		this.validUntilIsUnlimited = validUntilIsUnlimited;
	}
}

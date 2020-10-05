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
public class Market {

	private String marketId;

	private String timeZone;

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String toString() {
		return "Market{" + "id='" + marketId + '\'' + ", timeZone='" + timeZone + '\'' + '}';
	}
}

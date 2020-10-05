/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderaddon.forms;

import java.util.Map;

public class CancelServiceForm {

	private Map<Integer, Integer> cancelEntryQuantityMap;
	
	private String cancelReason;

	public Map<Integer, Integer> getCancelEntryQuantityMap()
	{
		return cancelEntryQuantityMap;
	}

	public void setCancelEntryQuantityMap(final Map<Integer, Integer> cancelEntryQuantityMap)
	{
		this.cancelEntryQuantityMap = cancelEntryQuantityMap;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	
}

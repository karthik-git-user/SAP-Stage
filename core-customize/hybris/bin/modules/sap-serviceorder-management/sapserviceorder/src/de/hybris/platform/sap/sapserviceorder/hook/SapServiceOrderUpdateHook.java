/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.hook;

import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;

public interface SapServiceOrderUpdateHook {
	
	public boolean execute(SAPCpiOutboundServiceOrderModel outboundOrder);
}

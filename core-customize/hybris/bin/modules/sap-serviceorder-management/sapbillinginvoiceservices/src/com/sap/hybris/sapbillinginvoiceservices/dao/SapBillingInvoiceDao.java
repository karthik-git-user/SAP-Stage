/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.dao;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

/**
 * Data Access Object for looking up items related to SAP Order
 *
 */
public interface SapBillingInvoiceDao {

	/**
	 * Gets Service Order By SAP Order Code
	 *
	 * @param serviceOrderCode
	 *           Service Order Code of SAPOrder
	 *
	 */
	SAPOrderModel getServiceOrderBySapOrderCode(String serviceOrderCode);

}

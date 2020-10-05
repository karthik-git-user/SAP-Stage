/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.strategy;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.List;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;


/**
 *
 */
public interface SapBillingInvoiceStrategy
{
	/**
	 * Gets Billing Documents permission
	 *
	 * @param sapOrder
	 *           SAPOrder created from commerce order
	 *
	 */
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(SAPOrderModel sapOrder);


	/**
	 * Gets PDF Data
	 *
	 * @param sapOrderData
	 *           SAPOrder created from commerce order
	 *
	 * @param billingDocId
	 *           Billing Document ID of target System
	 *
	 */
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocId) throws SapBillingInvoiceUserException;
}

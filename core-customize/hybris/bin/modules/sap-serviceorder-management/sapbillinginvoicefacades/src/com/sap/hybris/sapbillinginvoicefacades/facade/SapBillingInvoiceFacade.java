/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.facade;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;

import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;


/**
 * Facade for interacting with the SapBillingInvoiceService.
 */
public interface SapBillingInvoiceFacade {


	/**
	 * Gets Service Order from SAP Order Code
	 *
	 * @param sapOrderCode
	 *           Sap Order code from SAP Order Data
	 *           
	 * @return SAPOrderModel      
	 *
	 */
	SAPOrderModel getServiceOrderBySapOrderCode(String sapOrderCode);

	/**
	 * Gets Business Document from S/4 Cloud for service order code
	 *
	 * @param serviceOrderData
	 *           SAPOrder for commerce order
	 *           
	 * @return Map object of all the business document from s4      
	 *
	 */
	Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(SAPOrderModel serviceOrderData);

	/**
	 * Gets PDF Data
	 *
	 * @param sapOrderCode
	 *           Sap Order code of SAPOrder
	 *
	 * @param billingDocId
	 *           Billing Document ID of target System
	 *           
	 * @throws SapBillingInvoiceUserException when pdf is accessed by different USER
	 * 
	 * @return PDF Byte array 
	 *
	 */
	public byte[] getPDFData(final String sapOrderCode, final String billingDocId)
			throws SapBillingInvoiceUserException;


}



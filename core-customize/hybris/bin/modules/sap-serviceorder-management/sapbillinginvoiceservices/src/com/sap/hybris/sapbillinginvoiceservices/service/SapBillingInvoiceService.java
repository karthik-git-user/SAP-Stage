/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.service;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;

import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;


/**
 * Service for interacting with DAO and Client.
 *
 */
public interface SapBillingInvoiceService {

	/**
	 * Gets Service Order By SAP Order Code
	 *
	 * @param sapOrderCode
	 *           Service Order Code of SAPOrder
	 *
	 *	@return SAPOrderModel
	 */

	SAPOrderModel getServiceOrderBySapOrderCode(String sapOrderCode);

	/**
	 * Gets All the business Document from S/4 Cloud for service order data
	 *
	 * @param serviceOrderData
	 *           SAPOrder (Service Order) created from commerce order
	 *
	 * @return Map object of all the business document from s4    
	 */
	Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(SAPOrderModel serviceOrderData);

	/**
	 * Gets All the business Document from S/4 Cloud for sap order data
	 *
	 * @param sapOrderData
	 *           SAPOrder (Sales Order) created from commerce order
	 *
	 * @return Map object of all the business document from s4    
	 */
	Map<String, Object> getBusinessDocumentFromS4SSapOrderCode(SAPOrderModel sapOrderData);

	/**
	 * Gets PDF Data from S/4 Cloud for sap order data
	 *
	 * @param sapOrderData
	 *           SAPOrder created from commerce order
	 *
	 * @param billingDocumentId
	 *           Billing Document ID of external system
	 * 
	 * @throws SapBillingInvoiceUserException when pdf is accessed by different USER
	 * 
	 * @return PDF Byte array 
	 */
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocumentId)
			throws SapBillingInvoiceUserException;

}

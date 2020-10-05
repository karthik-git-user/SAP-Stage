/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.c4ccpiquote.constants;

/**
 * Global class for all C4ccpiquote constants. You can add global constants for your extension into this class.
 */
public final class C4ccpiquoteConstants extends GeneratedC4ccpiquoteConstants
{
	public static final String EXTENSIONNAME = "c4ccpiquote";
	public static final String PLATFORM_LOGO_CODE = "c4ccpiquotePlatformLogo";
	public static final String ORDER_PLACED_PROCESS = "sap-cpi-c4cquote-order-placed-process";
	public static final String BUYER_SUBMIT_PROCESS = "sap-cpi-c4cquote-buyer-submit-process";
	public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
	public static final String POST_CANCELLATION_PROCESS = "sap-cpi-c4c-quote-post-cancellation-process";
	public static final String SALES_ORG = "salesOrg";
	public static final String DISTRIBUTION_CHANNEL = "distChannel";
	public static final String DIVISION = "division";
	
	/*Quote Related Constants*/
	public static final String QUOTE_ACTION_CODE = "c4ccpiquote.c4cCpiOutboundQuote.actionCode";
	public static final String PREDECESSOR_INDICATOR= "c4ccpiquote.c4cCpiOutboundQuote.predecessorIndicator";
	public static final String SALES_EMPLOYEE_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR= "c4ccpiquote.c4cCpiOutboundQuote.salesEmployeePartyListCompleteTransmissionIndicator";
	public static final String OTHER_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR= "c4ccpiquote.c4cCpiOutboundQuote.otherPartyListCompleteTransmissionIndicator";
	public static final String BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_COMPLETE_TRANSMISSION_INDICATOR= "c4ccpiquote.c4cCpiOutboundQuote.businessTransactionDocumentReferenceCompleteTransmissionIndicator";
	public static final String ITEM_COMPLETE_TRANSMISSION_INDICATOR= "c4ccpiquote.c4cCpiOutboundQuote.itemCompleteTransmissionIndicator";
	
	/*Quote Item Related Constants*/
	public static final String ITEM_ACTION_CODE = "c4ccpiquote.sapC4CCpiOutboundItem.actioncode";
	public static final String SCHEDULELINE_COMPLETE_TRANSMISSION_INDICATOR =  "c4ccpiquote.sapC4CCpiOutboundItem.scheduleLineCompleteTransmissionIndicator";
	public static final String ITEM_CUSTOM_DEFINED_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR =  "c4ccpiquote.sapC4CCpiOutboundItem.ItemCustomDefinedPartyListCompleteTransmissionIndicator";
	
	/*Quote Header Comments */
	public static final String TEXT_ACTION_CODE = "c4ccpiquote.sapC4CComment.textActionCode";
	public static final String TEXT_LIST_COMPLETE_TRANSMISSION_INDICATOR = "c4ccpiquote.sapC4CComment.textListCompleteTransmissionIndicator";
	
	/*Order Notification Related Comments*/
	public static final String BUSINESS_TRANSACTION_DOCUMENT_ACTION_CODE = "c4ccpiquote.c4cSalesOrderNotification.businessTransactionDocumentActionCode";
	public static final String BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_LIST_COMPLETE_TRANSMISSION_INDICATOR = "c4ccpiquote.c4cSalesOrderNotification.businessTransactionDocumentReferenceListCompleteTransmissionIndicator";
	public static final String ITEM_LIST_COMPLETE_TRANSMISSION_INDICATOR = "c4ccpiquote.c4cSalesOrderNotification.itemListCompleteTransmissionIndicator";
	public static final String SALES_ORDER_TYPE_CODE = "c4ccpiquote.c4cSalesOrderNotification.salesOrderTypeCode";
	public static final String BUSINEES_TRANSACTION_DOCUMENT_TYPE_CODE = "c4ccpiquote.c4cSalesOrderNotification.busineesTransactionDocumentTypeCode";
	
	
	public static final String SAP_C4C_PRICECOMPONENT_ORIGIN_CODE = "c4ccpiquote.sapC4CPriceComponent.originCode";
	
	public static final String COMMEMT_TEXT_TYPE_CODE = "c4ccpiquote.quote.text.header.typecode";
	public static final String SCHEDULE_LINE_ID = "c4ccpiquote.quote.prod.schedulelineId";
	public static final String SCHEDULE_LINE_TYPE_CODE	= "c4ccpiquote.quote.prod.schedulelinetypecode";
	public static final String PARTY_ID_TYPE	= "c4ccpiquote.quote.partyIdType";
	
	
	public static final String QUOTE_CANCELLATION_CODE = "c4ccpiquote.quote.cancellationCode";




	private C4ccpiquoteConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

}

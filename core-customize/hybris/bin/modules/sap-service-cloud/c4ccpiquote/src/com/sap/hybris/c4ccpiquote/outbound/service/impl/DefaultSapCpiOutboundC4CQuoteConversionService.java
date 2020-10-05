/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.outbound.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants;
import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCommentModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundItemModel;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteConversionService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.util.Config;

/**
 * Default Implementation for SapCpiOutboundC4CQuoteConversionService
 * which is used to perform conversion operations
 */

public class DefaultSapCpiOutboundC4CQuoteConversionService implements SapCpiOutboundC4CQuoteConversionService {

	private static final int VERSION_ONE = 1;
	private B2BUnitService<B2BUnitModel, CustomerModel> b2bUnitService;
	
	
	@Override
	public SAPC4CCpiOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel model)
	{
		final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
		final SAPC4CCpiOutboundQuoteModel salesQuoteModel = new SAPC4CCpiOutboundQuoteModel();
		final AtomicInteger lineItemCounter = new AtomicInteger(0);


		salesQuoteModel.setPredecessorIndicator(Boolean.TRUE.toString());
		salesQuoteModel.setActionCode(Config.getString(C4ccpiquoteConstants.QUOTE_ACTION_CODE, StringUtils.EMPTY));
		salesQuoteModel.setItemCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.ITEM_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
		salesQuoteModel.setOtherPartyListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.OTHER_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
		salesQuoteModel.setBusinessTransactionDocumentReferenceCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
		salesQuoteModel.setSalesEmployeePartyListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.SALES_EMPLOYEE_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));


		salesQuoteModel.setName(model.getName());
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
		final Date date = new Date();
		salesQuoteModel.setSenderSequenceNumberVaue(dateFormat.format(date));
		salesQuoteModel.setBuyerID(model.getCode());
		salesQuoteModel.setCreationtime(new Date());
		salesQuoteModel.setCurrencyIsoCode(model.getCurrency().getIsocode());
		salesQuoteModel.setDistributionChannel(model.getStore().getSAPConfiguration().getSapcommon_distributionChannel());
		salesQuoteModel.setDivision(model.getStore().getSAPConfiguration().getSapcommon_division());
		salesQuoteModel.setSalesOrganization(model.getStore().getSAPConfiguration().getSapcommon_salesOrganization());

		salesQuoteModel.setProcessingTypeCode(model.getStore().getSAPConfiguration().getProcessingTypeCode());

		salesQuoteModel.setQuoteId(model.getCode());

		salesQuoteModel.setBillPartyRoleCode(PartnerRoles.BILL_TO.getCode());
		salesQuoteModel.setBuyerPartyRoleCode(PartnerRoles.SOLD_TO.getCode());
		salesQuoteModel.setProductRecepientRoleCode(PartnerRoles.SHIP_TO.getCode());

		final B2BCustomerModel customer = (B2BCustomerModel) model.getUser();
		final B2BUnitModel b2bUnitModel = getB2bUnitService().getParent(customer);
		final CompanyModel rootUnit = getB2bUnitService().getRootUnit(b2bUnitModel);
		final String partyId = rootUnit.getUid();

		salesQuoteModel.setPartyId(partyId);
		salesQuoteModel.setPartyIdType(Config.getString(C4ccpiquoteConstants.PARTY_ID_TYPE, StringUtils.EMPTY));
		salesQuoteModel.setLogicalSystemId(model.getStore().getSAPConfiguration().getLogicalSystemId());


		final Set<SAPC4CCpiOutboundItemModel> items = new HashSet<SAPC4CCpiOutboundItemModel>();
		model.getEntries().forEach(quoteItem -> {
			final SAPC4CCpiOutboundItemModel item = new SAPC4CCpiOutboundItemModel();

			item.setActionCode(Config.getString(C4ccpiquoteConstants.ITEM_ACTION_CODE, StringUtils.EMPTY));
			item.setItemCustomDefinedPartyListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.ITEM_CUSTOM_DEFINED_PARTY_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));

			if (model.getVersion().equals(VERSION_ONE))
			{
				item.setScheduleLineCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.SCHEDULELINE_COMPLETE_TRANSMISSION_INDICATOR,StringUtils.EMPTY));
			}
			else
			{
				item.setScheduleLineCompleteTransmissionIndicator(Boolean.FALSE.toString());
			}


			final String lineItemId = lineItemCounter.addAndGet(10) + "";
			item.setLineItemId(lineItemId);
			item.setDescription(quoteItem.getProduct().getDescription());
			item.setUnitCode(quoteItem.getUnit().getCode());
			item.setQuantity(quoteItem.getQuantity().toString());
			item.setProductId(quoteItem.getProduct().getSapProductID());

			item.setScheduleLineId(Config.getString(C4ccpiquoteConstants.SCHEDULE_LINE_ID, StringUtils.EMPTY));
			item.setScheduleLineTypeCode(Config.getString(C4ccpiquoteConstants.SCHEDULE_LINE_TYPE_CODE, StringUtils.EMPTY));
			item.setLogicalSystemId(model.getStore().getSAPConfiguration().getLogicalSystemId());

			items.add(item);
		});



		salesQuoteModel.setSapC4COutboundQuoteItems(items);
		
		List<SAPC4CCommentModel> comments = new ArrayList<>();
		if(null != model.getComments()) {
			
			model.getComments().forEach(comment -> {
				SAPC4CCommentModel commentModel = new SAPC4CCommentModel();
				commentModel.setTextActionCode(Config.getString(C4ccpiquoteConstants.TEXT_ACTION_CODE, StringUtils.EMPTY));
				commentModel.setTextLanguageCode(model.getStore().getDefaultLanguage().getIsocode().toUpperCase());
				commentModel.setTextListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.TEXT_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
				commentModel.setTextTypeCode(Config.getString(C4ccpiquoteConstants.COMMEMT_TEXT_TYPE_CODE, StringUtils.EMPTY));
				commentModel.setCommentText(comment.getText());
				commentModel.setCommentCode(comment.getCode());
				
				comments.add(commentModel);
				
			});
		}
		
		salesQuoteModel.setC4cComments(comments);

		return salesQuoteModel;
	}

	@Override
	public C4CSalesOrderNotificationModel convertQuoteToSalesOrderNotification(final QuoteModel quote)
	{

		final C4CSalesOrderNotificationModel salesOrderNotification = new C4CSalesOrderNotificationModel();
		salesOrderNotification.setOrderId(trimLeadingZeroes(quote.getQuoteOrderId()));
		salesOrderNotification.setC4cQuoteId(quote.getC4cQuoteExternalQuoteId());
		salesOrderNotification.setActionCode(Config.getString(C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_ACTION_CODE, StringUtils.EMPTY));
		salesOrderNotification.setBusinessTransactionDocumentReferenceListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.BUSINESS_TRANSACTION_DOCUMENT_REFERENCE_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
		salesOrderNotification.setItemListCompleteTransmissionIndicator(Config.getString(C4ccpiquoteConstants.ITEM_LIST_COMPLETE_TRANSMISSION_INDICATOR, StringUtils.EMPTY));
		salesOrderNotification.setItemTypeCode(Config.getString(C4ccpiquoteConstants.BUSINEES_TRANSACTION_DOCUMENT_TYPE_CODE, StringUtils.EMPTY));
		salesOrderNotification.setTypeCode(Config.getString(C4ccpiquoteConstants.SALES_ORDER_TYPE_CODE, StringUtils.EMPTY));
		salesOrderNotification.setSchemeId(quote.getStore().getSAPConfiguration().getLogicalSystemId());
		return salesOrderNotification;
	}
	
	
	private String trimLeadingZeroes(final String orderId)
	{

		int i = 0;
		while (i < orderId.length() && orderId.charAt(i) == '0')
		{
			i++;
		}

		final StringBuilder sb = new StringBuilder(orderId);

		sb.replace(0, i, "");

		return sb.toString();

	}
	
	/**
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, CustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, CustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}

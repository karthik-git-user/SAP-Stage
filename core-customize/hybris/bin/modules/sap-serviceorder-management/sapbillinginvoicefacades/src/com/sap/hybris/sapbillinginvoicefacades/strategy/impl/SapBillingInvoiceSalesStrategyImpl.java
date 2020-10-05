/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.strategy.impl;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.hybris.sapbillinginvoicefacades.constants.SapbillinginvoicefacadesConstants;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;


/**
 *
 */

public class SapBillingInvoiceSalesStrategyImpl implements SapBillingInvoiceStrategy
{

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceSalesStrategyImpl.class);
	private static final String BILLING_DOC_ITEM_MSG = "Unexpected Error Occured while trying to fetch billing document item from external systems ::";

	private SapBillingInvoiceService sapBillingInvoiceService;

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	private Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils()
	{
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils)
	{
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
	}

	public Converter<JsonObject, ExternalSystemBillingDocumentData> getExternalSystemBillingDocumentConverter()
	{
		return externalSystemBillingDocumentConverter;
	}

	public void setExternalSystemBillingDocumentConverter(
			final Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter)
	{
		this.externalSystemBillingDocumentConverter = externalSystemBillingDocumentConverter;
	}

	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	public SapBillingInvoiceService getSapBillingInvoiceService()
	{
		return sapBillingInvoiceService;
	}

	public void setSapBillingInvoiceService(final SapBillingInvoiceService sapBillingInvoiceService)
	{
		this.sapBillingInvoiceService = sapBillingInvoiceService;
	}

	@Override
	public List<ExternalSystemBillingDocumentData> getBillingDocuments(final SAPOrderModel sapOrder)
	{

		LOG.info("Start of Sales flow to Get of billing documents ");

		final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
		try
		{
			final Map<String, Object> billingDocumentItemMap = getSapBillingInvoiceService()
					.getBusinessDocumentFromS4SSapOrderCode(sapOrder);
			final JsonArray billingDocumentArray = (JsonArray) billingDocumentItemMap.get("billingDocumentArray");

			for (final JsonElement billingDoc : billingDocumentArray)
			{
				final ExternalSystemBillingDocumentData billingDocItemData = getExternalSystemBillingDocumentConverter()
						.convert(billingDoc.getAsJsonObject());
				billingDocItemData.setSapOrderCode(sapOrder.getCode());


				billingDocItemData.setBillingDocType(SapbillinginvoicefacadesConstants.SALES_ORDER);
				billingDocItemData.setBillingInvoiceDate(
						getSapBillingInvoiceUtils().s4DateStringToDate(billingDoc.getAsJsonObject().get("CreationDate").getAsString()));

				final String billingInvoiceNetAmount = billingDoc.getAsJsonObject().get("TransactionCurrency").getAsString() + " "
						+ billingDoc.getAsJsonObject().get("NetAmount").getAsBigDecimal()
								.add(billingDoc.getAsJsonObject().get("TaxAmount").getAsBigDecimal()).toString();

				billingDocItemData.setBillingInvoiceNetAmount(billingInvoiceNetAmount);

				billingItems.add(billingDocItemData);
			}

		}
		catch (final Exception e)
		{
			LOG.error(BILLING_DOC_ITEM_MSG, e);
		}

		LOG.info("End of Sales flow to Get of billing documents ");

		return billingItems;
	}

	@Override
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocId) throws SapBillingInvoiceUserException
	{

		return getSapBillingInvoiceService().getPDFData(sapOrderData, billingDocId);
	}

}

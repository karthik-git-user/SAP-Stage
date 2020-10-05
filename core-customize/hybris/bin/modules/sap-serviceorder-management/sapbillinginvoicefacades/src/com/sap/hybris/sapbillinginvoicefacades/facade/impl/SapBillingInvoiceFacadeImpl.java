/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.facade.impl;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoicefacades.facade.SapBillingInvoiceFacade;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;

public class SapBillingInvoiceFacadeImpl implements SapBillingInvoiceFacade{

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceFacadeImpl.class);

	private SapBillingInvoiceService sapBillingInvoiceService;

	private Map<String, SapBillingInvoiceStrategy> handlers;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	public void setHandlers(final Map<String, SapBillingInvoiceStrategy> handlers)
	{
		this.handlers = handlers;
	}

	public void registerHandler(final String stringValue, final SapBillingInvoiceStrategy handler)
	{
		handlers.put(stringValue, handler);
	}

	public void removeHandler(final String stringValue)
	{
		handlers.remove(stringValue);
	}

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils()
	{
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils)
	{
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
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
	public SAPOrderModel getServiceOrderBySapOrderCode(final String sapOrderCode)
	{
		return getSapBillingInvoiceService().getServiceOrderBySapOrderCode(sapOrderCode);
	}

	@Override
	public Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(final SAPOrderModel serviceOrderData) {
		return getSapBillingInvoiceService().getBusinessDocumentFromS4ServiceOrderCode(serviceOrderData);
	}


	@Override
	public byte[] getPDFData(final String sapOrderCode, final String billingDocId) throws SapBillingInvoiceUserException
	{
		LOG.info("Start of Get PDF facade layer");
		byte[] pdfData = new byte[0];

		final SAPOrderModel sapOrderData = getServiceOrderBySapOrderCode(sapOrderCode);
		final String orderType = getSapBillingInvoiceUtils().getSapOrderType(sapOrderData);

		if (orderType != null && !orderType.isEmpty())
		{
			final SapBillingInvoiceStrategy sapBillingInvoiceStrategy = handlers.get(orderType);
			if (sapBillingInvoiceStrategy != null)
			{
				pdfData = sapBillingInvoiceStrategy.getPDFData(sapOrderData, billingDocId);
			}
		}

		LOG.info("End of Get PDF facade layer");

		return pdfData;
	}

}

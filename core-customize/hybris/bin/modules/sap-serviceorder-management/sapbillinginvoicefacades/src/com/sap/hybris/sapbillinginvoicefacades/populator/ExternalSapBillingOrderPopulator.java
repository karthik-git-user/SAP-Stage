/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoicefacades.constants.SapbillinginvoicefacadesConstants;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * External Billing invoice order populator
 */
public class ExternalSapBillingOrderPopulator implements Populator<OrderModel, OrderData>
{

	public static final Logger LOG = Logger.getLogger(ExternalSapBillingOrderPopulator.class);

	private Map<String, SapBillingInvoiceStrategy> handlers;

	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	private ConfigurationService configurationService;


	@Override
	public void populate(final OrderModel source, final OrderData target)
	{

		final Boolean billingInvoiceEnabled = getConfigurationService().getConfiguration()
				.getBoolean(SapbillinginvoicefacadesConstants.BILLINGINVOICE_ENABLED, false);
		
		if (billingInvoiceEnabled)
		{
			LOG.info("Start of populating order details with billing documents");

			final Set<SAPOrderModel> sapOrders = source.getSapOrders();
			final List<ExternalSystemBillingDocumentData> billingItems = new ArrayList<>();
			
			for (final SAPOrderModel sapOrder : sapOrders)
			{

				final String orderType = getSapBillingInvoiceUtils().getSapOrderType(sapOrder);

				getBillingDocuments(billingItems, sapOrder, orderType);

			}

			LOG.info("End of populating order details with billing documents");

			target.setExtBillingDocuments(billingItems);

		}
		else
		{
			LOG.info("Billing Invoice not Enabled");
		}



	}

	protected void getBillingDocuments(final List<ExternalSystemBillingDocumentData> billingItems, final SAPOrderModel sapOrder,
			final String orderType) {
		if (orderType != null && !orderType.isEmpty())
		{
			final SapBillingInvoiceStrategy sapBillingInvoiceStrategy = handlers.get(orderType);
			if (sapBillingInvoiceStrategy != null)
			{
				final List<ExternalSystemBillingDocumentData> billingItemsStrategyResponse = sapBillingInvoiceStrategy
						.getBillingDocuments(sapOrder);
				billingItems.addAll(billingItemsStrategyResponse);
			}

		}
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public SapBillingInvoiceUtils getSapBillingInvoiceUtils()
	{
		return sapBillingInvoiceUtils;
	}

	public void setSapBillingInvoiceUtils(final SapBillingInvoiceUtils sapBillingInvoiceUtils)
	{
		this.sapBillingInvoiceUtils = sapBillingInvoiceUtils;
	}

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

}

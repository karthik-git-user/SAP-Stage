/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteEntryMapperService;


/**
 * Default implementation for SapCpiQuoteEntryMapperService
 *
 */
public class DefaultSapCpiQuoteEntryMapperService
		implements SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>
{

	@Override
	public void map(final AbstractOrderEntryModel quoteEntry, final SAPCpiOutboundQuoteItemModel sapCpiQuoteItem)
	{
		mapQuoteEntries(quoteEntry, sapCpiQuoteItem);
	}



	/**
	 * @param quoteEntry parameter
	 * @param sapCpiQuoteItem parameter
	 */
	private void mapQuoteEntries(final AbstractOrderEntryModel quoteEntry, final SAPCpiOutboundQuoteItemModel sapCpiQuoteItem)
	{

		sapCpiQuoteItem.setEntryNumber(quoteEntry.getEntryNumber().toString());
		sapCpiQuoteItem.setProductCode(quoteEntry.getProduct().getCode());
		sapCpiQuoteItem.setProductName(quoteEntry.getProduct().getName());
		sapCpiQuoteItem.setQuantity(quoteEntry.getQuantity().toString());

	}


}

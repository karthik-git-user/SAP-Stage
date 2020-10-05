/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;


/**
 * Provides mapping from {@link AbstractOrderEntryModel} of Quote to {@link SAPCpiOutboundQuoteItemModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface SapCpiQuoteEntryMapperService<SOURCE extends AbstractOrderEntryModel, TARGET extends SAPCpiOutboundQuoteItemModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Entry Model
	 * @param target
	 *           SAP CPI Outbound Quote Entry Model
	 */
	void map(SOURCE source, TARGET target);

}
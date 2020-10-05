/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Provides mapping from {@link QuoteModel} to {@link SAPCpiOutboundQuoteStatusModel}.
 *
 * @param <SOURCE> the parameter of the class
 * @param <TARGET> the parameter of the class
 */
public interface SapCpiQuoteStatusMapperService<SOURCE extends QuoteModel, TARGET extends SAPCpiOutboundQuoteStatusModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Model
	 * @param target
	 *           SAP CPI Outbound Quote Status Model
	 */
	void map(SOURCE source, TARGET target);

}
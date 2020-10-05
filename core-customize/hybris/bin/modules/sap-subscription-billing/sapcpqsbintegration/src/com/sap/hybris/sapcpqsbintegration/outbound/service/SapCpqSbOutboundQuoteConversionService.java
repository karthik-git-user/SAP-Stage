/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.outbound.service;

import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Sap cpq sb outbound quote conversion service
 */
public interface SapCpqSbOutboundQuoteConversionService
{
	/**
	 * create subscription pricing request
	 * @param quoteModel quote model
	 * @return created subscription pricing request
	 */
	SubscriptionPricingOutboundModel createSubscriptionPricingRequest(QuoteModel quoteModel);

}

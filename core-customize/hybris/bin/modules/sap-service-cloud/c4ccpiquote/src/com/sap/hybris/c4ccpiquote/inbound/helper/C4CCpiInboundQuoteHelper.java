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
package com.sap.hybris.c4ccpiquote.inbound.helper;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 *
 */
public interface C4CCpiInboundQuoteHelper
{

	QuoteModel processSalesInboundQuote(QuoteModel inbounbQuote);

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound.helper;

import de.hybris.platform.core.model.order.QuoteModel;

public interface CpqInboundQuoteHelper {

	QuoteModel processInboundQuote(QuoteModel inboundQuote);
}

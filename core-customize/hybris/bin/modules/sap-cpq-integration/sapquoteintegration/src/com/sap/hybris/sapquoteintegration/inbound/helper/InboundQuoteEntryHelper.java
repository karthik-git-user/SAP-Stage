/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.inbound.helper;

import de.hybris.platform.core.model.order.QuoteEntryModel;

public interface InboundQuoteEntryHelper {

	QuoteEntryModel processInboundQuoteEntry(QuoteEntryModel inboundQuoteEntry);
	

}
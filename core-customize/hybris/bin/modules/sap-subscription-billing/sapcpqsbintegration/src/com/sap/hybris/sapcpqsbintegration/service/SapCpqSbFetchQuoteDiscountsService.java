package com.sap.hybris.sapcpqsbintegration.service;

import de.hybris.platform.core.model.order.QuoteModel;

public interface SapCpqSbFetchQuoteDiscountsService {
    public QuoteModel getCurrentQuoteVendorDiscounts(String QuoteId);
}

package com.sap.hybris.sapcpqsbintegration.service.impl;

import com.sap.hybris.sapcpqsbintegration.service.SapCpqSbFetchQuoteDiscountsService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultSapCpqSbFetchQuoteDiscountsService implements SapCpqSbFetchQuoteDiscountsService {

    private FlexibleSearchService flexibleSearchService;
    protected static final String QUOTES_QUERY = "SELECT {quote:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
            + " as quote}";
    protected static final String WHERE_CODE_CLAUSE = " WHERE {quote:" + QuoteModel.CODE + "}=?code AND {quote:" + QuoteModel.STATE + "}=?quoteStates";
    protected static final String ORDER_BY_VERSION_DESC = " ORDER BY {quote:" + QuoteModel.VERSION + "} DESC";


    @Override
    public QuoteModel getCurrentQuoteVendorDiscounts(String quoteId) {

        validateParameterNotNullStandardMessage("quoteId", quoteId);

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(
                QUOTES_QUERY + WHERE_CODE_CLAUSE + ORDER_BY_VERSION_DESC);
        searchQuery.addQueryParameter("code", quoteId);
        searchQuery.addQueryParameter("quoteStates", QuoteState.BUYER_OFFER);
        searchQuery.addQueryParameter("version",1);
        searchQuery.setCount(1);

        return getFlexibleSearchService().searchUnique(searchQuery);
    }


    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}

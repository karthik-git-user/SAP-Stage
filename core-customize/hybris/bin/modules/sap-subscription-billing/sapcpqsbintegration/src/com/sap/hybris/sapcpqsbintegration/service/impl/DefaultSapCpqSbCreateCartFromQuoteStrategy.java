package com.sap.hybris.sapcpqsbintegration.service.impl;

import de.hybris.platform.commerceservices.order.impl.CommerceCreateCartFromQuoteStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.Random;

public class DefaultSapCpqSbCreateCartFromQuoteStrategy extends CommerceCreateCartFromQuoteStrategy {
    @Override
    protected void postProcess(final QuoteModel original, final CartModel copy) {

        super.postProcess(original, copy);
            //Accept and Check out
            copy.getEntries().forEach(entry -> {
                if(!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails()) && entry.getProduct().getSubscriptionCode() != null) {
                    entry.getCpqSubscriptionDetails().forEach(discountEntry -> {
                        discountEntry.setOneTimeChargeEntries(null);
                        discountEntry.setRecurringChargeEntries(null);
                        discountEntry.setUsageCharges(null);
                    });
                }
            });
    }
}

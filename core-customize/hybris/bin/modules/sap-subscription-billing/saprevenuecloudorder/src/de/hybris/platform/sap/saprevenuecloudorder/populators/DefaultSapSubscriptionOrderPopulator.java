/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import org.apache.log4j.Logger;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;

public class DefaultSapSubscriptionOrderPopulator implements Populator<OrderModel, OrderData> {
    private static final Logger LOG = Logger.getLogger(DefaultSapSubscriptionOrderPopulator.class);

    @Override
    public void populate(OrderModel source, OrderData target) throws ConversionException {
        boolean onlySubscription = source.getEntries().stream().allMatch(oe -> {
            String subscriptionCode = oe.getProduct().getSubscriptionCode();
            return StringUtils.isNotBlank(subscriptionCode);
        });

        if (onlySubscription) {
            target.setCancellable(false);
        }
        LOG.info("Start of Setting the returnable/Cancellable quantity to zero for Subscription order");
        for (final OrderEntryData entry : target.getEntries()) {
            String subscriptionCode = entry.getProduct().getSubscriptionCode();
            if (StringUtils.isNotBlank(subscriptionCode)) {
                entry.setReturnableQty(0);
                entry.setCancellableQty(0);
            }
        }

    }

}

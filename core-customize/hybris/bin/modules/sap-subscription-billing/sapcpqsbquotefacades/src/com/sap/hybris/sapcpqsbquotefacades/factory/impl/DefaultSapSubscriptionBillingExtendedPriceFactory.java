/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.factory.impl;

import com.sap.hybris.sapcpqsbquotefacades.factory.SapSubscriptionBillingExtendedPriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.subscriptionservices.jalo.ExtendedCatalogAwareEurope1PriceFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Date;

public class DefaultSapSubscriptionBillingExtendedPriceFactory extends ExtendedCatalogAwareEurope1PriceFactory
		implements SapSubscriptionBillingExtendedPriceFactory {

	@Override
	public PriceRow getPriceRow(Product product, Date effectiveDate) throws JaloPriceFactoryException {

		final SessionContext ctx = getSession().getSessionContext();
		final User user = ctx.getUser();
		final Currency currency = ctx.getCurrency();
		final EnumerationValue ppg = getPPG(ctx, product);
		final EnumerationValue upg = getUPG(ctx, user);
		final boolean net = true;
		Date date = new Date();
		if(effectiveDate == null || effectiveDate.compareTo(date) < 0){
			effectiveDate = date;
		}

		final Collection<PriceRow> priceRows = filterPriceRows(
				matchPriceRowsForInfo(ctx, product, ppg, user, upg, currency, effectiveDate, net));

		if (CollectionUtils.isNotEmpty(priceRows)) {
			return priceRows.iterator().next();
		} else {
			return null;
		}
	}

}

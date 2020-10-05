/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.service.impl;

import com.sap.hybris.sapcpqsbquotefacades.factory.SapSubscriptionBillingExtendedPriceFactory;
import com.sap.hybris.sapcpqsbquotefacades.service.SapSubscriptionBillingEffectivePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.impl.DefaultSubscriptionCommercePriceService;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Default implementation of {@link SapSubscriptionBillingEffectivePriceService}
 */
public class DefaultSapSubscriptionBillingEffectivePriceService extends DefaultSubscriptionCommercePriceService
		implements SapSubscriptionBillingEffectivePriceService {

	private static final Logger LOG = Logger.getLogger(DefaultSapSubscriptionBillingEffectivePriceService.class);

	private SapSubscriptionBillingExtendedPriceFactory sapSubscriptionBillingExtendedPriceFactory;


	@Override
	public SubscriptionPricePlanModel getSubscriptionEffectivePricePlan(final ProductModel subscriptionProduct,
			Date effectiveDate) {
		final Product productItem = getModelService().getSource(subscriptionProduct);
		final PriceRow priceRowItem;

		try {
			priceRowItem = this.sapSubscriptionBillingExtendedPriceFactory.getPriceRow(productItem, effectiveDate);
		} catch (final JaloPriceFactoryException e) {
			throw new SystemException(e.getMessage(), e);
		}

		if (priceRowItem == null) {
			return null;
		}

		final PriceRowModel priceRow = getModelService().get(priceRowItem);

		if (priceRow instanceof SubscriptionPricePlanModel) {
			LOG.debug("Found subscription price row: " + ((SubscriptionPricePlanModel) priceRow).getName());
			return (SubscriptionPricePlanModel) priceRow;
		} else {
			LOG.info("Found no subscription price plan for product: " + subscriptionProduct.getCode());
			return null;
		}
	}

	public void setSapSubscriptionBillingExtendedPriceFactory(
			SapSubscriptionBillingExtendedPriceFactory sapSubscriptionBillingExtendedPriceFactory) {
		this.sapSubscriptionBillingExtendedPriceFactory = sapSubscriptionBillingExtendedPriceFactory;
	}

}

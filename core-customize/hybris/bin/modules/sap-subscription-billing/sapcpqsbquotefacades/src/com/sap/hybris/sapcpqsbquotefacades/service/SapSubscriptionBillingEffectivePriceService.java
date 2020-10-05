/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

import java.util.Date;

/**
 * Extension of {@link SubscriptionCommercePriceService}
 */
public interface SapSubscriptionBillingEffectivePriceService extends SubscriptionCommercePriceService{

	/**
	 * Get subscription effective price plan
	 * @param productModel product model
	 * @param effectiveDate effective date
	 * @return subscription effective price plan
	 */
	SubscriptionPricePlanModel getSubscriptionEffectivePricePlan(ProductModel productModel, Date effectiveDate);

}

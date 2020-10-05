/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.factory;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.subscriptionservices.jalo.ExtendedPriceFactory;

import java.util.Date;

public interface SapSubscriptionBillingExtendedPriceFactory extends ExtendedPriceFactory{
	
	PriceRow getPriceRow(Product product, Date effectiveDate) throws JaloPriceFactoryException;



}

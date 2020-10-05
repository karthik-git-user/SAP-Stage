/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.subscriptionfacades.converters.populator.SearchResultSubscriptionProductPopulator;


/**
 * SOLR Populator for subscription-capable {@link ProductModel}.
 *
 * @param <SOURCE>
 *           source class
 * @param <TARGET>
 *           target class
 */
public class SapRevenueCloudSearchResultsSubscriptionProductPopulator<SOURCE extends SearchResultValueData, TARGET extends ProductData>
		extends SearchResultSubscriptionProductPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		super.populate(source, target);
		target.setSubscriptionCode(getValue(source, "subscriptionCode"));
	}

}

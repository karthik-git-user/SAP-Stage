package com.sap.hybris.saprevenuecloudproduct.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SapSubscriptionPricePlanWithEffectiveDateDao {
    Optional<SubscriptionPricePlanModel>  getSubscriptionPricesWithEffectiveDate(final String pricePlanId,
                                                                                 final CatalogVersionModel catalogVersion, final Date effectiveAt);

    Optional<SubscriptionPricePlanModel> getSubscriptionPricesWithEffectiveDate(final String pricePlanId, final List<CatalogVersionModel> catalogVersions, final Date effectiveAt);
}

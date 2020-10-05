/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.service;

import java.util.Date;
import java.util.List;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;


/**
 * Sap Reveunue Cloud Product Service interface
 */
public interface SapRevenueCloudProductService
{

	/**
	 * get the Subscription price for a specific price plan ID
	 *
	 * @param pricePlanId
	 *           - price plan ID for the {@link SubscriptionPricePlanModel}
	 * @param catalogVersion
	 *           - catalog Version for the {@link SubscriptionPricePlanModel}          
	 *           
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	SubscriptionPricePlanModel getSubscriptionPricePlanForId(final String pricePlanId, CatalogVersionModel catalogVersion);
	
	/**
	 * Get the {@link SAPRatePlanElementModel} from {@code id}
	 * @param label
	 * 			- label of {@link SAPRatePlanElementModel }
	 * @return
	 * 		ratePlan element
	 */
	SAPRatePlanElementModel getRatePlanElementfromId(final String label);
	
	/**
	 * 
	 * Get {@link UsageUnitModel} from {@code id}
	 * @param id
	 * 			- id of {@link UsageUnitModel }
	 * @return
	 * 		usage unit
	 */
	UsageUnitModel getUsageUnitfromId(final String id);
	
	/**
	 * Get last success run date for Cronjob 
	 * @param code
	 * 			- code of {@link CronJobModel }
	 * @return
	 * 		{@link Date}
	 */
	
	Date getProductReplicationDateForCronjob(final String code);

	/**
	 * get the Subscription price for a specific price plan ID and EffectiveAt date
	 *
	 * @param pricePlanId
	 *           - price plan ID for the {@link SubscriptionPricePlanModel}
	 * @param catalogVersion
	 *           - catalog Version for the {@link SubscriptionPricePlanModel}
	 * @param effectiveAt
	 * 			- Effective date of the priceplan Module {@link SubscriptionPricePlanModel}
	 *
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	SubscriptionPricePlanModel getSubscriptionPricesWithEffectiveDate(final String pricePlanId, CatalogVersionModel catalogVersion,final Date effectiveAt);

	/**
	 * get the Subscription price for a specific price plan ID and EffectiveAt date
	 *
	 * @param pricePlanId
	 *           - price plan ID for the {@link SubscriptionPricePlanModel}
	 * @param catalogVersions
	 *           - catalog Versions for the {@link SubscriptionPricePlanModel}
	 * @param effectiveAt
	 * 			- Effective date of the priceplan Module {@link SubscriptionPricePlanModel}
	 *
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	SubscriptionPricePlanModel getSubscriptionPricesWithEffectiveDate(final String pricePlanId, final List<CatalogVersionModel> catalogVersions, final Date effectiveAt);
}

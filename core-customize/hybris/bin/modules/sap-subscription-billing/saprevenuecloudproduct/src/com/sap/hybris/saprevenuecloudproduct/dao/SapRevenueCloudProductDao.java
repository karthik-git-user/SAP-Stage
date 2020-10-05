/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Date;
import java.util.Optional;


/**
 * SAP Revenue Cloud Product DAO interface
 */
public interface SapRevenueCloudProductDao
{

	/**
	 * Returns the {@link SubscriptionPricePlanModel} for a specific pricePlan ID
	 *
	 * @param pricePlanId
	 *           - price Plan id for the subscription price plan
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	Optional<SubscriptionPricePlanModel> getSubscriptionPricePlanForId(final String pricePlanId,
			CatalogVersionModel catalogVersion);
	
	/**
	 * Get the last success time for the cronjob
	 * 
	 * @param code
	 * 			- code for {@link CronJobModel }
	 * @return
	 * 		{@link CronJobHistoryModel}
	 */
	public CronJobHistoryModel getLastSuccessRunForCronjob(String  code);

}

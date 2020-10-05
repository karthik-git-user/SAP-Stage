/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy;
import de.hybris.platform.commerceservices.strategies.PickupStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 * Default Pick up Strategy
 */
public class DefaultSapOaaPickupAvailabilityStrategy implements PickupAvailabilityStrategy
{

	private PickupStrategy pickupStrategy;
	private CommerceStockService commerceStockService;

	protected PickupStrategy getPickupStrategy()
	{
		return pickupStrategy;
	}


	public void setPickupStrategy(final PickupStrategy pickupStrategy)
	{
		this.pickupStrategy = pickupStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy#isPickupAvailableForProduct(de.hybris.
	 * platform.core.model.product.ProductModel, de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public Boolean isPickupAvailableForProduct(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (!PickupInStoreMode.DISABLED.equals(getPickupStrategy().getPickupInStoreMode()))
		{
			validateParameterNotNull(baseStore, "baseStore must not be null");
			if (CollectionUtils.isNotEmpty(baseStore.getPointsOfService()))
			{
				return isProductAvailableInAnyStore(product, baseStore);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * @param product
	 * @param baseStore
	 */
	public Boolean isProductAvailableInAnyStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		final Map<PointOfServiceModel, StockLevelStatus> stockLevelStatusMap = commerceStockService
				.getPosAndStockLevelStatusForProduct(product, baseStore);

		final Iterator iter = stockLevelStatusMap.entrySet().iterator();
		while (iter.hasNext())
		{
			final Map.Entry<PointOfServiceModel, StockLevelStatus> entry = (Entry<PointOfServiceModel, StockLevelStatus>) iter
					.next();
			if (entry.getValue().equals(StockLevelStatus.INSTOCK))
			{
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}
}

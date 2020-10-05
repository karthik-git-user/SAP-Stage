/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.stock.impl;


import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;


/**
 * Provides stock level for COS use-cases
 */
public class DefaultSapCosOaaCommerceStockService extends DefaultCommerceStockService implements SapOaaCommerceStockService
{

	private static final Logger LOG = Logger.getLogger(DefaultSapCosOaaCommerceStockService.class);

	private ATPService atpService;
	private PointOfServiceDao pointOfServiceDao;
	private ATPAggregationStrategy atpAggregationStrategy;

	/**
	 * fetches {@link StockLevelStatus} for a {@link ProductModel} and {@link BaseStoreModel}
	 *
	 * @param product
	 * @param baseStore
	 * @return {@link Long}
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		final StockLevelStatus stockLevelStatus = this.getStockLevelStatusForProductAndBaseStore(product, baseStore);

		return getStockLevel(stockLevelStatus, product, baseStore);
	}


	/**
	 * @param product
	 * @param baseStore
	 * @return {@link StockLevelStatus}
	 */
	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		return checkStockLevelStatus(getAvailableStockLevel(null, null, product, null));

	}


	@Override
	public StockData getStockDataForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore,
			final StockData stockData)
	{
		final Long stockValue = getAvailableStockLevel(null, null, product, null);
		final StockLevelStatus stockLevelStatus = checkStockLevelStatus(stockValue);
		stockData.setStockLevelStatus(stockLevelStatus);
		stockData.setStockLevel(getStockLevel(stockLevelStatus, product, baseStore));
		return stockData;

	}

	@Override
	public StockLevelStatus getStockLevelStatusForProductAndPointOfService(final ProductModel product,
			final PointOfServiceModel pointOfService)
	{
		if (this.getStockLevelForProductAndPointOfService(product, pointOfService).longValue() > 0)
		{
			return StockLevelStatus.INSTOCK;
		}
		else
		{
			return StockLevelStatus.OUTOFSTOCK;
		}
	}

	@Override
	public Long getStockLevelForProductAndPointOfService(final ProductModel product, final PointOfServiceModel pointOfService)
	{
		long availableStock = 0;

		try
		{
			final List<ATPAvailability> productAvailabilityList = getAvailabilityForProductAndSource(product,
					pointOfService.getName());

			if (productAvailabilityList != null && !productAvailabilityList.isEmpty())
			{
				availableStock = atpAggregationStrategy.aggregateAvailability(null, product, null, productAvailabilityList)
						.longValue();
			}
		}
		catch (final ATPException | BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return Long.valueOf(availableStock);
	}

	protected Long getStockLevel(final StockLevelStatus stockLevelStatus, final ProductModel product,
			final BaseStoreModel baseStore)
	{

		if (stockLevelStatus.equals(StockLevelStatus.INSTOCK))
		{
			return null;
		}

		if (stockLevelStatus.equals(StockLevelStatus.OUTOFSTOCK))
		{
			return Long.valueOf(0);
		}

		if (stockLevelStatus.equals(StockLevelStatus.LOWSTOCK))
		{
			return getAvailableStockLevel(null, null, product, null);

		}
		return Long.valueOf(0);
	}

	/**
	 * @param stockLevel
	 * @return {@link StockLevelStatus}
	 */

	private StockLevelStatus checkStockLevelStatus(final Long stockLevel)
	{
		if (stockLevel > 0)
		{
			return StockLevelStatus.INSTOCK;
		}
		else
		{
			return StockLevelStatus.OUTOFSTOCK;
		}

	}


	@Override
	public List<ATPAvailability> getAvailabilityForProduct(final String cartGuid, final String itemId, final ProductModel product)
	{
		return atpService.callRestAvailabilityServiceForProduct(cartGuid, itemId, product);
	}


	@Override
	public List<ATPAvailability> getAvailabilityForProductAndSource(final ProductModel product, final String source)
	{
		return atpService.callRestAvailabilityServiceForProductAndSource(product, source);
	}


	@Override
	public List<ATPAvailability> getAvailabilityForProductAndSource(final String cartGuid, final String itemId,
			final ProductModel product, final String source)
	{
		return atpService.callRestAvailabilityServiceForProductAndSource(cartGuid, itemId, product, source);
	}


	@Override
	public List<ATPProductAvailability> getAvailabilityForProducts(final String cartGuid, final String itemId,
			final String productUnit, final List<ProductModel> productList)
	{
		//As hybris does not support batch mode, we pass only single item id and single product unit
		final List<String> itemIdList = new ArrayList<>();
		itemIdList.add(itemId);

		return atpService.callRestAvailabilityServiceForProducts(cartGuid, itemIdList, productUnit, productList);
	}


	@Override
	public List<ATPProductAvailability> getAvailabilityForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList)
	{
		return atpService.callRestAvailabilityServiceForProductAndSources(cartGuid, itemId, product, sourcesList);
	}

	@Override
	public Map<PointOfServiceModel, StockLevelStatus> getPosAndStockLevelStatusForProduct(final ProductModel product,
			final BaseStoreModel baseStore)
	{
		final List<String> sourcesList = new ArrayList<>();
		for (final PointOfServiceModel pointOfService : baseStore.getPointsOfService())
		{
			if (pointOfService.getType().equals(PointOfServiceTypeEnum.STORE))
			{
				sourcesList.add(pointOfService.getName());
			}
		}

		final Map<PointOfServiceModel, StockLevelStatus> stockLevelStatusMap = new HashedMap();

		try
		{
			final List<ATPProductAvailability> availabilities = atpService.callRestAvailabilityServiceForProductAndSources(null,
					null, product, sourcesList);

			for (final ATPProductAvailability availability : availabilities)
			{
				final PointOfServiceModel pointOfService = determinePointOfService(availability.getSourceId());


				if (getAtpAggregationStrategy()
						.aggregateAvailability(null, product, pointOfService, availability.getAvailabilityList()).longValue() > 0
						&& pointOfService != null)
				{
					stockLevelStatusMap.put(pointOfService, StockLevelStatus.INSTOCK);
				}
				else
				{
					stockLevelStatusMap.put(pointOfService, StockLevelStatus.OUTOFSTOCK);
				}
			}
		}
		catch (final ATPException e)
		{
			LOG.error("Error when fetching stock level information form COS", e);
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}


		return stockLevelStatusMap;
	}

	@Override
	public Long getAvailableStockLevelForPos(final String cartGuid, final ProductModel productModel,
			final PointOfServiceModel pointOfService)
	{
		long availableStock = 0;

		final List<ATPAvailability> productAvailabilityList = getAvailabilityForProductAndSource(productModel,
				pointOfService.getName());

		if (productAvailabilityList != null && !productAvailabilityList.isEmpty())
		{
			availableStock = atpAggregationStrategy
					.aggregateAvailability(null, productModel, pointOfService, productAvailabilityList).longValue();
		}

		return Long.valueOf(availableStock);
	}


	@Override
	public StockLevelModel getStockLevelForRSI(final ProductModel product, final BaseStoreModel baseStore)
	{
		final Collection<StockLevelModel> stockLevels = getStockService().getStockLevels(product,
				getWarehouseSelectionStrategy().getWarehousesForBaseStore(baseStore));

		if (stockLevels.isEmpty())
		{
			return null;
		}

		return stockLevels.iterator().next();

	}


	@Override
	public Long getAvailableStockLevel(final String cartGuid, final String itemId, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel)
	{
		long availableStock = 0;

		List<ATPAvailability> productAvailabilityList;

		if (pointOfServiceModel == null)
		{
			productAvailabilityList = atpService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
		}
		else
		{
			productAvailabilityList = atpService.callRestAvailabilityServiceForProductAndSource(cartGuid, itemId, productModel,
					pointOfServiceModel.getName());
		}

		if (productAvailabilityList != null && !productAvailabilityList.isEmpty())
		{
			availableStock = atpAggregationStrategy
					.aggregateAvailability(cartGuid, productModel, pointOfServiceModel, productAvailabilityList).longValue();
		}
		return Long.valueOf(availableStock);
	}


	/**
	 * @return the atpService
	 */
	public ATPService getAtpService()
	{
		return atpService;
	}


	/**
	 * @param atpService
	 *           the atpService to set
	 */
	public void setAtpService(final ATPService atpService)
	{
		this.atpService = atpService;
	}


	/**
	 * @return the pointOfServiceDao
	 */
	public PointOfServiceDao getPointOfServiceDao()
	{
		return pointOfServiceDao;
	}


	/**
	 * @param pointOfServiceDao
	 *           the pointOfServiceDao to set
	 */
	public void setPointOfServiceDao(final PointOfServiceDao pointOfServiceDao)
	{
		this.pointOfServiceDao = pointOfServiceDao;
	}


	/**
	 * @return the atpAggregationStrategy
	 */
	public ATPAggregationStrategy getAtpAggregationStrategy()
	{
		return atpAggregationStrategy;
	}


	/**
	 * @param atpAggregationStrategy
	 *           the atpAggregationStrategy to set
	 */
	public void setAtpAggregationStrategy(final ATPAggregationStrategy atpAggregationStrategy)
	{
		this.atpAggregationStrategy = atpAggregationStrategy;
	}

	/**
	 * @param sourceId
	 * @return pointOfServiceModel
	 */
	private PointOfServiceModel determinePointOfService(final String sourceId)
	{
		return pointOfServiceDao.getPosByName(sourceId);
	}

}

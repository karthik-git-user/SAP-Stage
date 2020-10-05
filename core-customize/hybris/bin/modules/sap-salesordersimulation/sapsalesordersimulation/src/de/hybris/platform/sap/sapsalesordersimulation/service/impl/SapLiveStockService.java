/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.ArrayList;
import java.util.Collection;


import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.impl.SapProductAvailabilityCache;
import de.hybris.platform.sap.sapsalesordersimulation.service.AvailabilityService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.store.services.BaseStoreService;
/**
 * Implementation for overriding the default stock levels
 */
public class SapLiveStockService extends DefaultStockService {
	private SalesOrderSimulationService salesOrderSimulationService;
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private BaseStoreService baseStoreService;
	

	private SapProductAvailabilityCache sapProductAvailabilityCache;
	private UserService userService;
	private B2BUnitService b2bUnitService;
	private AvailabilityService availabilityService;

	@Override
	public StockLevelStatus getProductStatus(ProductModel product, WarehouseModel warehouse) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			Collection<WarehouseModel> warehouses = new ArrayList<>();
			 warehouses.add(warehouse);
			return getStockStatus (getAvailabilityService().getStockAvailability(product, warehouses) );
		}
		return super.getProductStatus(product, warehouse);
	}
	
	@Override
	public StockLevelStatus getProductStatus(ProductModel product, Collection<WarehouseModel> warehouses) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			return getStockStatus (getAvailabilityService().getStockAvailability(product, warehouses) );
		}
		return super.getProductStatus(product, warehouses);
	}
	

	private StockLevelStatus getStockStatus(Collection<StockLevelModel> availability) {
		for (StockLevelModel stockLevelModel : availability) {
			if (stockLevelModel.getAvailable()>0) {
				return StockLevelStatus.INSTOCK;
			}
			
		}
		return StockLevelStatus.OUTOFSTOCK;
	}


	@Override
	public Collection<StockLevelModel> getStockLevels(ProductModel product, Collection<WarehouseModel> warehouses) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			return getAvailabilityService().getStockAvailability(product, warehouses) ;
		}  else {
			return super.getStockLevels(product,warehouses);
		}
		
	}



	@Override
	public StockLevelModel getStockLevel(ProductModel product, WarehouseModel warehouse) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			Collection<WarehouseModel> warehouses = new ArrayList<>();
			 warehouses.add(warehouse);
			 
			return  getAvailabilityService().getStockAvailability(product, warehouses).iterator().next();
		} else {
			return super.getStockLevel(product, warehouse);
		}
	}

	@Override
	public Collection<StockLevelModel> getAllStockLevels(ProductModel product) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			return getAvailabilityService().getStockAvailability(product, null) ;
		} else {
			return super.getAllStockLevels(product);
		}
	}


	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	protected SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	protected SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}



	public SapProductAvailabilityCache getSapProductAvailabilityCache() {
		return sapProductAvailabilityCache;
	}


	public void setSapProductAvailabilityCache(SapProductAvailabilityCache sapProductAvailabilityCache) {
		this.sapProductAvailabilityCache = sapProductAvailabilityCache;
	}


	public UserService getUserService() {
		return userService;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}


	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}

	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}

	
}

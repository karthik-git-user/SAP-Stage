/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.impl.SapProductAvailabilityCache;
import de.hybris.platform.sap.sapsalesordersimulation.service.AvailabilityService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
/**
 * 
 * Implementation for the Availability service.
 *
 */
public class DefaultAvailabilityService implements AvailabilityService {
	private static final String DEFAULT_PLANT = "defaultplant";
	private BaseStoreService baseStoreService;
	private UserService userService;
	private B2BUnitService b2bUnitService;
	private SapProductAvailabilityCache sapProductAvailabilityCache;
	private SalesOrderSimulationService salesOrderSimulationService;
	
	
	@Override
	public Collection<StockLevelModel> getStockAvailability(ProductModel product,
			Collection<WarehouseModel> warehouses) {
		Collection<StockLevelModel> stockLevelModels = new ArrayList<>();
		Collection<WarehouseModel> sapPlants = filterWarehousesBasedOnSAPConfigMapping(warehouses);
		for (Map.Entry<String, SapProductAvailability> entry : fetchAllStockDetails(product, sapPlants).entrySet()) {
			SapProductAvailability availability = entry.getValue();
			if (availability!=null &&  availability.getStockLevelModel() !=null) {
				stockLevelModels.add(availability.getStockLevelModel());
			}
		}
		return stockLevelModels;
	}

	@Override
	public Collection<FutureStockModel> getFutureAvailability(ProductModel product,
			Collection<WarehouseModel> warehouses) {
		
		
		Collection<FutureStockModel> futureStockLevelModels = new ArrayList<>();
		Collection<WarehouseModel> sapPlants = filterWarehousesBasedOnSAPConfigMapping(warehouses);
		for (Map.Entry<String, SapProductAvailability> entry : fetchAllStockDetails(product, sapPlants).entrySet()) {
			SapProductAvailability availability = entry.getValue();
			if (availability!=null &&  availability.getFutureAvailability() !=null) {
				futureStockLevelModels.addAll(availability.getFutureAvailability());
			}
		}
		return futureStockLevelModels;

	}
	@Override
	public SapProductAvailability getProductAvailability(ProductModel productModel,BaseStoreModel baseStore) {
		final String  currentCustomerId = getCurrentCustomerID();
		SapProductAvailability availability = getSapProductAvailabilityCache().readCachedProductAvailability(productModel,currentCustomerId,DEFAULT_PLANT);
		if(availability==null ) {
			availability =  getSalesOrderSimulationService().getStockAvailability(productModel, baseStore);
			if (availability != null ) {
				getSapProductAvailabilityCache().cacheProductAvailability(availability, productModel, currentCustomerId, DEFAULT_PLANT);
			}
		}
		return availability;
	}
	
	protected Map<String, SapProductAvailability>  fetchAllStockDetails(ProductModel product, Collection<WarehouseModel> sapPlants) {
		Map<String, SapProductAvailability> allSapProductAvailabilities = new HashMap<>();
		Collection<WarehouseModel> warehouseWithStockFetchRequired = new ArrayList<>();
		String currentCustomerId = getCurrentCustomerID();
		for ( WarehouseModel plant: sapPlants) {
			SapProductAvailability availability = getSapProductAvailabilityCache().readCachedProductAvailability(product,currentCustomerId,plant.getCode());
			if (availability!=null && availability.getStockLevelModel() !=null) {
				allSapProductAvailabilities.put(plant.getCode(),availability);
			} else {
				warehouseWithStockFetchRequired.add(plant);	
			}
		}	
		fetchStockDetailsForWarehouses(product, allSapProductAvailabilities, warehouseWithStockFetchRequired,
				currentCustomerId);
		return allSapProductAvailabilities;
	}

	protected void fetchStockDetailsForWarehouses(ProductModel product,
			Map<String, SapProductAvailability> allSapProductAvailabilities,
			Collection<WarehouseModel> warehouseWithStockFetchRequired, String currentCustomerId) {
		if (warehouseWithStockFetchRequired.isEmpty()) {
			return;
		}
		Map<String, SapProductAvailability> sapProductAvailability = getSalesOrderSimulationService()
				.getStockLevels(product, warehouseWithStockFetchRequired);
		if (sapProductAvailability != null) {
			for (Map.Entry<String, SapProductAvailability> entry : sapProductAvailability.entrySet()) {
				SapProductAvailability availability = entry.getValue();
				if (availability != null && availability.getStockLevelModel() != null) {
					getSapProductAvailabilityCache().cacheProductAvailability(availability, product, currentCustomerId,
							entry.getKey());
					allSapProductAvailabilities.put(entry.getKey(), availability);
				}
			}

		}

	}
	
	protected Collection<WarehouseModel> filterWarehousesBasedOnSAPConfigMapping(Collection<WarehouseModel> warehouses) {
		Collection<WarehouseModel> filteredWarehouse = new ArrayList<>();
		Set<SAPPlantLogSysOrgModel> sapPlantLogSysOrgs = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapPlantLogSysOrg();
		for (SAPPlantLogSysOrgModel sapPlantDetails : sapPlantLogSysOrgs) {
			if (isValidPlant(warehouses, sapPlantDetails)) {
				
				filteredWarehouse.add(sapPlantDetails.getPlant());
			}
		}
		return filteredWarehouse;
	}


	protected String getCurrentCustomerID() {

		UserModel userModel= getUserService().getCurrentUser();

		B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);

		return parent.getUid();

	}
	
	protected boolean isValidPlant(Collection<WarehouseModel> warehouses, SAPPlantLogSysOrgModel sapPlantDetails) {
		boolean vaildPlant = true;

		if (sapPlantDetails == null || sapPlantDetails.getPlant() == null) {
			vaildPlant = false;
		}

		if (warehouses != null && !warehouses.isEmpty()
				&& warehouses.stream().noneMatch(o -> o.getCode().equals(sapPlantDetails.getPlant().getCode()))) {
			vaildPlant = false;
		}
		return vaildPlant;
	}

	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	protected UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	protected SapProductAvailabilityCache getSapProductAvailabilityCache() {
		return sapProductAvailabilityCache;
	}

	public void setSapProductAvailabilityCache(SapProductAvailabilityCache sapProductAvailabilityCache) {
		this.sapProductAvailabilityCache = sapProductAvailabilityCache;
	}

	protected SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}
	
	
	

}

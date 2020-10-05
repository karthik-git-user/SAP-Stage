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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.impl.SapProductAvailabilityCache;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAvailabilityServiceTest {
	
	private static final int AVAILABLE_SIZE = 2;
	private static final long STOCK_20 = 20L;
	private static final int QTY_20 = 20;
	private static final long STOCK_LEVEL = 10L;
	private static final int AVAILABLE_QTY = 10;
	private static final String PLANT = "1010";
	private static final String CUSTOMERID = "0010100001";
	@InjectMocks
	DefaultAvailabilityService defaultAvailabilityService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private UserService userService;
	
	@Mock
	private B2BUnitService b2bUnitService;
	@Mock
	private SapProductAvailabilityCache sapProductAvailabilityCache;
	@Mock
	private SalesOrderSimulationService salesOrderSimulationService;
	
	@Test
	public void testGetStockAvailability() {
		ProductModel product = mock(ProductModel.class);
		WarehouseModel warehouse = mock(WarehouseModel.class);
		when(warehouse.getCode()).thenReturn(PLANT);
		
		StockLevelModel stockModel = mock(StockLevelModel.class);
		when(stockModel.getAvailable()).thenReturn(AVAILABLE_QTY);
		when(stockModel.getWarehouse()).thenReturn(warehouse);
		Collection<WarehouseModel> warehouses = new ArrayList<>();
		warehouses.add(warehouse);
		SAPPlantLogSysOrgModel sapPlantLogSysOrg = mock(SAPPlantLogSysOrgModel.class);
		when(sapPlantLogSysOrg.getPlant()).thenReturn(warehouse);
		Set<SAPPlantLogSysOrgModel> sapPlantLogSysOrgs = new HashSet();
		sapPlantLogSysOrgs.add(sapPlantLogSysOrg);
		SapProductAvailability sapProductAvailability = mock(SapProductAvailability.class);
		when(sapProductAvailability.getCurrentStockLevel()).thenReturn(STOCK_LEVEL);
		when(sapProductAvailability.getStockLevelModel()).thenReturn(stockModel);
		
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(baseStoreService.getCurrentBaseStore().getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(baseStoreService.getCurrentBaseStore().getSAPConfiguration().getSapPlantLogSysOrg()).thenReturn(sapPlantLogSysOrgs);
		when(defaultAvailabilityService.getCurrentCustomerID() ).thenReturn(CUSTOMERID);
		when(sapProductAvailabilityCache.readCachedProductAvailability(product,CUSTOMERID,PLANT)).thenReturn(sapProductAvailability);
		
		Assert.assertTrue(defaultAvailabilityService.isValidPlant(warehouses, sapPlantLogSysOrg));
		
		
		when(sapProductAvailabilityCache.readCachedProductAvailability(product,CUSTOMERID,PLANT)).thenReturn(sapProductAvailability);
		Collection<StockLevelModel> stockLevel = defaultAvailabilityService.getStockAvailability(product,warehouses);
		assertNotNull(stockLevel);
		StockLevelModel stock = stockLevel.iterator().next();
		Assert.assertThat(Double.valueOf(AVAILABLE_QTY), is(Double.valueOf(stock.getAvailable())));
		
		Map<String, SapProductAvailability> map = new HashMap<>(); 
		when(stockModel.getAvailable()).thenReturn(QTY_20);
		when(sapProductAvailability.getCurrentStockLevel()).thenReturn(STOCK_20);
		List<FutureStockModel> futureAvailabilities = new ArrayList<>();
		futureAvailabilities.add(mock(FutureStockModel.class));
		futureAvailabilities.add(mock(FutureStockModel.class));
		when(sapProductAvailability.getFutureAvailability()).thenReturn(futureAvailabilities);
		
		map.put(PLANT, sapProductAvailability);
		when(sapProductAvailabilityCache.readCachedProductAvailability(product,CUSTOMERID,PLANT)).thenReturn(null);
		when( salesOrderSimulationService.getStockLevels(product, warehouses)).thenReturn(map);
		stockLevel = defaultAvailabilityService.getStockAvailability(product,warehouses);
		assertNotNull(stockLevel);
		stock = stockLevel.iterator().next();
		Assert.assertThat(Double.valueOf(QTY_20), is(Double.valueOf(stock.getAvailable())));
		
		Collection<FutureStockModel> availabilities = defaultAvailabilityService.getFutureAvailability(product, warehouses);
		Assert.assertThat(Double.valueOf(AVAILABLE_SIZE), is(Double.valueOf(availabilities.size())));
		
	}
}

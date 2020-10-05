/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.*;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.impl.DefaultPPSConfigService;


@SuppressWarnings("javadoc")
public class DefaultPPSConfigurationServiceTest
{
	DefaultPPSConfigService cut;

	@Mock
	private BaseStoreService baseStoreService;
	private BaseStoreModel baseStoreModel;
	private SAPConfigurationModel sapConfig;
	private CartModel order;
	private ProductModel product;

	@Before
	public void setUp()
	{
		cut = new DefaultPPSConfigService();
		baseStoreModel = new BaseStoreModel();
		sapConfig = new SAPConfigurationModel();
		order = new CartModel();
		product = new ProductModel();
		MockitoAnnotations.initMocks(this);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		baseStoreModel.setSAPConfiguration(sapConfig);
		sapConfig.setSappps_active(Boolean.TRUE);
		cut.setBaseStoreService(baseStoreService);
		order.setStore(baseStoreModel);
	}

	@Test
	public void testSetGetBaseStoreService()
	{
		cut = new DefaultPPSConfigService();
		assertNull(cut.getBaseStoreService());
		cut.setBaseStoreService(baseStoreService);
		assertSame(baseStoreService, cut.getBaseStoreService());
	}

	@Test
	public void testNoSapConfigActive() throws Exception
	{
		sapConfig.setSappps_active(Boolean.FALSE);
		assertFalse(cut.isPpsActive(order));
	}

	@Test
	public void testNoSapConfigAssigned() throws Exception
	{
		baseStoreModel.setSAPConfiguration(null);
		assertFalse(cut.isPpsActive(order));
	}

	@Test
	public void testNoSapConfigActiveProd() throws Exception
	{
		sapConfig.setSappps_active(Boolean.FALSE);
		assertFalse(cut.isPpsActive(product));
	}

	@Test
	public void testNoSapConfigAssignedProd() throws Exception
	{
		baseStoreModel.setSAPConfiguration(null);
		assertFalse(cut.isPpsActive(product));
	}

	@Test
	public void testGetBuIdOrder() throws Exception
	{
		sapConfig.setSappps_businessUnitId("BU1");
		assertEquals("BU1", cut.getBusinessUnitId(order));
	}

	@Test
	public void testGetBuIdProd() throws Exception
	{
		sapConfig.setSappps_businessUnitId("BU1");
		assertEquals("BU1", cut.getBusinessUnitId(product));
	}

}

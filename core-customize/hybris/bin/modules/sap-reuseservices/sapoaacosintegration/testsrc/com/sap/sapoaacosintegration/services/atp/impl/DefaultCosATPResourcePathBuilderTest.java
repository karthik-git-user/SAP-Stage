/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)

public class DefaultCosATPResourcePathBuilderTest
{
	@Mock
	public ServiceUtils serviceUtils;

	@Mock
	CosConfigurationService configurationService;

	@Mock
	ProductModel product;

	@Mock
	UnitModel unitModel;

	@InjectMocks
	private DefaultCosATPResourcePathBuilder defaultCosATPResourcePathBuilder;

	private final List<ProductModel> productList = new ArrayList<>();


	@Before
	public void setUp()
	{
		defaultCosATPResourcePathBuilder.setConfigurationService(configurationService);

		//Mocks
		when(configurationService.getCosCacStrategyId()).thenReturn("ABC");

		//UnitModel
		when(unitModel.getCode()).thenReturn("234");

		//ProductModel
		when(product.getCode()).thenReturn("123");
		when(product.getUnit()).thenReturn(unitModel);

		//ProductModelLost
		productList.add(product);
	}

	@Test
	public void prepareRestCallForProductTest()
	{
		//SetUp
		HttpEntity entity;

		//Execute
 		entity = defaultCosATPResourcePathBuilder.prepareRestCallForProduct(product);

		//Verify
		Assert.assertNotNull(entity);
	}

	@Test
	public void prepareRestCallForProductsTest()
	{
		//SetUp
		HttpEntity entity;

		//Execute
		entity = defaultCosATPResourcePathBuilder.prepareRestCallForProducts(productList);

		//Verify
		Assert.assertNotNull(entity);
	}

	@Test
	public void prepareRestCallForProductAndSourceTest()
	{
		//SetUp
		HttpEntity entity;
		final String sourceId = "test";

		//Execute
		entity = defaultCosATPResourcePathBuilder.prepareRestCallForProductAndSource(product, sourceId);

		//Verify
		Assert.assertNotNull(entity);
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.config.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosConfigurationServiceTest
{

	@Mock
	SAPConfigurationService sapCoreConfigurationService;

	@InjectMocks
	DefaultCosConfigurationService defaultCosConfigurationService;

	@Before
	public void setup()
	{
		//Mocks
		when(((String) sapCoreConfigurationService.getProperty(SapoaacosintegrationConstants.COS_CAC_STRATEGY_ID)))
				.thenReturn("1234");
		when(((String) sapCoreConfigurationService.getProperty(SapoaacosintegrationConstants.COS_CAS_STRATEGY_ID)))
				.thenReturn("abcd");
	}

	@Test
	public void getCosCacStrategyIdTest()
	{
		//SetUp
		String StrategyId;

		//Execute
		StrategyId = defaultCosConfigurationService.getCosCacStrategyId();

		//Verify
		Assert.assertNotNull(StrategyId);
		Assert.assertEquals("1234", StrategyId);

	}

	@Test
	public void getCosCasStrategyIdTest()
	{
		//SetUp
		String StrategyId;

		//Execute
		StrategyId = defaultCosConfigurationService.getCosCasStrategyId();

		//Verify
		Assert.assertNotNull(StrategyId);
		Assert.assertEquals("abcd", StrategyId);

	}
}

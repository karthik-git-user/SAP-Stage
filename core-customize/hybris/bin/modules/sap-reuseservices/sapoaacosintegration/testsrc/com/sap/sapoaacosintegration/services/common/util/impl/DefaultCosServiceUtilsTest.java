/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.common.util.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosServiceUtilsTest
{
	@InjectMocks
	private DefaultCosServiceUtils defaultCosServiceUtils;

	@Test
	public void generateItemNumberTest()
	{
		//SetUp
		String result = null;

		//Execute
		result = defaultCosServiceUtils.generateItemNumber();

		//Verify
		Assert.assertNotNull(result);
	}

	@Test
	public void generateRandomStringTest()
	{
		//SetUp
		String num = null;

		//Execute
		num = defaultCosServiceUtils.generateRandomString();

		//Verify
		Assert.assertNotNull(num);
	}

}

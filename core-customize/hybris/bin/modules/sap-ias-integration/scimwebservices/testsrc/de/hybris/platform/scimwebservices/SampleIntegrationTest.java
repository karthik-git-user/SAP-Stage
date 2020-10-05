/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Test;


@IntegrationTest
public class SampleIntegrationTest
{

	private static String SAMPLE_TEST_CLASS = "sample-test-class";

	@Test
	public void testOnValidateWhenScimUserIdIsEmpty() throws InterceptorException
	{
		// do nothing
	}

}

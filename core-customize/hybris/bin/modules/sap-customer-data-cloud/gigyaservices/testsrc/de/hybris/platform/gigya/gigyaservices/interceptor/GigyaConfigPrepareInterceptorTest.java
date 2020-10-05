/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

@UnitTest
public class GigyaConfigPrepareInterceptorTest {
	
	private final GigyaConfigPrepareInterceptor interceptor = new GigyaConfigPrepareInterceptor();

	private GigyaConfigModel gigyaConfig;
	
	final String newLine = System.getProperty("line.separator");
	
	@Before
	public void setUp() {
		gigyaConfig = new GigyaConfigModel();
		gigyaConfig.setGigyaPrivateKey(setUpPrivateKey());
	}
	
	@Test
	public void testRemovalofWhitespace() throws InterceptorException {
		interceptor.onPrepare(gigyaConfig, null);
		final boolean assertCondition = StringUtils.contains(gigyaConfig.getGigyaPrivateKey(), newLine);
		assertFalse("Checking if whitespaces are removed", assertCondition);
	}
	
	private String setUpPrivateKey() {
		return "-----BEGIN RSA PRIVATE KEY-----"
				.concat(newLine)
				.concat("-----END RSA PRIVATE KEY-----");
	}
	
}

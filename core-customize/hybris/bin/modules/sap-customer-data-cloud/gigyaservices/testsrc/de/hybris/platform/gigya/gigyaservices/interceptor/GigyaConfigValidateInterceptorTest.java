/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class GigyaConfigValidateInterceptorTest
{

	private final GigyaConfigValidateInterceptor interceptor = new GigyaConfigValidateInterceptor();

	private GigyaConfigModel gigyaConfig;
	private GigyaSessionConfigModel gigyaSessionConfig;

	@Before
	public void setUp()
	{
		gigyaConfig = new GigyaConfigModel();
		gigyaSessionConfig = new GigyaSessionConfigModel();		
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenApiKeyIsMissing() throws InterceptorException
	{
		interceptor.onValidate(gigyaConfig, null);
		fail("Not yet implemented");
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserSecretIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		gigyaConfig.setGigyaUserKey("user-key");
		interceptor.onValidate(gigyaConfig, null);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserKeyIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		gigyaConfig.setGigyaUserSecret("user-secret");
		interceptor.onValidate(gigyaConfig, null);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenUserKeyAndSecretOrPrivateKeyIsMissing() throws InterceptorException
	{
		gigyaConfig.setGigyaApiKey("api-key");
		interceptor.onValidate(gigyaConfig, null);
	}
	
	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenSlidingSessionWithGlobalSite() throws InterceptorException
	{
		gigyaConfig.setIsSiteGlobal(true);
		gigyaSessionConfig.setSessionType(GigyaSessionType.SLIDING);
		gigyaConfig.setGigyaSessionConfig(gigyaSessionConfig);
		interceptor.onValidate(gigyaConfig, null);
	}
	
	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenSlidingSessionWithNonGlobalSiteAndNoUserSecret() throws InterceptorException
	{
		gigyaConfig.setIsSiteGlobal(false);
		gigyaConfig.setGigyaUserSecret(null);
		gigyaSessionConfig.setSessionType(GigyaSessionType.SLIDING);
		gigyaConfig.setGigyaSessionConfig(gigyaSessionConfig);
		interceptor.onValidate(gigyaConfig, null);
	}

}

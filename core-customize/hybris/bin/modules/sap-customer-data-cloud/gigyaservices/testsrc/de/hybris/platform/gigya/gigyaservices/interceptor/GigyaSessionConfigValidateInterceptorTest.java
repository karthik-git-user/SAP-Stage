/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

@UnitTest
public class GigyaSessionConfigValidateInterceptorTest {
	
	private final GigyaSessionConfigValidateInterceptor interceptor = new GigyaSessionConfigValidateInterceptor();

	private GigyaConfigModel gigyaConfig;
	private GigyaSessionConfigModel gigyaSessionConfig;

	@Before
	public void setUp()
	{
		gigyaConfig = new GigyaConfigModel();		
		gigyaSessionConfig = new GigyaSessionConfigModel();		
	}
	
	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenSlidingSessionWithGlobalSite() throws InterceptorException
	{
		gigyaConfig.setIsSiteGlobal(true);
		gigyaSessionConfig.setSessionType(GigyaSessionType.SLIDING);		
		gigyaSessionConfig.setGigyaConfigs(Collections.singleton(gigyaConfig));
		interceptor.onValidate(gigyaSessionConfig, null);
	}
	
	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenSlidingSessionWithNonGlobalSiteAndNoUserSecret() throws InterceptorException
	{
		gigyaConfig.setIsSiteGlobal(false);
		gigyaConfig.setGigyaUserSecret(null);
		gigyaSessionConfig.setSessionType(GigyaSessionType.SLIDING);		
		gigyaSessionConfig.setGigyaConfigs(Collections.singleton(gigyaConfig));
		interceptor.onValidate(gigyaSessionConfig, null);
	}

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 *
 */
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.user.UserContextService;


@UnitTest
public class LoginEventListenerTest
{
	@Mock
	BaseSiteService baseSiteService;

	@Mock
	ClickStreamService clickStreamService;

	LoginEventListener listener = new LoginEventListener();

	@Mock
	UserContextService userContextService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		listener.setBaseSiteService(baseSiteService);
		listener.setClickStreamService(clickStreamService);
		listener.setUserContextService(userContextService);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test_onApplicationEvent()
	{
		listener.onApplicationEvent(null);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(new BaseSiteModel());
		listener.onApplicationEvent(null);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(Boolean.TRUE);
		listener.onApplicationEvent(null);
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.microsoft.sqlserver.jdbc.StringUtils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class to test the functionality of 'GigyaCustomerFacadeAspect'
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaCustomerFacadeAspectTest {

	private static final String SAMPLE_UID = "uid";

	@InjectMocks
	private GigyaCustomerFacadeAspect gigyaCustomerFacadeAspect = new GigyaCustomerFacadeAspect();

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private UserService userService;

	@Mock
	private GigyaLoginFacade gigyaLoginFacade;

	@Mock
	private CustomerModel customer;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Mock
	private EmployeeModel employee;

	@Before
	public void setUp() {
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.when(customer.getGyUID()).thenReturn(SAMPLE_UID);
	}

	@Test
	public void testUpdateFullProfileForBaseSitesWithCDCConfigAndCustomerIsRegisteredByCDC() throws Throwable {
		gigyaCustomerFacadeAspect.updateFullProfile(joinPoint);

		Mockito.verify(gigyaLoginFacade).updateUser(gigyaConfig, customer);
	}

	@Test
	public void testUpdateFullProfileForBaseSitesWithoutCDCConfig() throws Throwable {
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(null);

		gigyaCustomerFacadeAspect.updateFullProfile(joinPoint);

		Mockito.verifyNoMoreInteractions(gigyaLoginFacade);
	}

	@Test
	public void testUpdateFullProfileWhenCurrentUserIsEmployee() throws Throwable {
		Mockito.when(userService.getCurrentUser()).thenReturn(employee);

		gigyaCustomerFacadeAspect.updateFullProfile(joinPoint);

		Mockito.verifyNoMoreInteractions(gigyaLoginFacade);
	}

	@Test
	public void testUpdateFullProfileWhenCustomerIsNotRegisteredByCDC() throws Throwable {
		Mockito.when(customer.getGyUID()).thenReturn(StringUtils.EMPTY);

		gigyaCustomerFacadeAspect.updateFullProfile(joinPoint);

		Mockito.verifyNoMoreInteractions(gigyaLoginFacade);
	}

}

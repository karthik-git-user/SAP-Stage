/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaGroupsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaOrganisationData;
import de.hybris.platform.gigya.gigyafacades.consent.GigyaConsentFacade;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSResponse;


/**
 * Test class for DefaultGigyaB2BLoginFacade
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaB2BLoginFacadeTest
{

	private static final String SAMPLE_UID = "sample-uid";
	private static final String SAMPLE_API_KEY = "sample-api-key";

	@InjectMocks
	private final GigyaLoginFacade gigyaLoginFacade = new DefaultGigyaB2BLoginFacade()
	{


		@Override
		protected GigyaGroupsData getGroupsData(final GSResponse gsResponse)
		{
			return groups;
		}

	};

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private GigyaAuthService gigyaAuthService;

	@Mock
	private ModelService sapB2BModelService;

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private TaskService taskService;

	@Mock
	private GigyaConsentFacade gigyaConsentFacade;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaUserObject gigyaUserObject;

	@Mock
	private UserModel userModel;

	@Mock
	private TaskModel task;

	@Mock
	private CustomerModel customer;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private B2BCustomerModel b2bCustomer;

	@Mock
	private GigyaGroupsData groups;

	@Mock
	private GigyaOrganisationData firstOrg;

	@Mock
	private GigyaOrganisationData secondOrg;

	@Mock
	private B2BUnitModel b2bUnit;

	@Mock
	private GSResponse gsResponse;

	@Test(expected = GigyaApiException.class)
	public void testCreateNewCustomerWhenEmailIsMissing() throws DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn("");
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(CustomerModel.class)).thenReturn(customer);
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);

		Assert.assertEquals(customer, gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID));
		Mockito.verify(customerAccountService).register(customer, null);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testCreateNewCustomerWhenGigyaConfigExists() throws DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(CustomerModel.class)).thenReturn(customer);
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);

		Assert.assertEquals(customer, gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID));
		Mockito.verify(customerAccountService).register(customer, null);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testCreateNewCustomerWithGroupsWithOrgAssociation() throws DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(B2BCustomerModel.class)).thenReturn(b2bCustomer);
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.when(gigyaUserObject.getGroups()).thenReturn(groups);
		Mockito.when(groups.getOrganizations()).thenReturn(Arrays.asList(firstOrg, secondOrg));
		Mockito.when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(b2bUnit);
		Mockito.when(firstOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(secondOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaConfig.getGigyaApiKey()).thenReturn(SAMPLE_API_KEY);

		Assert.assertEquals(b2bCustomer, gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID));
		Mockito.verify(b2bUnitService, Mockito.times(2)).addMember(b2bUnit, b2bCustomer);
		Mockito.verify(modelService, Mockito.times(2)).save(b2bUnit);
		Mockito.verify(b2bCustomer).setDefaultB2BUnit(b2bUnit);
		Mockito.verify(gigyaAuthService).assignAuthorisationsToCustomer(b2bCustomer);
		Mockito.verify(sapB2BModelService).save(b2bCustomer);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testCreateNewCustomerWithGroupsWithOrgCreationAndAssociation() throws DuplicateUidException
	{
		Mockito.when(gigyaUserObject.getEmail()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.when(modelService.create(B2BCustomerModel.class)).thenReturn(b2bCustomer);
		Mockito.when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.when(gigyaUserObject.getGroups()).thenReturn(groups);
		Mockito.when(groups.getOrganizations()).thenReturn(Arrays.asList(firstOrg, secondOrg));
		Mockito.when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(null);
		Mockito.when(firstOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(secondOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaConfig.getGigyaApiKey()).thenReturn(SAMPLE_API_KEY);
		Mockito.when(gigyaConfig.isCreateOrganizationOnLogin()).thenReturn(Boolean.TRUE);

		Assert.assertEquals(b2bCustomer, gigyaLoginFacade.createNewCustomer(gigyaConfig, SAMPLE_UID));
		Mockito.verify(b2bUnitService, Mockito.times(2)).addMember(b2bUnit, b2bCustomer);
		Mockito.verify(modelService, Mockito.times(2)).save(b2bUnit);
		Mockito.verify(b2bCustomer).setDefaultB2BUnit(b2bUnit);
		Mockito.verify(gigyaAuthService).assignAuthorisationsToCustomer(b2bCustomer);
		Mockito.verify(sapB2BModelService).save(b2bCustomer);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testUpdateUserWithoutGroups() throws GSKeyNotFoundException, GigyaApiException
	{
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.when(gsResponse.hasData()).thenReturn(Boolean.FALSE);

		gigyaLoginFacade.updateUser(gigyaConfig, customer);

		Mockito.verify(modelService).save(customer);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testUpdateUserWithGroups() throws GSKeyNotFoundException, GigyaApiException
	{
		Mockito.when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.when(gsResponse.hasData()).thenReturn(Boolean.FALSE);
		Mockito.when(groups.getOrganizations()).thenReturn(Arrays.asList(firstOrg, secondOrg));
		Mockito.when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(null);
		Mockito.when(firstOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(secondOrg.getOrgId()).thenReturn(SAMPLE_UID);
		Mockito.when(gigyaConfig.getGigyaApiKey()).thenReturn(SAMPLE_API_KEY);
		Mockito.when(gigyaConfig.isCreateOrganizationOnLogin()).thenReturn(Boolean.TRUE);

		gigyaLoginFacade.updateUser(gigyaConfig, b2bCustomer);

		Mockito.verify(b2bUnitService, Mockito.times(2)).addMember(b2bUnit, b2bCustomer);
		Mockito.verify(modelService, Mockito.times(2)).save(b2bUnit);
		Mockito.verify(b2bCustomer).setDefaultB2BUnit(b2bUnit);
		Mockito.verify(gigyaAuthService).removeAuthorisationsOfCustomer(b2bCustomer);
		Mockito.verify(gigyaAuthService).assignAuthorisationsToCustomer(b2bCustomer);
		Mockito.verify(modelService).save(b2bCustomer);
		Mockito.verify(taskService).scheduleTask(task);
	}

}

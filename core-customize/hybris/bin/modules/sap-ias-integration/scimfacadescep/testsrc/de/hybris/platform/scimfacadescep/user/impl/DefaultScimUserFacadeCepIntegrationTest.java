/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.user.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserEmail;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultScimUserFacadeCepIntegrationTest extends ServicelayerTransactionalTest
{

	private ScimUser user;

	@Resource
	private DefaultEnhancedScimUserFacade scimUserFacade;

	private static final String USER_ID = "user-id";
	private static final String NEW_USER_ID = "newUser";

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private Converter<ScimUser, UserModel> scimUserReverseConverter;

	@Resource
	private Converter<UserModel, ScimUser> scimUserConverter;

	@Resource
	private GenericDao<UserModel> scimUserGenericDao;



	private static final Logger LOG = Logger.getLogger(DefaultScimUserFacadeCepIntegrationTest.class);

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Default Scim User Facade Integration Test");
		user = new ScimUser();
		user.setId(USER_ID);
		user.setUserType("employee");
		final ScimUserEmail email = new ScimUserEmail();
		email.setValue("test@test.com");
		email.setPrimary(true);
		user.setEmails(Collections.singletonList(email));
		scimUserFacade.createUser(user);
	}

	@Test
	public void shouldCreateUser() {
		final ScimUser newUser = createNewUser();
		final EmployeeModel exampleEmployee = new EmployeeModel();
		exampleEmployee.setScimUserId(NEW_USER_ID);
		final EmployeeModel employee = flexibleSearchService.getModelByExample(exampleEmployee);
		Assert.assertEquals(newUser.getId().toString(), employee.getScimUserId().toString());
		Assert.assertEquals(employee.getUid().toString(), "newtest@newtest.com");

	}

	@Test
	public void shouldGetUser() {
		final ScimUser returnedUser = scimUserFacade.getUser(USER_ID);
		Assert.assertEquals(user.getId().toString(), returnedUser.getId().toString());
		final ScimUserEmail email = user.getEmails().get(0);
		Assert.assertEquals(email.getValue().toString(), "test@test.com");
		Assert.assertTrue(email.getPrimary());
	}

	@Test
	public void shouldGetAllUsers()
	{
		createNewUser();
		final String[] ids =
		{ NEW_USER_ID, USER_ID };
		final List<ScimUser> returnedScimUsers = scimUserFacade.getUsers();
		for (final ScimUser returnedUser : returnedScimUsers)
		{
			if (returnedUser.getId() != null)
			{
				Assert.assertTrue(Arrays.asList(ids).contains(returnedUser.getId().toString()));
			}

		}
	}

	@Test
	public void shouldDeleteUser() {
		final boolean returnedValue = scimUserFacade.deleteUser(USER_ID);
		Assert.assertTrue(returnedValue);
		final UserModel exampleEmployee = new UserModel();
		exampleEmployee.setScimUserId(USER_ID);
		LOG.info(flexibleSearchService.getModelByExample(exampleEmployee));
		Assert.assertFalse(exampleEmployee.isLoginDisabled());
	}

	@Test
	public void shouldUpdateUser() {
		user.setActive(true);
		scimUserFacade.updateUser(USER_ID, user);

		final EmployeeModel exampleEmployee = new EmployeeModel();
		exampleEmployee.setScimUserId(USER_ID);
		final UserModel employee = flexibleSearchService.getModelByExample(exampleEmployee);
		Assert.assertFalse(employee.isLoginDisabled());
		Assert.assertEquals(employee.getUid().toString(), "test@test.com");
	}

	private ScimUser createNewUser() {
		final ScimUser newUser = new ScimUser();
		newUser.setId(NEW_USER_ID);
		newUser.setUserType("employee");
		final ScimUserEmail newEmail = new ScimUserEmail();
		newEmail.setValue("newtest@newtest.com");
		newEmail.setPrimary(true);
		newUser.setEmails(Collections.singletonList(newEmail));
		return scimUserFacade.createUser(newUser);
	}



}

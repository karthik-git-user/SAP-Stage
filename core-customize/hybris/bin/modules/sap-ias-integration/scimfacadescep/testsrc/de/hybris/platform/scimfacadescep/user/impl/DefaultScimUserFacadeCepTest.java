/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScimUserFacadeCepTest
{
	private static final String EXTERNAL_ID = "external-id";

	@InjectMocks
	private final DefaultEnhancedScimUserFacade scimUserFacade = new DefaultEnhancedScimUserFacade();

	@Mock
	private Converter<EmployeeModel, ScimUser> scimUserConverter;

	@Mock
	private EmployeeModel employeeModel;

	@Mock
	private GenericDao<UserModel> scimUserGenericDao;
	
	@Mock
	private ModelService modelService;
	
	@Mock
	private Converter<ScimUser, UserModel> scimUserReverseConverter;
	
	@Mock
	private FlexibleSearchService flexibleSearchService;


	@Test
	public void testGetUsers()
	{

		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("employee");

		Mockito.when(modelService.create(EmployeeModel.class)).thenReturn(employeeModel);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));
		Mockito.when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);
		Mockito.when(scimUserReverseConverter.convert(scimUser)).thenReturn(employeeModel);
		Mockito.when(scimUserGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(employeeModel));
		
		scimUserFacade.createUser(scimUser);
		scimUserFacade.getUsers();

		Mockito.verify(scimUserConverter).convert(employeeModel);
		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
	}

}

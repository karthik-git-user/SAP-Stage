/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.user.impl;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.user.impl.DefaultScimUserFacade;
import de.hybris.platform.scimfacadescep.user.EnhancedScimUserFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of EnhancedScimUserFacade
 */
public class DefaultEnhancedScimUserFacade extends DefaultScimUserFacade
		implements EnhancedScimUserFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultScimUserFacade.class);

	private GenericDao<UserModel> scimUserGenericDao;
	private Converter<UserModel, ScimUser> enhancedScimUserConverter;


	@Override
	public List<ScimUser> getUsers()
	{
		LOG.info("ScimUsersFacade getUsers entry");
		try
		{
			final List<UserModel> userModels = scimUserGenericDao.find();
			final List<ScimUser> scimUsers = new ArrayList<>();
			for (final UserModel userModel : userModels)
			{
				if (userModel instanceof EmployeeModel)
				{
					scimUsers.add(super.getScimUserConverter().convert(userModel));
				}
			}

			return CollectionUtils.isNotEmpty(scimUsers) ? scimUsers : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No user model found", e);
		}
		return Collections.emptyList();
	}


	public GenericDao<UserModel> getScimUserGenericDao()
	{
		return scimUserGenericDao;
	}

	public void setScimUserGenericDao(final GenericDao<UserModel> scimUserGenericDao)
	{
		this.scimUserGenericDao = scimUserGenericDao;
	}

	public Converter<UserModel, ScimUser> getEnhancedScimUserConverter()
	{
		return enhancedScimUserConverter;
	}

	public void setEnhancedScimUserConverter(final Converter<UserModel, ScimUser> enhancedScimUserConverter)
	{
		this.enhancedScimUserConverter = enhancedScimUserConverter;
	}
}

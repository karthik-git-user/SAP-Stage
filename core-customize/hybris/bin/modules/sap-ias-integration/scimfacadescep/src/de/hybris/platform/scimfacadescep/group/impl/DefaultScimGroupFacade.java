/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.group.impl;

import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.group.ScimGroupFacade;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Default implementation of ScimGroupFacade
 */
public class DefaultScimGroupFacade implements ScimGroupFacade {

	private static final Logger LOG = Logger.getLogger(DefaultScimGroupFacade.class);

	private ModelService modelService;

	private FlexibleSearchService flexibleSearchService;

	private Converter<ScimGroup, ScimUserGroupModel> scimGroupReverseConverter;

	private Converter<ScimUserGroupModel, ScimGroup> scimGroupConverter;

	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;



	@Override
	public ScimGroup createGroup(final ScimGroup scimGroup) {
		final ScimUserGroupModel group = modelService.create(ScimUserGroupModel.class);
		scimGroup.setMembers(null);
		scimGroupReverseConverter.convert(scimGroup, group);
		modelService.save(group);
		LOG.info("User Group with id=" + group.getScimUserGroup() + " successfully created.");
		return getGroup(group.getScimUserGroup());
	}

	@Override
	public ScimGroup updateGroup(final String groupId, final ScimGroup scimGroup){
		final ScimUserGroupModel group = getGroupForScimGroupId(groupId);
		if (group != null)
		{
			scimGroupReverseConverter.convert(scimGroup, group);
			modelService.save(group);
			LOG.info("Group with id=" + group.getScimUserGroup() + " successfully updated.");
			return getGroup(groupId);
		}
		else
		{
			throw new ScimException("Group with scimGroupId=" + scimGroup.getId() + " doesn't exist, update failed.");
		}
	}

	@Override
	public ScimGroup getGroup(final String groupId)
	{
		final ScimUserGroupModel group = getGroupForScimGroupId(groupId);

		if (group == null)
		{
			throw new ScimException("Error while fetching the resource=" + groupId);
		}

		return scimGroupConverter.convert(group);

	}

	@Override
	public List<ScimGroup> getGroups(){
		try
		{
			final List<ScimUserGroupModel> groupModels = scimUserGroupGenericDao.find();
			final List<ScimGroup> scimGroups = new ArrayList<ScimGroup> ();
			for(final ScimUserGroupModel scimUserGroupModel: groupModels) {
				scimGroups.add(scimGroupConverter.convert(scimUserGroupModel));
			}

			return CollectionUtils.isNotEmpty(scimGroups) ? scimGroups : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No group model found", e);
		}
		return Collections.emptyList();
	}

	@Override
	public void deleteGroup(final String groupId){
		final ScimUserGroupModel group = getGroupForScimGroupId(groupId);

		if (group != null)
		{
			modelService.remove(group);
			LOG.info("Group with uid=" + groupId + " is deleted.");
		}
		else
		{
			throw new ScimException("Error while deleting the resource=" + groupId);
		}
	}

	@Override
	public ScimUserGroupModel getGroupForScimGroupId(final String scimGroupId)
	{
		try
		{
			final ScimUserGroupModel exampleGroupModel = new ScimUserGroupModel();
			exampleGroupModel.setScimUserGroup(scimGroupId);
			final List<ScimUserGroupModel> groupModels = flexibleSearchService.getModelsByExample(exampleGroupModel);

			return CollectionUtils.isNotEmpty(groupModels) ? groupModels.get(0) : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No group model found with scimGroupId=" + scimGroupId, e);

		}
		return null;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public Converter<ScimGroup, ScimUserGroupModel> getScimGroupReverseConverter()
	{
		return scimGroupReverseConverter;
	}

	public void setScimGroupReverseConverter(final Converter<ScimGroup, ScimUserGroupModel> scimGroupReverseConverter)
	{
		this.scimGroupReverseConverter = scimGroupReverseConverter;
	}

	public Converter<ScimUserGroupModel, ScimGroup> getScimGroupConverter()
	{
		return scimGroupConverter;
	}

	public void setScimGroupConverter(final Converter<ScimUserGroupModel, ScimGroup> scimGroupConverter)
	{
		this.scimGroupConverter = scimGroupConverter;
	}

	public GenericDao<ScimUserGroupModel> getScimUserGroupGenericDao()
	{
		return scimUserGroupGenericDao;
	}

	public void setScimUserGroupGenericDao(final GenericDao<ScimUserGroupModel> scimUserGroupGenericDao)
	{
		this.scimUserGroupGenericDao = scimUserGroupGenericDao;
	}

}

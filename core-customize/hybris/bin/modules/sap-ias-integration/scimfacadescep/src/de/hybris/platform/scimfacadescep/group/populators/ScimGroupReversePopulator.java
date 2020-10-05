/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.group.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupMember;
import de.hybris.platform.scimfacadescep.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Populates ScimUserGroupModel using ScimGroup information
 */
public class ScimGroupReversePopulator implements Populator<ScimGroup, ScimUserGroupModel> {

	private FlexibleSearchService flexibleSearchService;
	private static final Logger LOG = Logger.getLogger(ScimGroupReversePopulator.class);
	private ModelService modelService;
	private TypeService scimUserGroupTypeService;



	@Override
	public void populate(final ScimGroup scimGroup, final ScimUserGroupModel scimUserGroupModel){
		Collection<UserGroupModel> userGroupModelCollection = new HashSet<UserGroupModel>();
		final String id = scimGroup.getId();
		final List<ScimGroupMember> members = scimGroup.getMembers();

		final ScimUserGroupModel existingScimUserGroup = getGroupForScimGroupId(id);
		scimUserGroupModel.setScimUserGroup(id);
		scimUserGroupModel.setUserType(scimUserGroupTypeService.getTypeForCode("Employee"));

		if(CollectionUtils.isNotEmpty(members) && existingScimUserGroup != null && existingScimUserGroup.getUserGroups() != null) {
			final Collection<UserGroupModel> userGroups = existingScimUserGroup.getUserGroups();
			for(final UserGroupModel userGroup : userGroups) {
				for(final ScimGroupMember member : members) {
					userGroupModelCollection = formUserGroups(userGroup, member, userGroupModelCollection);
				}
			}
		}

		if (existingScimUserGroup != null) {
			userGroupModelCollection.addAll(existingScimUserGroup.getUserGroups());
			LOG.debug("Existing groups added");
		}




		 scimUserGroupModel.setUserGroups(userGroupModelCollection);
		 LOG.info("group id:" + scimUserGroupModel.getScimUserGroup());

	}

	private Collection<UserGroupModel> formUserGroups(final UserGroupModel userGroup, final ScimGroupMember member, final Collection<UserGroupModel> userGroupModelCollection) {
		final PrincipalModel user = getUserForScimUserId(member.getValue());
		if(user != null) {
			final Set<PrincipalModel> existingMembers = ScimGroupUtils.formMembersList(userGroup.getMembers());
			existingMembers.add(user);
			userGroup.setMembers(existingMembers);
			modelService.save(userGroup);
			userGroupModelCollection.add(userGroup);
		}
		return userGroupModelCollection;
	}

	private UserModel getUserForScimUserId(final String scimUserId)
	{
		try
		{
			final UserModel exampleUserModel = new UserModel();
			exampleUserModel.setScimUserId(scimUserId);
			final List<UserModel> userModels = flexibleSearchService.getModelsByExample(exampleUserModel);
			return CollectionUtils.isNotEmpty(userModels) ? userModels.get(0) : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No user model found with scimUserId=" + scimUserId, e);
		}
		return null;
	}

	private ScimUserGroupModel getGroupForScimGroupId(final String scimGroupId)
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
			LOG.info("No group model found with scimGroupId=" + scimGroupId, e);
		}
		return null;
	}

	public TypeService getScimUserGroupTypeService()
	{
		return scimUserGroupTypeService;
	}

	public void setScimUserGroupTypeService(final TypeService scimUserGroupTypeService)
	{
		this.scimUserGroupTypeService = scimUserGroupTypeService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}

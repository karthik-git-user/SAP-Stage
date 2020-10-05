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
import de.hybris.platform.scimfacadescep.ScimGroupMeta;
import de.hybris.platform.scimfacadescep.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.scimfacadescep.constants.ScimfacadescepConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Populates ScimGroup model using ScimUserGroupModel information
 */
public class ScimGroupPopulator implements Populator<ScimUserGroupModel, ScimGroup>{

	@Override
	public void populate(final ScimUserGroupModel scimUserGroupModel, final ScimGroup scimGroup) {
		List<ScimGroupMember> scimGroupMembersList = new ArrayList<> ();
		Set<ScimGroupMember> scimGroupMembers = new HashSet<> ();
		scimGroup.setId(scimUserGroupModel.getScimUserGroup());
		scimGroup.setDisplayName(scimUserGroupModel.getScimUserGroup());
		final Collection<UserGroupModel> userGroups = scimUserGroupModel.getUserGroups();
		if (userGroups != null && !userGroups.isEmpty())
		{
			scimGroupMembers = formScimGroupMembers(userGroups, scimGroupMembers);
		}
		scimGroupMembersList.addAll(scimGroupMembers);
		scimGroup.setMembers(scimGroupMembersList);
		final ScimGroupMeta meta = new ScimGroupMeta();
		meta.setVersion(ScimfacadescepConstants.META_VERSION);
		meta.setResourceType(ScimfacadescepConstants.GROUP_RESOURCE_TYPE);
		meta.setCreated(scimUserGroupModel.getCreationtime());
		meta.setLastModified(scimUserGroupModel.getModifiedtime());
		scimGroup.setMeta(meta);
	}

	private Set<ScimGroupMember> formScimGroupMembers(final Collection<UserGroupModel> userGroups, final Set<ScimGroupMember> scimGroupMembers){
		Set<UserModel> usersSet = new HashSet<>();
		for(final UserGroupModel userGroupModel : userGroups) {
			final Set<PrincipalModel> users = ScimGroupUtils.formMembersList(userGroupModel.getMembers());
			for(final PrincipalModel userDetail: users) {
				final UserModel user = (UserModel) userDetail;
				usersSet.add(user);
			}
		}
		
		for(UserModel user: usersSet) {
			final ScimGroupMember newMember = new ScimGroupMember();
			newMember.setValue(user.getScimUserId());
			newMember.setDisplay(user.getDisplayName());
			scimGroupMembers.add(newMember);
		}
		return scimGroupMembers;
	}
}

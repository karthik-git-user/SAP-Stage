/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.group;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;

import java.util.List;

/**
 * Facade to carry out usergroup operations
 */
public interface ScimGroupFacade {

	/**
	 * Create group from scim group
	 *
	 * @param scimGroup
	 *           the scim group object
	 * @return ScimGroup object
	 */
	ScimGroup createGroup(ScimGroup scimGroup);

	/**
	 * Update group for groupId
	 *
	 * @param groupId
	 *           the group id
	 * @param scimGroup
	 *           the scim group object
	 * @return ScimGroup object
	 */
	ScimGroup updateGroup(String groupId, ScimGroup scimGroup);

	/**
	 * Get scim group for groupId
	 *
	 * @param groupId
	 *           the group id
	 * @return ScimGroup object
	 */
	ScimGroup getGroup(String groupId);

	/**
	 * Get all scim groups
	 *
	 * @param groupId
	 *           the group id
	 * @return List<ScimGroup> of scim groups
	 */
	List<ScimGroup> getGroups();

	/**
	 * Delete group
	 *
	 * @param groupId
	 *           the group id
	 * @return OK if successful deleted otherwise error
	 */
	void deleteGroup(String groupId);

	/**
	 * Get group for ScimGroup id
	 *
	 * @param scimGroupId
	 *           the scim group id
	 * @return groupModel group model object
	 */
	ScimUserGroupModel getGroupForScimGroupId(String scimGroupId);

}

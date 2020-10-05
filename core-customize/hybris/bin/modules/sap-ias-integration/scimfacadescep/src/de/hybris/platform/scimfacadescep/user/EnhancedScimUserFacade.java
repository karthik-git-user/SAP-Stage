/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.user;

import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.user.ScimUserFacade;

import java.util.List;


/**
 * Facade to enhance user operations
 */
public interface EnhancedScimUserFacade extends ScimUserFacade
{

	/**
	 * Get all scim users
	 *
	 * @param userId
	 *           the user id
	 * @return List<ScimUser> of scim users
	 */
	List<ScimUser> getUsers();

}

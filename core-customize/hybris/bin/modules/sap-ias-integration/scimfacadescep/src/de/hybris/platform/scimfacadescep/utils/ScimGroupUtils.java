/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.utils;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupList;
import de.hybris.platform.scimfacadescep.ScimUserList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Scim Utility class
 */
public class ScimGroupUtils
{
	private static final Logger LOG = Logger.getLogger(ScimGroupUtils.class);
	/**
	 * Constructor to suppress creation of objects of utility class
	 */
	private ScimGroupUtils()
	{
	}

	public static ScimGroupList searchScimGroupPageData(final ScimGroupList scimPageData)
	{
		final int totalResults = scimPageData.getTotalResults();
		final int itemsPerPage = scimPageData.getItemsPerPage() == 0 ? totalResults : scimPageData.getItemsPerPage();
		int startIndex = scimPageData.getStartIndex() - 1;
		final List<ScimGroup> groups = scimPageData.getResources();
		int iterator = 1;
		final List<ScimGroup> paginatedGroup = new ArrayList<ScimGroup>();
		while (startIndex != totalResults && iterator <= itemsPerPage) {
			paginatedGroup.add(groups.get(startIndex));
			startIndex ++;
			iterator ++;
		}
		scimPageData.setResources(paginatedGroup);
		scimPageData.setItemsPerPage(itemsPerPage);
		return scimPageData;

	}

	public static ScimUserList searchScimUserPageData(final ScimUserList scimPageData)
	{
		final int totalResults = scimPageData.getTotalResults();
		final int itemsPerPage = scimPageData.getItemsPerPage() == 0 ? totalResults : scimPageData.getItemsPerPage();
		int startIndex = scimPageData.getStartIndex() - 1;
		final List<ScimUser> users = scimPageData.getResources();
		int iterator = 1;
		final List<ScimUser> paginatedUsers = new ArrayList<ScimUser>();
		while (startIndex != totalResults && iterator <= itemsPerPage) {
			paginatedUsers.add(users.get(startIndex));
			startIndex ++;
			iterator ++;
		}
		scimPageData.setResources(paginatedUsers);
		scimPageData.setItemsPerPage(itemsPerPage);
		return scimPageData;

	}

	public static Set<PrincipalModel> formMembersList(final Set<PrincipalModel> users)
	{
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>();
		for (final PrincipalModel userDetail : users)
		{
			try
			{
				if (userDetail instanceof UserModel)
				{
					members.add(userDetail);
				}
				else
				{
					final UserGroupModel innerGroup = (UserGroupModel) userDetail;
					formMembersList(innerGroup.getMembers());
				}
			}
			catch (final ClassCastException e)
			{
				LOG.debug("no members exists " + e);
			}
		}
		return members;

	}
}

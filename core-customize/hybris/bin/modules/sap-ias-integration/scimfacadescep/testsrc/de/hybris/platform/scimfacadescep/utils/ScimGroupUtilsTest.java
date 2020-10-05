/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupList;
import de.hybris.platform.scimfacadescep.ScimUserList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class ScimGroupUtilsTest
{
	@Test
	public void testSearchScimUserPageData() {
		final ScimUserList scimPageData = new ScimUserList();
		scimPageData.setTotalResults(2);
		scimPageData.setItemsPerPage(1);
		scimPageData.setStartIndex(1);
		final ScimUser user1 = new ScimUser();
		user1.setActive(true);
		user1.setDisplayName("User1");
		user1.setId("User1");
		final ScimUser user2 = new ScimUser();
		user2.setActive(true);
		user2.setDisplayName("User2");
		user2.setId("User2");
		final List<ScimUser> users = new ArrayList<> ();
		users.add(user1);
		users.add(user2);
		scimPageData.setResources(users);
		final ScimUserList returnedScimPageData = ScimGroupUtils.searchScimUserPageData(scimPageData);
		Assert.assertEquals(returnedScimPageData.getTotalResults(), 2);
		Assert.assertEquals(returnedScimPageData.getItemsPerPage(), 1);
		Assert.assertEquals(returnedScimPageData.getStartIndex(), 1);
		Assert.assertEquals(returnedScimPageData.getResources().get(0), user1);

	}

	@Test
	public void testSearchScimGroupPageData() {
		final ScimGroupList scimPageData = new ScimGroupList();
		scimPageData.setTotalResults(2);
		scimPageData.setItemsPerPage(1);
		scimPageData.setStartIndex(1);
		final ScimGroup group1 = new ScimGroup();
		group1.setDisplayName("Group1");
		group1.setId("Group1");
		final ScimGroup group2 = new ScimGroup();
		group2.setDisplayName("Group2");
		group2.setId("Group2");
		final List<ScimGroup> groups = new ArrayList<> ();
		groups.add(group1);
		groups.add(group2);
		scimPageData.setResources(groups);
		final ScimGroupList returnedScimPageData = ScimGroupUtils.searchScimGroupPageData(scimPageData);
		Assert.assertEquals(returnedScimPageData.getTotalResults(), 2);
		Assert.assertEquals(returnedScimPageData.getItemsPerPage(), 1);
		Assert.assertEquals(returnedScimPageData.getStartIndex(), 1);
		Assert.assertEquals(returnedScimPageData.getResources().get(0), group1);

	}
	
	@Test
	public void testFromMembersList() {
		Set<PrincipalModel> users = new HashSet<>();
		EmployeeModel user = new EmployeeModel();
		user.setUid("User1");
		user.setScimUserId("User1");
		users.add(user);
		final Set<PrincipalModel> returnedMembers = ScimGroupUtils.formMembersList(users);
		for(PrincipalModel member: returnedMembers) {
			EmployeeModel employee = (EmployeeModel)member;
			Assert.assertEquals(employee.getScimUserId(), "User1");
		}
		
	}

}

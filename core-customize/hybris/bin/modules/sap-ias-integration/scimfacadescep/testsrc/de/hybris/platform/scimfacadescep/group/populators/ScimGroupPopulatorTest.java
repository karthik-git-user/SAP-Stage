package de.hybris.platform.scimfacadescep.group.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupMember;
import de.hybris.platform.scimfacadescep.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimGroupPopulatorTest {

	private static final Logger LOG = Logger.getLogger(ScimGroupPopulatorTest.class);

	@InjectMocks
	private final ScimGroupPopulator populator = new ScimGroupPopulator();

	@Mock
	private ScimUserGroupModel scimUserGroup;

	@Mock
	private UserModel user;


	private ScimGroup scimGroup;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testPopulateWithMembers()
	{
		scimGroup = new ScimGroup();


		Mockito.when(scimUserGroup.getScimUserGroup()).thenReturn("external-id");
		Mockito.when(scimUserGroup.getUserGroups()).thenReturn(Collections.singletonList(userGroup));

		Mockito.when(user.getScimUserId()).thenReturn("test-user");
		Mockito.when(user.getDisplayName()).thenReturn("test-name");

		populator.populate(scimUserGroup, scimGroup);
		final ScimGroupMember newMember = new ScimGroupMember();
		final List<ScimGroupMember> scimGroupMembers = new ArrayList<> ();
		final Set<PrincipalModel> users = ScimGroupUtils.formMembersList(userGroup.getMembers());
		for(final PrincipalModel userDetail: users) {
			final UserModel user = (UserModel) userDetail;

			newMember.setValue(user.getScimUserId());
			newMember.setDisplay(user.getDisplayName());
			if(checkIfMemberAlreadyExists(scimGroupMembers, newMember) == false) {
				scimGroupMembers.add(newMember);
			}
		}
		Assert.assertEquals("external-id", scimGroup.getId());
		Assert.assertEquals(scimGroupMembers, scimGroup.getMembers());
	}

	@Test
	public void testPopulateWithoutMembers()
	{
		scimGroup = new ScimGroup();


		Mockito.when(scimUserGroup.getScimUserGroup()).thenReturn("external-id");
		Mockito.when(scimUserGroup.getUserGroups()).thenReturn(null);

		Mockito.when(user.getScimUserId()).thenReturn("test-user");
		Mockito.when(user.getDisplayName()).thenReturn("test-name");
		populator.populate(scimUserGroup, scimGroup);
		Assert.assertEquals("external-id", scimGroup.getId());
	}

	private boolean checkIfMemberAlreadyExists(final List<ScimGroupMember> scimGroupMembers, final ScimGroupMember newMember) {
		if(scimGroupMembers.size() > 0) {
			for(final ScimGroupMember scimGroupMember: scimGroupMembers) {
				if(scimGroupMember.getValue().equals(newMember.getValue())) {
					return true;
				}
			}
		}
		return false;
	}
}

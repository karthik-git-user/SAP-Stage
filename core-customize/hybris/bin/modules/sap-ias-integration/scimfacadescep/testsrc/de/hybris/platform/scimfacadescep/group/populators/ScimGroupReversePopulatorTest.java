package de.hybris.platform.scimfacadescep.group.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupMember;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimGroupReversePopulatorTest {
	
	@InjectMocks
	private final ScimGroupReversePopulator populator = new ScimGroupReversePopulator();

	private ScimUserGroupModel scimUserGroup;

	private ScimGroup scimGroup;

	@Mock
	private UserGroupModel userGroup;

	@Mock
	private  List<ScimGroupMember> scimGroupMember;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private DefaultTypeService scimUserGroupTypeService;

	@Test
	public void testPopulateWithMembers()
	{
		scimUserGroup = new ScimUserGroupModel();
		scimGroup = new ScimGroup();
		scimGroup.setId("external-id");
		scimGroup.setMembers(scimGroupMember);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);
		populator.populate(scimGroup, scimUserGroup);
		Assert.assertEquals("external-id", scimUserGroup.getScimUserGroup());
	}

	@Test
	public void testPopulateWithoutMembers()
	{
		scimUserGroup = new ScimUserGroupModel();
		scimGroup = new ScimGroup();
		scimGroup.setId("external-id");
		scimGroup.setMembers(null);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);
		populator.populate(scimGroup, scimUserGroup);
		Assert.assertEquals("external-id", scimUserGroup.getScimUserGroup());
	}
}

package de.hybris.platform.scimfacadescep.group.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;

import java.util.Collections;

import org.apache.http.protocol.HTTP;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScimGroupFacadeTest {
	
	private static final String EXTERNAL_ID = "external-id";

	@InjectMocks
	private final DefaultScimGroupFacade scimGroupFacade = new DefaultScimGroupFacade();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private DefaultTypeService scimUserGroupTypeService;

	@Mock
	private ScimUserGroupModel scimUserGroupModel;

	@Mock
	private Converter<ScimUserGroupModel, ScimGroup> scimGroupConverter;

	@Mock
	private ModelService modelService;

	@Mock
	private Converter<ScimGroup, ScimUserGroupModel> scimGroupReverseConverter;

	@Mock
	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Test
	public void testCreateGroup()
	{
		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		scimGroup.setDisplayName("testGroup");

		Mockito.when(modelService.create(ScimUserGroupModel.class)).thenReturn(scimUserGroupModel);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		Mockito.when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		final ScimGroup returnedScimGroup = scimGroupFacade.createGroup(scimGroup);

		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		Mockito.verify(modelService).save(scimUserGroupModel);
		Assert.assertEquals(scimGroup, returnedScimGroup);
	}


	@Test
	public void testUpdateGroupWhenGroupExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		Mockito.when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		final ScimGroup returnedScimGroup = scimGroupFacade.updateGroup(EXTERNAL_ID, scimGroup);

		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		Mockito.verify(modelService).save(scimUserGroupModel);
		Assert.assertEquals(scimGroup, returnedScimGroup);
	}

	@Test(expected = ScimException.class)
	public void testUpdateGroupWhenGroupDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.updateGroup(EXTERNAL_ID, new ScimGroup());
	}

	@Test
	public void testGetGroupWhenGroupExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroup(EXTERNAL_ID);

		Mockito.verify(scimGroupConverter).convert(scimUserGroupModel);
	}

	@Test(expected = ScimException.class)
	public void testGetGroupWhenGroupDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.getGroup(EXTERNAL_ID);
	}

	@Test
	public void testGetGroupForScimGroupIdWhenModelExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroup(EXTERNAL_ID);
		Assert.assertEquals(scimUserGroupModel, scimGroupFacade.getGroupForScimGroupId(EXTERNAL_ID));
	}

	@Test(expected = ScimException.class)
	public void testGetGroupForScimGroupIdWhenModelDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenThrow(new ModelNotFoundException("exception"));
		scimGroupFacade.getGroup(EXTERNAL_ID);
		Assert.assertNull(scimGroupFacade.getGroupForScimGroupId(EXTERNAL_ID));
	}

	@Test
	public void testDeleteGroupWhenGroupExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		scimGroupFacade.deleteGroup(EXTERNAL_ID);
		Mockito.verify(modelService, Mockito.times(1)).remove(scimUserGroupModel);
	}

	@Test(expected = ScimException.class)
	public void testDeleteGroupWhenGroupDoesntExists()
	{
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.deleteGroup(EXTERNAL_ID);
	}

	@Test
	public void testGetGroups()
	{
		
		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		scimGroup.setDisplayName("testGroup");
		
		Mockito.when(modelService.create(ScimUserGroupModel.class)).thenReturn(scimUserGroupModel);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		Mockito.when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		scimGroupFacade.createGroup(scimGroup);
		
		Mockito.when(scimUserGroupGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroups();

		Mockito.verify(scimGroupConverter).convert(scimUserGroupModel);
		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		
	}

}

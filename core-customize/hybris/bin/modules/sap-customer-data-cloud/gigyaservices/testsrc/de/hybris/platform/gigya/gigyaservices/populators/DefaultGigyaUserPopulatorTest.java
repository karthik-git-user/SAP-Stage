/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaUserPopulatorTest {

	@InjectMocks
	private final DefaultGigyaUserPopulator populator = new DefaultGigyaUserPopulator();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaConfigModel firstGigyaConfig;

	@Mock
	private GigyaConfigModel secondGigyaConfig;

	@Test
	public void testPopulateWithSingleMapping() throws GSKeyNotFoundException 
	{
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));

		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.when(fieldMapping.getHybrisType()).thenReturn(GyAttributeType.STRING);
		Mockito.when(fieldMapping.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping.getHybrisAttributeName()).thenReturn("description");
		Mockito.when(fieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.when(firstGigyaConfig.getGigyaApiKey()).thenReturn("test-api");

		Mockito.when(gigyaUser.getUid()).thenReturn("uid");
		Mockito.when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.when(gigyaUser.getName()).thenReturn("user name");
		Mockito.when(gigyaUser.getGyApiKey()).thenReturn("test-api");
		final String[] names = new String[2];
		names[0] = "user";
		names[1] = "name";
		Mockito.when(customerNameStrategy.splitName("user name")).thenReturn(names);

		Mockito.when(modelService.getAttributeValue(gigyaUser, "description")).thenReturn("description");

		final GSObject gsObject = new GSObject();

		populator.populate(gigyaUser, gsObject);

		Assert.assertEquals("gigya-uid", gsObject.getString("UID"));
		Assert.assertTrue(gsObject.containsKey("profile"));
		final GSObject profileObject = (GSObject) gsObject.get("profile");
		Assert.assertEquals("user", profileObject.getString("firstName"));
		Assert.assertEquals("name", profileObject.getString("lastName"));
		Assert.assertEquals("uid", profileObject.getString("email"));
		Assert.assertEquals("description", profileObject.getString("description"));
	}

	@Test
	public void testPopulateWithMultipleMappings() throws GSKeyNotFoundException 
	{

		Mockito.when(firstGigyaConfig.getGigyaApiKey()).thenReturn("test-api");

		final GigyaFieldMappingModel fieldMapping1 = creatFieldMappingModel(GigyaSyncDirection.H2G,
				GyAttributeType.STRING, "profile.description", "description", firstGigyaConfig);

		final GigyaFieldMappingModel fieldMapping2 = creatFieldMappingModel(GigyaSyncDirection.H2G,
				GyAttributeType.BOOLEAN, "isVerified", "isVerified", secondGigyaConfig);

		final GigyaFieldMappingModel fieldMapping3 = creatFieldMappingModel(GigyaSyncDirection.G2H,
				GyAttributeType.BOOLEAN, "isVerified", "isVerified", firstGigyaConfig);

		Mockito.when(gigyaFieldMappingGenericDao.find())
				.thenReturn(Arrays.asList(fieldMapping1, fieldMapping2, fieldMapping3));

		Mockito.when(firstGigyaConfig.getGigyaApiKey()).thenReturn("test-api");

		Mockito.when(gigyaUser.getUid()).thenReturn("uid");
		Mockito.when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.when(gigyaUser.getName()).thenReturn("user name");
		Mockito.when(gigyaUser.getGyApiKey()).thenReturn("test-api");

		final String[] names = new String[2];
		names[0] = "user";
		names[1] = "name";
		Mockito.when(customerNameStrategy.splitName("user name")).thenReturn(names);

		Mockito.when(modelService.getAttributeValue(gigyaUser, "description")).thenReturn("description");

		final GSObject gsObject = new GSObject();

		populator.populate(gigyaUser, gsObject);

		Assert.assertEquals("gigya-uid", gsObject.getString("UID"));
		Assert.assertTrue(gsObject.containsKey("profile"));
		final GSObject profileObject = (GSObject) gsObject.get("profile");
		Assert.assertEquals("user", profileObject.getString("firstName"));
		Assert.assertEquals("name", profileObject.getString("lastName"));
		Assert.assertEquals("uid", profileObject.getString("email"));
		Assert.assertEquals("description", profileObject.getString("description"));
	}

	private GigyaFieldMappingModel creatFieldMappingModel(GigyaSyncDirection syncDirection,
			GyAttributeType gyAttributeType, String gyAttributeName, String hybrisAttrName,
			GigyaConfigModel gigyaConfig) 
	{
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(syncDirection);
		Mockito.when(fieldMapping.getHybrisType()).thenReturn(gyAttributeType);
		Mockito.when(fieldMapping.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping.getHybrisAttributeName()).thenReturn("description");
		Mockito.when(fieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);
		return fieldMapping;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaUserReversePopulatorTest {

	@InjectMocks
	private final DefaultGigyaUserReversePopulator populator = new DefaultGigyaUserReversePopulator();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GSObject gsObject;

	@Mock
	private GigyaConfigModel firstGigyaConfig;

	@Mock
	private GigyaConfigModel secondGigyaConfig;

	@Test
	public void testPopulateWhenResponseHasNoData() {
		Mockito.when(gsResponse.hasData()).thenReturn(false);

		populator.populate(gsResponse, gigyaUser);

		Mockito.verifyZeroInteractions(gigyaUser);
	}

	@Test
	public void testPopulateWhenResponseHasData() throws GSKeyNotFoundException {
		Mockito.when(gsResponse.hasData()).thenReturn(true);

		final GigyaFieldMappingModel fieldMapping = creatFieldMappingModel(GigyaSyncDirection.G2H,
				GyAttributeType.STRING, "profile.description", "description", firstGigyaConfig);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));

		Mockito.when(firstGigyaConfig.getGigyaApiKey()).thenReturn("test-api");
		Mockito.when(gigyaUser.getGyApiKey()).thenReturn("test-api");

		Mockito.when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.when(gsObject.containsKey("profile")).thenReturn(true);
		Mockito.when(gsObject.getObject("profile")).thenReturn(gsObject);
		Mockito.when(gsObject.getString("description")).thenReturn("some-desc");
		Mockito.when(gsObject.containsKey("description")).thenReturn(true);

		populator.populate(gsResponse, gigyaUser);

		Mockito.verify(modelService).setAttributeValue(gigyaUser, "description", "some-desc");
	}

	@Test
	public void testPopulateWhenResponseHasDataWithMultipleMappings()
			throws GSKeyNotFoundException, InvalidClassException, NullPointerException {
		Mockito.when(gsResponse.hasData()).thenReturn(true);
		Mockito.when(firstGigyaConfig.getGigyaApiKey()).thenReturn("test-api");
		Mockito.when(secondGigyaConfig.getGigyaApiKey()).thenReturn("test-api-2");

		final GigyaFieldMappingModel fieldMapping1 = creatFieldMappingModel(GigyaSyncDirection.G2H,
				GyAttributeType.STRING, "profile.description", "description", firstGigyaConfig);

		final GigyaFieldMappingModel fieldMapping2 = creatFieldMappingModel(GigyaSyncDirection.G2H,
				GyAttributeType.BOOLEAN, "isVerified", "isVerified", secondGigyaConfig);

		final GigyaFieldMappingModel fieldMapping3 = creatFieldMappingModel(GigyaSyncDirection.H2G,
				GyAttributeType.BOOLEAN, "isVerified", "isVerified", firstGigyaConfig);

		Mockito.when(gigyaFieldMappingGenericDao.find())
				.thenReturn(Arrays.asList(fieldMapping1, fieldMapping2, fieldMapping3));

		Mockito.when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.when(gsObject.containsKey("profile")).thenReturn(true);
		Mockito.when(gsObject.getObject("profile")).thenReturn(gsObject);
		Mockito.when(gsObject.getString("description")).thenReturn("some-desc");
		Mockito.when(gsObject.containsKey("description")).thenReturn(true);
		Mockito.when(gsObject.containsKey("isVerified")).thenReturn(true);
		Mockito.when(gsObject.getBool("isVerified")).thenReturn(true);

		Mockito.when(gigyaUser.getGyApiKey()).thenReturn("test-api");

		populator.populate(gsResponse, gigyaUser);

		Mockito.verify(modelService).setAttributeValue(gigyaUser, "description", "some-desc");
		Mockito.verify(modelService, Mockito.times(0)).setAttributeValue(gigyaUser, "isVerified", true);
	}

	private GigyaFieldMappingModel creatFieldMappingModel(GigyaSyncDirection syncDirection,
			GyAttributeType gyAttributeType, String gyAttributeName, String hybrisAttrName,
			GigyaConfigModel gigyaConfig) {
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(syncDirection);
		Mockito.when(fieldMapping.getHybrisType()).thenReturn(gyAttributeType);
		Mockito.when(fieldMapping.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping.getHybrisAttributeName()).thenReturn("description");
		Mockito.when(fieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);
		return fieldMapping;
	}

}

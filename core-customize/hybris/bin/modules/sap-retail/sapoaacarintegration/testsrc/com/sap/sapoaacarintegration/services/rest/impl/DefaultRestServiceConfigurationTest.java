/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.retail.oaa.model.constants.SapoaamodelConstants;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;




/**
 *
 */
public class DefaultRestServiceConfigurationTest
{

	private static final String TARGETURL = "http://www.sap.com";
	private static final String USERID = "user";
	private static final String PASSWORD = "password";
	private static final String SAP_OAA_PROFILE = "SapOaaProfileId";
	private static final String SAP_SALES_CHANNEL = "SALES_CHANNEL";
	private static final String SAP_CONSUMER = "CONSUMER";
	private static final String SAP_CLIENT = "800";
	private static final String SAP_ITEM_CATEGORY = "TAS";


	private DefaultRestServiceConfiguration classUnderTest = null;

	@Before
	public void setUp()
	{
		classUnderTest = EasyMock.createMockBuilder(DefaultRestServiceConfiguration.class).addMockedMethod("readProperties")
				.createMock();
	}

	@Test
	public void testInitializationFallbackMode()
	{
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		final SAPConfigurationService sapCoreConfigurationServiceMock = createSapCoreConfigurationServiceMock();

		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);
		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		classUnderTest.initializeConfiguration();
		assertEquals(USERID, classUnderTest.getUser());
		assertEquals(TARGETURL, classUnderTest.getTargetUrl());
		assertEquals(PASSWORD, classUnderTest.getPassword());
		assertEquals(SAP_OAA_PROFILE, classUnderTest.getOaaProfile());
		assertEquals(SAP_ITEM_CATEGORY, classUnderTest.getItemCategory());
	}

	@Test
	public void testInitializationSalesChannelMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: SalesChannel is maintained in Sales Channel Mode
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_SALESCHANNEL))
				.andReturn(SAP_SALES_CHANNEL);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_MODE))
				.andReturn(Sapoaa_mode.SALESCHANNEL).anyTimes();
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_VENDOR_ITEM_CATEGORY))
				.andReturn(SAP_ITEM_CATEGORY);
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		classUnderTest.initializeConfiguration();
		assertEquals(USERID, classUnderTest.getUser());
		assertEquals(TARGETURL, classUnderTest.getTargetUrl());
		assertEquals(PASSWORD, classUnderTest.getPassword());
		assertEquals(SAP_SALES_CHANNEL, classUnderTest.getSalesChannel());
		assertEquals(SAP_ITEM_CATEGORY, classUnderTest.getItemCategory());
	}


	@Test
	public void initializeGlobalConfigurationNoCARConfigTest()
	{
		// Mock SapGlobalConfigurationService
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = EasyMock
				.createNiceMock(SAPGlobalConfigurationService.class);

		EasyMock.expect(Boolean.valueOf(sapGlobalConfigurationServiceMock.sapGlobalConfigurationExists())).andReturn(Boolean.TRUE);
		EasyMock.expect(sapGlobalConfigurationServiceMock.getProperty(SapoaamodelConstants.SAPOAA_CARCLIENT)).andReturn(SAP_CLIENT);
		EasyMock.expect(sapGlobalConfigurationServiceMock.getAllPropertyAccesses()).andReturn(new HashMap<>()).anyTimes();
		EasyMock.replay(sapGlobalConfigurationServiceMock);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		final SAPConfigurationService sapCoreConfigurationServiceMock = createSapCoreConfigurationServiceMock();
		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing SAP CAR config settings: " + SapoaamodelConstants.SAPOAA_CARCLIENT);
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}

	}

	@Test
	public void initializeGlobalConfigurationMissingUserIDTest()
	{
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(null,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		final SAPConfigurationService sapCoreConfigurationServiceMock = createSapCoreConfigurationServiceMock();
		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing SAP CAR HTTP config setting: " + SAPHTTPDestinationModel.USERID);
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}


	@Test
	public void initializeGlobalConfigurationMissingTargetURLTest()
	{
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				null, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		final SAPConfigurationService sapCoreConfigurationServiceMock = createSapCoreConfigurationServiceMock();
		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing SAP CAR HTTP config setting: " + SAPHTTPDestinationModel.TARGETURL);
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingPasswordTest()
	{
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, null);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		final SAPConfigurationService sapCoreConfigurationServiceMock = createSapCoreConfigurationServiceMock();
		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing SAP CAR HTTP config setting: " + SAPHTTPDestinationModel.PASSWORD);
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingOAAProfileFallbackMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: No OAA Profile is maintained when no mode is maintained
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_CONSUMERID))
				.andReturn(SAP_CONSUMER);
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		//THEN: Expect an exception
		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing OAA Profile");
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingConsumerFallbackMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: No Consumer is maintained when no mode is maintained
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_OAAPROFILE))
				.andReturn(SAP_OAA_PROFILE);
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		//THEN: Expect an exception
		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing Consumer");
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingOAAProfileOAAProfileMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: No OAA Profile is maintained when OAA Profile mode is maintained
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_CONSUMERID))
				.andReturn(SAP_CONSUMER);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_MODE))
				.andReturn(Sapoaa_mode.OAAPROFILE).anyTimes();
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		//THEN: Expect an exception
		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing OAA Profile");
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingConsumerOAAProfileMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: No Consumer is maintained when OAA Profile mode is maintained
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_OAAPROFILE))
				.andReturn(SAP_OAA_PROFILE);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_MODE))
				.andReturn(Sapoaa_mode.OAAPROFILE).anyTimes();
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		//THEN: Expect an exception
		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing Consumer");
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void initializeGlobalConfigurationMissingSalesChannelSalesChannelMode()
	{
		//GIVEN: Global Configuration with sufficent data
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = createSapGlobalConfigurationServiceMock(USERID,
				TARGETURL, PASSWORD);
		classUnderTest.setSapGlobalConfigurationService(sapGlobalConfigurationServiceMock);

		//WHEN: No SalesChannel is maintained when SalesChannel mode is maintained
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_MODE))
				.andReturn(Sapoaa_mode.SALESCHANNEL).anyTimes();
		EasyMock.replay(sapCoreConfigurationServiceMock);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationServiceMock);

		//THEN: Expect an exception
		try
		{
			classUnderTest.initializeConfiguration();
			Assert.fail("Exception expected due to missing Consumer");
		}
		catch (final RestInitializationException e)
		{
			Assert.assertNotNull(e);
		}
	}

	/**
	 * @return SAPConfigurationService
	 */
	private SAPConfigurationService createSapCoreConfigurationServiceMock()
	{
		final SAPConfigurationService sapCoreConfigurationServiceMock = EasyMock.createNiceMock(SAPConfigurationService.class);

		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_OAAPROFILE))
				.andReturn(SAP_OAA_PROFILE);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_CONSUMERID))
				.andReturn(SAP_CONSUMER);
		EasyMock.expect(sapCoreConfigurationServiceMock.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_VENDOR_ITEM_CATEGORY))
				.andReturn(SAP_ITEM_CATEGORY);
		EasyMock.replay(sapCoreConfigurationServiceMock);
		return sapCoreConfigurationServiceMock;
	}

	/**
	 *
	 */
	private Map<String, ConfigurationPropertyAccess> createPropertyAccessStub(final String user, final String targetURL,
			final String password)
	{
		// Build mock for SAP HTTP Destination
		final Map<String, ConfigurationPropertyAccess> configMap = new HashMap<>();
		final ConfigurationPropertyAccess carHttpDestinationMock = EasyMock.createNiceMock(ConfigurationPropertyAccess.class);
		EasyMock.expect(carHttpDestinationMock.getProperty(SAPHTTPDestinationModel.USERID)).andReturn(user).anyTimes();
		EasyMock.expect(carHttpDestinationMock.getProperty(SAPHTTPDestinationModel.TARGETURL)).andReturn(targetURL).anyTimes();
		EasyMock.expect(carHttpDestinationMock.getProperty(SAPHTTPDestinationModel.PASSWORD)).andReturn(password).anyTimes();
		EasyMock.replay(carHttpDestinationMock);
		configMap.put(SapoaamodelConstants.SAPOAA_CARHTTPDESTINATION, carHttpDestinationMock);

		return configMap;
	}

	/**
	 * @return ConfigurationPropertyAccess
	 */
	private SAPGlobalConfigurationService createSapGlobalConfigurationServiceMock(final String user, final String targetURL,
			final String password)
	{
		final SAPGlobalConfigurationService sapGlobalConfigurationServiceMock = EasyMock
				.createNiceMock(SAPGlobalConfigurationService.class);

		EasyMock.expect(Boolean.valueOf(sapGlobalConfigurationServiceMock.sapGlobalConfigurationExists())).andReturn(Boolean.TRUE);

		EasyMock.expect(sapGlobalConfigurationServiceMock.getProperty(SapoaamodelConstants.SAPOAA_CARCLIENT)).andReturn(SAP_CLIENT);

		final Map<String, ConfigurationPropertyAccess> configMap = createPropertyAccessStub(user, targetURL, password);
		EasyMock.expect(sapGlobalConfigurationServiceMock.getAllPropertyAccesses()).andReturn(configMap).anyTimes();

		EasyMock.replay(sapGlobalConfigurationServiceMock);
		return sapGlobalConfigurationServiceMock;
	}

}

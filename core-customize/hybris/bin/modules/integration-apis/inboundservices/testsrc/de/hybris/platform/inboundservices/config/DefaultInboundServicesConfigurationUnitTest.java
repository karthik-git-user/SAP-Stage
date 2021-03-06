/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.inboundservices.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultInboundServicesConfigurationUnitTest
{
	private static final String SUCCESS_RETENTION_PROPERTY_KEY = "inboundservices.monitoring.success.payload.retention";
	private static final String ERROR_RETENTION_PROPERTY_KEY = "inboundservices.monitoring.error.payload.retention";
	private static final String MONITORING_ENABLED_KEY = "inboundservices.monitoring.enabled";

	@Mock
	private Configuration configuration;
	@Mock
	private ConfigurationService configurationService;

	@InjectMocks
	private DefaultInboundServicesConfiguration inboundServicesConfiguration;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void testSuccessRetention()
	{
		when(configuration.getBoolean(SUCCESS_RETENTION_PROPERTY_KEY)).thenReturn(true);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).isTrue();
	}

	@Test
	public void testSuccessRetentionConversionExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new ConversionException())
				.when(configuration).getBoolean(SUCCESS_RETENTION_PROPERTY_KEY);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).isFalse();
	}

	@Test
	public void testSuccessRetentionNoSuchElementExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new NoSuchElementException())
				.when(configuration).getBoolean(SUCCESS_RETENTION_PROPERTY_KEY);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).isFalse();
	}

	@Test
	public void testErrorRetention()
	{
		when(configuration.getBoolean(ERROR_RETENTION_PROPERTY_KEY)).thenReturn(false);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).isFalse();
	}

	@Test
	public void testErrorRetentionConversionExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new ConversionException())
				.when(configuration).getBoolean(ERROR_RETENTION_PROPERTY_KEY);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).isTrue();
	}

	@Test
	public void testErrorRetentionNoSuchElementExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new NoSuchElementException())
				.when(configuration).getBoolean(ERROR_RETENTION_PROPERTY_KEY);
		assertThat(inboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).isTrue();
	}

	@Test
	public void testMonitoringEnabled()
	{
		when(configuration.getBoolean(MONITORING_ENABLED_KEY)).thenReturn(true);
		assertThat(inboundServicesConfiguration.isMonitoringEnabled()).isTrue();
	}

	@Test
	public void testMonitoringDisabled()
	{
		when(configuration.getBoolean(MONITORING_ENABLED_KEY)).thenReturn(false);
		assertThat(inboundServicesConfiguration.isMonitoringEnabled()).isFalse();
	}

	@Test
	public void testMonitoringEnabledConversionExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new ConversionException())
				.when(configuration).getBoolean(MONITORING_ENABLED_KEY);
		assertThat(inboundServicesConfiguration.isMonitoringEnabled()).isTrue();
	}

	@Test
	public void testMonitoringEnabledNoSuchElementExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new NoSuchElementException())
				.when(configuration).getBoolean(MONITORING_ENABLED_KEY);
		assertThat(inboundServicesConfiguration.isMonitoringEnabled()).isTrue();
	}
}

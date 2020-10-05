/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponse;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseItem;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseItemScheduleLine;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseSource;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosSourcingResponseMapperTest
{
	@InjectMocks
	DefaultCosSourcingResponseMapper defaultCosSourcingResponseMapper;

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	private final CosSourcingResponse cosSourcingResponse = new CosSourcingResponse();
	private final CosSourcingResponseSource cosSourcingResponseSource = new CosSourcingResponseSource();
	private final CosSourcingResponseItemScheduleLine cosSourcingResponseItemScheduleLine = new CosSourcingResponseItemScheduleLine();
	private final List<CosSourcingResponseItemScheduleLine> responseItemScheduleLine = new ArrayList<>();
	private final CosSourcingResponseItem cosSourcingResponseItem = new CosSourcingResponseItem();
	private final List<CosSourcingResponseItem> listCosSourcingResponseItem = new ArrayList<>();
	private final List<List<CosSourcingResponseItem>> responseItems = new ArrayList<>();
	private final Date availableFrom = new Date();
	private final String SOURCE_ID = "ID123";
	private final String ITEM_ID = "ID345";

	@Before
	public void setUp()
	{
		//CosSourcingResponseSource
		cosSourcingResponseSource.setSourceType(SapoaacosintegrationConstants.SOURCETYPE_THIRD_PARTY);
		cosSourcingResponseSource.setSourceId(SOURCE_ID);

		//CosSourcingResponseItemScheduleLine
		cosSourcingResponseItemScheduleLine.setSource(cosSourcingResponseSource);
		cosSourcingResponseItemScheduleLine.setAvailableFrom(availableFrom);
		cosSourcingResponseItemScheduleLine.setQuantity(1000d);


		//ResponseItemScheduleLine
		responseItemScheduleLine.add(cosSourcingResponseItemScheduleLine);

		//CosSourcingResponseItem
		cosSourcingResponseItem.setResponseItemScheduleLine(responseItemScheduleLine);
		cosSourcingResponseItem.setItemId(ITEM_ID);

		//ListCosSourcingResponseItem
		listCosSourcingResponseItem.add(cosSourcingResponseItem);

		//ResponseItems
		responseItems.add(listCosSourcingResponseItem);

		//CosSourcingResponse
		cosSourcingResponse.setSourcings(responseItems);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(any(String.class), any(String.class))).thenReturn("yyyy-MM-dd");
	}

	@Test
	public void mapCosSourcingResponseToSourcingResponseTest()
	{
		//Setup
		final SourcingResponse sourcingResponse;
		defaultCosSourcingResponseMapper.setConfigurationService(configurationService);

		//Execute
		sourcingResponse = defaultCosSourcingResponseMapper.mapCosSourcingResponseToSourcingResponse(cosSourcingResponse);

		//Verify
		Assert.assertNotNull(sourcingResponse);
		Assert.assertNotNull(sourcingResponse.getSourcingResults());
		Assert.assertEquals(SapoaacosintegrationConstants.ABAP_TRUE, sourcingResponse.getReservationSuccessful());
	}
}

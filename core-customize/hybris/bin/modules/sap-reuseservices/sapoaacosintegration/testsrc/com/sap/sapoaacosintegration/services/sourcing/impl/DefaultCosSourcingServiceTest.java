/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;
import com.sap.sapoaacosintegration.services.reservation.ReservationService;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingRequestMapper;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResponseMapper;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResultHandler;
import com.sap.sapoaacosintegration.services.sourcing.request.CosSourcingItem;
import com.sap.sapoaacosintegration.services.sourcing.request.CosSourcingRequest;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponse;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseItem;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosSourcingServiceTest
{
	@Mock
	CosSourcingResultHandler cosSourcingResultHandler;

	@Mock
	IntegrationRestTemplateFactory integrationRestTemplateFactory;

	@Mock
	CosServiceUtils cosServiceUtils;

	@Mock
	CosSourcingRequestMapper cosSourcingRequestMapper;

	@Mock
	CosSourcingResponseMapper cosSourcingResponseMapper;

	@Mock
	CosConfigurationService configurationService;

	@Mock
	ReservationService reservationService;

	@Mock
	SessionService sessionService;

	@Mock
	RestOperations restOperations;

	@Mock
	ResponseEntity<CosSourcingResponse> cosSourcingResponse;

	@InjectMocks
	DefaultCosSourcingService defaultCosSourcingService;

	private static final Logger LOG = Logger.getLogger(DefaultCosSourcingService.class);
	private final CartModel abstractOrderModel = new CartModel();
	private final CosSourcingRequest cosSourcingRequest = new CosSourcingRequest();
	private final List<CosSourcingItem> listCosSourcingItems = new ArrayList<>();
	private final CosSourcingItem cosSourcingItem = new CosSourcingItem();
	private final ConsumedDestinationModel destination = new ConsumedDestinationModel();
	private final ReservationResponse reservationResponse = new ReservationResponse();
	private final CosSourcingResponse value = new CosSourcingResponse();
	private final List<CosSourcingResponseItem> listCosSourcingResponseItem = new ArrayList<>();
	private final List<List<CosSourcingResponseItem>> responseItems = new ArrayList<>();
	private final SourcingResponse sourcingResponse = new SourcingResponse();

	@Before
	public void setUp()
	{
		defaultCosSourcingService.setCosSourcingResultHandler(cosSourcingResultHandler);
		defaultCosSourcingService.setCosSourcingRequestMapper(cosSourcingRequestMapper);
		defaultCosSourcingService.setCosSourcingResponseMapper(cosSourcingResponseMapper);
		defaultCosSourcingService.setIntegrationRestTemplateFactory(integrationRestTemplateFactory);
		defaultCosSourcingService.setConfigurationService(configurationService);
		defaultCosSourcingService.setReservationService(reservationService);
		defaultCosSourcingService.setCosServiceUtils(cosServiceUtils);

		//Mocks
		doNothing().when(sessionService).setAttribute(anyString(), anyBoolean());
		doNothing().when(cosSourcingResultHandler).persistCosSourcingResultInCart(any(SourcingResponse.class),
				any(AbstractOrderModel.class));
		when(cosSourcingRequestMapper.prepareCosSourcingRequest(any(AbstractOrderModel.class))).thenReturn(cosSourcingRequest);
		when(reservationService.updateReservation(any(AbstractOrderModel.class), anyString())).thenReturn(reservationResponse);
		when(integrationRestTemplateFactory.create(any(ConsumedDestinationModel.class))).thenReturn(restOperations);
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CosSourcingResponse.class)))
				.thenReturn(cosSourcingResponse);
		when(cosSourcingResponse.getBody()).thenReturn(value);
		when(cosSourcingResponseMapper.mapCosSourcingResponseToSourcingResponse(any(CosSourcingResponse.class)))
				.thenReturn(sourcingResponse);
		when(cosServiceUtils.getConsumedDestinationModelById(anyString())).thenReturn(destination);

		//ResponseItems
		responseItems.add(listCosSourcingResponseItem);

		//CosSourcingResponse
		value.setSourcings(responseItems);

		//ConsumedDestinationModel
		destination.setId("ID123");

		//ListCosSourcingItems
		listCosSourcingItems.add(cosSourcingItem);

		//CosSourcingRequest
		cosSourcingRequest.setItems(listCosSourcingItems);

		abstractOrderModel.setIsCosOrderAcknowledgedByBackend(Boolean.FALSE);
	}

	@Test
	public void callRestServiceAndPersistResultTest()
	{
		//Execute
		defaultCosSourcingService.callRestServiceAndPersistResult(abstractOrderModel);

		//Verify
		Assert.assertEquals(true, abstractOrderModel.getCosReservationExpireFlag());
	}

	@Test
	public void callRestServiceTest()
	{
		//SetUp
		final SourcingResponse sourcingResponse;

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callRestServiceTest_HttpClientErrorException()
	{
		//SetUp
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		final SourcingResponse sourcingResponse;
		when(cosSourcingRequestMapper.prepareCosSourcingRequest(any(AbstractOrderModel.class))).thenThrow(httpClientErrorException);

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callRestServiceTest_AuthenticationServiceException()
	{
		//SetUp
		final SourcingResponse sourcingResponse;
		when(cosSourcingRequestMapper.prepareCosSourcingRequest(any(AbstractOrderModel.class)))
				.thenThrow(new AuthenticationServiceException());

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callRestServiceTest_ResourceAccessException()
	{
		//SetUp
		final SourcingResponse sourcingResponse;
		when(cosSourcingRequestMapper.prepareCosSourcingRequest(any(AbstractOrderModel.class)))
				.thenThrow(new ResourceAccessException("Resource Cannot be accessed"));

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callRestServiceTest_ModelNotFoundException()
	{
		//SetUp
		final SourcingResponse sourcingResponse;
		when(cosSourcingRequestMapper.prepareCosSourcingRequest(any(AbstractOrderModel.class)))
				.thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}


	@Test
	public void callCosRestServiceTest()
	{
		//SetUp
		final SourcingResponse sourcingResponse;

		//Execute
		sourcingResponse = defaultCosSourcingService.callCosRestService(abstractOrderModel);

		//verify
		Assert.assertEquals(true, abstractOrderModel.getCosReservationExpireFlag());
	}

	@Test(expected = SourcingException.class)
	public void callCosRestServiceTest_HttpClientErrorException()
	{
		//SetUp
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		final SourcingResponse sourcingResponse;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CosSourcingResponse.class)))
				.thenThrow(httpClientErrorException);

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callCosRestServiceTest_ResourceAccessException()
	{
		//SetUp
		final SourcingResponse sourcingResponse;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CosSourcingResponse.class)))
				.thenThrow(new ResourceAccessException("Resource Cannot be accessed"));

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

	@Test(expected = SourcingException.class)
	public void callCosRestServiceTest_ModelNotFoundException()
	{
		//SetUp
		final SourcingResponse sourcingResponse;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(CosSourcingResponse.class)))
				.thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		sourcingResponse = defaultCosSourcingService.callRestService(abstractOrderModel, false, true);
	}

}

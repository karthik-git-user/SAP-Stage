/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.reservation.CosReservationRequestMapper;
import com.sap.sapoaacosintegration.services.reservation.CosReservationResultHandler;
import com.sap.sapoaacosintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequest;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequestItem;
import com.sap.sapoaacosintegration.services.reservation.response.CosReservationResponse;
import com.sap.sapoaacosintegration.services.rest.impl.DefaultAbstractCosRestService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosReservationServiceTest
{
	@Mock
	CosReservationResultHandler reservationResultHandler;

	@Mock
	CosReservationRequestMapper cosReservationRequestMapper;

	@Mock
	ConfigurationService configurationService;

	@Mock
	IntegrationRestTemplateFactory integrationRestTemplateFactory;

	@Mock
	CosServiceUtils cosServiceUtils;

	@Mock
	RestOperations restOperations;

	@Mock
	ResponseEntity<List<CosReservationResponse>> response;

	@Mock
	CosReservationRequest request;

	@Mock
	DefaultAbstractCosRestService defaultAbstractRestService;

	@Mock
	SessionService sessionService;

	@Mock
	Configuration configuration;

	@InjectMocks
	DefaultCosReservationService defaultCosReservationService;

	private final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private final List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
	private final String reservationStatus = SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER;
	private final String cartItemId = SapoaacosintegrationConstants.CART_ITEM_ID;
	private final ConsumedDestinationModel consumedDestinationModel = new ConsumedDestinationModel();
	private final List<CosReservationResponse> listCosReservationResponse = new ArrayList<>();
	private final List<CosReservationRequestItem> listCosReservationRequestItem = new ArrayList<>();


	@Before
	public void setUp()
	{
		defaultCosReservationService.setCosServiceUtils(cosServiceUtils);
		defaultCosReservationService.setIntegrationRestTemplateFactory(integrationRestTemplateFactory);
		defaultCosReservationService.setReservationResultHandler(reservationResultHandler);
		defaultCosReservationService.setCosRequestMapper(cosReservationRequestMapper);
		defaultCosReservationService.setConfigurationService(configurationService);


		//Mocks
		when(cosServiceUtils.getConsumedDestinationModelById(anyString())).thenReturn(consumedDestinationModel);
		when(integrationRestTemplateFactory.create(any(ConsumedDestinationModel.class))).thenReturn(restOperations);
		when(cosReservationRequestMapper.mapOrderModelToReservationRequest(any(AbstractOrderModel.class),
				any(CosReservationRequest.class), anyString())).thenReturn(listCosReservationRequestItem);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInt(any(String.class))).thenReturn(10);
		when(request.getMethodType()).thenReturn(HttpMethod.PATCH);
		when(response.getBody()).thenReturn(listCosReservationResponse);
		doNothing().when(reservationResultHandler).updateReservation(any(AbstractOrderModel.class), any(ReservationResponse.class));
		doNothing().when(reservationResultHandler).deleteReservation(any(AbstractOrderModel.class));
		doNothing().when(sessionService).setAttribute(anyString(), anyBoolean());

		//ListAbstractOrderEntryModel
		listAbstractOrderEntryModel.add(abstractOrderEntryModel);

		//ConsumedDestinationModel
		consumedDestinationModel.setUrl("https://cpfs-dtrt.cfapps.eu10.hana.ondemand.com/test");

		//AbstractOrderModel
		abstractOrderModel.setCosReservationId("101");
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		abstractOrderModel.setIsCosOrderAcknowledgedByBackend(Boolean.FALSE);
	}

	@Test
	public void updateReservationTest()
	{
		//Setup
		ReservationResponse reservationResponse;
		when(restOperations.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(response);

		//Execute
		reservationResponse = defaultCosReservationService.updateReservation(abstractOrderModel, reservationStatus);

		//Verify
		Assert.assertNotNull(abstractOrderModel.getCosReservationId());
		Assert.assertNotNull(reservationResponse);

	}

	@Test(expected = ReservationException.class)
	public void updateReservationTest_AuthenticationServiceException()
	{
		//Setup
		ReservationResponse reservationResponse;
		doThrow(AuthenticationServiceException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH),
				any(HttpEntity.class), any(ParameterizedTypeReference.class));

		//Execute
		reservationResponse = defaultCosReservationService.updateReservation(abstractOrderModel, reservationStatus);
	}

	@Test(expected = BackendDownException.class)
	public void updateReservationTest_HttpClientErrorException()
	{
		//Setup
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ReservationResponse reservationResponse;
		doThrow(httpClientErrorException).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class),
				any(ParameterizedTypeReference.class));


		//Execute
		reservationResponse = defaultCosReservationService.updateReservation(abstractOrderModel, reservationStatus);
	}

	@Test(expected = COSDownException.class)
	public void updateReservationTest_ResourceAccessException()
	{
		//Setup
		ReservationResponse reservationResponse;
		doThrow(ResourceAccessException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH),
				any(HttpEntity.class), any(ParameterizedTypeReference.class));


		//Execute
		reservationResponse = defaultCosReservationService.updateReservation(abstractOrderModel, reservationStatus);
	}

	@Test
	public void updateReservationForCartItemTest()
	{
		//Setup
		ReservationResponse reservationResponse;
		when(restOperations.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(response);

		//Execute
		reservationResponse = defaultCosReservationService.updateReservationForCartItem(abstractOrderModel, reservationStatus,
				cartItemId);

		//Verify
		Assert.assertNotNull(reservationResponse);

	}

	@Test(expected = Exception.class)
	public void updateReservationForCartItemTest_AuthenticationServiceException()
	{
		//Setup
		ReservationResponse reservationResponse;
		doThrow(AuthenticationServiceException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH),
				any(HttpEntity.class), any(ParameterizedTypeReference.class));

		//Execute
		reservationResponse = defaultCosReservationService.updateReservationForCartItem(abstractOrderModel, reservationStatus,
				cartItemId);
	}

	@Test(expected = BackendDownException.class)
	public void updateReservationForCartItemTest_HttpClientErrorException()
	{
		//Setup
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ReservationResponse reservationResponse;
		doThrow(httpClientErrorException).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class),
				any(ParameterizedTypeReference.class));

		//Execute
		reservationResponse = defaultCosReservationService.updateReservationForCartItem(abstractOrderModel, reservationStatus,
				cartItemId);
	}

	@Test(expected = COSDownException.class)
	public void updateReservationForCartItemTest_ResourceAccessException()
	{
		//Setup
		ReservationResponse reservationResponse;
		doThrow(ResourceAccessException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.PATCH),
				any(HttpEntity.class), any(ParameterizedTypeReference.class));

		//Execute
		reservationResponse = defaultCosReservationService.updateReservationForCartItem(abstractOrderModel, reservationStatus,
				cartItemId);
	}

	@Test
	public void deleteReservationTest()
	{
		//Setup
		doReturn(response).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(String.class));

		//Execute
		defaultCosReservationService.deleteReservation(abstractOrderModel);
	}

	@Test
	public void deleteReservationItemTest_OneItem()
	{
		//Setup
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		doReturn(response).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(String.class));

		//Execute
		defaultCosReservationService.deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);
	}

	@Test(expected = ReservationException.class)
	public void deleteReservationItemTest_AuthenticationServiceException()
	{
		//Setup
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		doThrow(AuthenticationServiceException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE),
				any(HttpEntity.class), eq(String.class));

		//Execute
		defaultCosReservationService.deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);
	}

	@Test(expected = BackendDownException.class)
	public void deleteReservationItemTest_HttpClientErrorException()
	{
		//Setup
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		doThrow(httpClientErrorException).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(String.class));

		//Execute
		defaultCosReservationService.deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);
	}

	@Test(expected = COSDownException.class)
	public void deleteReservationItemTest_ResourceAccessException()
	{
		//Setup
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		doThrow(ResourceAccessException.class).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE),
				any(HttpEntity.class), eq(String.class));

		//Execute
		defaultCosReservationService.deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);
	}

	@Test
	public void deleteReservationItemTest_MultipleItem()
	{
		//Setup
		final AbstractOrderEntryModel abstractOrderEntryModelNew = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setEntryNumber(10);
		abstractOrderEntryModelNew.setEntryNumber(11);
		listAbstractOrderEntryModel.add(abstractOrderEntryModelNew);
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
		doReturn(response).when(restOperations).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class),
				eq(String.class));
		when(restOperations.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(response);


		//Execute
		defaultCosReservationService.deleteReservationItem(abstractOrderModel, abstractOrderEntryModel);

		//Verify
		Assert.assertEquals(true, abstractOrderModel.getCosReservationExpireFlag());

	}

}

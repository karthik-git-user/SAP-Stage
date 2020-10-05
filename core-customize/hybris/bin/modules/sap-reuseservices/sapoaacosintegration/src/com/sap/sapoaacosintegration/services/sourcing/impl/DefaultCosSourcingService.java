/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;
import com.sap.sapoaacosintegration.services.reservation.ReservationService;
import com.sap.sapoaacosintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacosintegration.services.rest.impl.DefaultAbstractCosRestService;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingRequestMapper;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResponseMapper;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResultHandler;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingService;
import com.sap.sapoaacosintegration.services.sourcing.request.CosSourcingRequest;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponse;


/**
 * Default Cos Sourcing Service
 */
public class DefaultCosSourcingService extends DefaultAbstractCosRestService implements CosSourcingService
{
	private CosSourcingResultHandler cosSourcingResultHandler;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private CosSourcingRequestMapper cosSourcingRequestMapper;
	private CosSourcingResponseMapper cosSourcingResponseMapper;
	private CosConfigurationService configurationService;
	private ReservationService reservationService;
	private CosServiceUtils cosServiceUtils;

	private static final String SLASH = "/";
	private static final String ERROR_WHEN_CALLING_SOURCING_WEB_SERVICE = "Error when calling sourcing web service.";
	private static final Logger LOG = Logger.getLogger(DefaultCosSourcingService.class);


	/**
	 * Calls the COS rest service for fetching sourcing results
	 *
	 * @param abstractOrderModel
	 */
	@Override
	public void callRestServiceAndPersistResult(final AbstractOrderModel model)
	{
		model.setIsCosOrderAcknowledgedByBackend(Boolean.FALSE);
		this.callRestService(model, false, true);
	}

	/**
	 * Calls the callCosRestService method to fulfill the sourcing service
	 *
	 * @param abstractOrderModel
	 * @param execAllStrategies
	 * @param reserve
	 * @return {@link SourcingResponse}
	 */
	@Override
	public SourcingResponse callRestService(final AbstractOrderModel abstractOrderModel, final boolean execAllStrategies,
			final boolean reserve)
	{
		try
		{
			abstractOrderModel.setSapCosSystemUsed(Boolean.TRUE);
			setBackendDown(false);
			return callCosRestService(abstractOrderModel);

		}
		catch (HttpClientErrorException | AuthenticationServiceException | SourcingException | ReservationException
				| RestInitializationException e)
		{
			LOG.error(ERROR_WHEN_CALLING_SOURCING_WEB_SERVICE);
			throw new SourcingException(e);
		}
		catch (final ResourceAccessException | COSDownException e)
		{
			setBackendDown(true);
			LOG.error(SapoaacosintegrationConstants.COS_DOWN_MESSAGE);
			throw new SourcingException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new SourcingException(e);
		}

	}

	/**
	 * Calls the COS rest service for fetching sourcing results
	 *
	 * @param abstractOrderModel
	 * @return {@link SourcingResponse}
	 */
	@Override
	public SourcingResponse callCosRestService(final AbstractOrderModel abstractOrderModel)
	{
		SourcingResponse response = new SourcingResponse();

		if (abstractOrderModel.getIsCosOrderAcknowledgedByBackend() == null
				|| !abstractOrderModel.getIsCosOrderAcknowledgedByBackend())
		{
			response = doSourcingAndPersistResult(abstractOrderModel);
		}
		abstractOrderModel.setCosReservationExpireFlag(true);

		decideAndPerformReservation(abstractOrderModel);

		return response;
	}

	protected void decideAndPerformReservation(final AbstractOrderModel abstractOrderModel)
	{
		if (abstractOrderModel instanceof OrderModel)
		{
			confirmReservation(abstractOrderModel);
		}
		else
		{
			updateTempReservation(abstractOrderModel);
		}
	}

	protected SourcingResponse doSourcingAndPersistResult(final AbstractOrderModel abstractOrderModel)
	{
		SourcingResponse response = new SourcingResponse();
		final CosSourcingRequest cosSourcingRequest = cosSourcingRequestMapper.prepareCosSourcingRequest(abstractOrderModel);
		if (!cosSourcingRequest.getItems().isEmpty())
		{
			response = getSourcingResponse(cosSourcingRequest);
			cosSourcingResultHandler.persistCosSourcingResultInCart(response, abstractOrderModel);
		}
		return response;
	}


	protected void updateTempReservation(final AbstractOrderModel abstractOrderModel)
	{
		reservationService.updateReservation(abstractOrderModel, SapoaacosintegrationConstants.RESERVATION_STATUS_CART);

	}


	protected void confirmReservation(final AbstractOrderModel abstractOrderModel)
	{
		reservationService.updateReservation(abstractOrderModel, SapoaacosintegrationConstants.RESERVATION_STATUS_ORDER);
		LOG.info("Confirm reservation id : " + abstractOrderModel.getCosReservationId());
	}


	protected SourcingResponse getSourcingResponse(final CosSourcingRequest cosSourcingRequest)
	{
		SourcingResponse sourcingResponse = null;

		try
		{
			sourcingResponse = callSourcingRestService(cosSourcingRequest);
		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(ERROR_WHEN_CALLING_SOURCING_WEB_SERVICE + " Error: "
					+ e.getStatusCode());
			throw new SourcingException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}

		return sourcingResponse;
	}


	protected SourcingResponse callSourcingRestService(final CosSourcingRequest cosSourcingRequest)
	{
		final HttpEntity<CosSourcingRequest> entity = createHttpEntityForSourcing(cosSourcingRequest);
		return exchangeRestTemplateAndExtractSourcingResponse(entity);
	}

	protected SourcingResponse exchangeRestTemplateAndExtractSourcingResponse(final HttpEntity entity)
	{
		ResponseEntity<CosSourcingResponse> cosSourcingResponse = null;
		String finalUri = null;

		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);

			finalUri = destinationModel.getUrl() + SLASH + SapoaacosintegrationConstants.SOURCING_RESOURCE_PATH;
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);

			cosSourcingResponse = restOperations.exchange(finalUri, HttpMethod.POST, entity, CosSourcingResponse.class);

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error("Error calling Rest Service " + e.getResponseBodyAsString());
			throw new SourcingException(e);
		}
		catch (final ResourceAccessException e)
		{
			LOG.error("Error calling Rest Service. Backend may be down." + e);
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("ConsumedDestination not found" + e);
			throw new SourcingException(e);
		}

		return constructSourcingResponse(cosSourcingResponse);

	}


	protected SourcingResponse constructSourcingResponse(final ResponseEntity<CosSourcingResponse> cosSorcingResponse)
	{
		SourcingResponse response = new SourcingResponse();

		if (cosSorcingResponse != null && cosSorcingResponse.getBody() != null)
		{
			final CosSourcingResponse cosSourcingResponse = cosSorcingResponse.getBody();

			if (cosSourcingResponse.getSourcings().isEmpty())
			{
				LOG.info("Sourcing response is empty");
				return response;
			}
			else
			{
				LOG.debug("Sourcing response has values.");
				response = cosSourcingResponseMapper.mapCosSourcingResponseToSourcingResponse(cosSourcingResponse);
			}

		}

		return response;
	}

	/**
	 * @param cosSourcingRequest
	 * @return HttpEntity
	 */
	protected HttpEntity createHttpEntityForSourcing(final CosSourcingRequest cosSourcingRequest)
	{
		HttpEntity entity;
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		entity = new HttpEntity<>(cosSourcingRequest, header);
		return entity;
	}

	/**
	 * @return the integrationRestTemplateFactory
	 */
	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	/**
	 * @param integrationRestTemplateFactory
	 *           the integrationRestTemplateFactory to set
	 */
	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	/**
	 * @return the cosSourcingRequestMapper
	 */
	public CosSourcingRequestMapper getCosSourcingRequestMapper()
	{
		return cosSourcingRequestMapper;
	}

	/**
	 * @param cosSourcingRequestMapper
	 *           the cosSourcingRequestMapper to set
	 */
	public void setCosSourcingRequestMapper(final CosSourcingRequestMapper cosSourcingRequestMapper)
	{
		this.cosSourcingRequestMapper = cosSourcingRequestMapper;
	}

	/**
	 * @return the cosSourcingResponseMapper
	 */
	public CosSourcingResponseMapper getCosSourcingResponseMapper()
	{
		return cosSourcingResponseMapper;
	}

	/**
	 * @param cosSourcingResponseMapper
	 *           the cosSourcingResponseMapper to set
	 */
	public void setCosSourcingResponseMapper(final CosSourcingResponseMapper cosSourcingResponseMapper)
	{
		this.cosSourcingResponseMapper = cosSourcingResponseMapper;
	}

	/**
	 * @return the cosSourcingResultHandler
	 */
	public CosSourcingResultHandler getCosSourcingResultHandler()
	{
		return cosSourcingResultHandler;
	}

	/**
	 * @param cosSourcingResultHandler
	 *           the cosSourcingResultHandler to set
	 */
	public void setCosSourcingResultHandler(final CosSourcingResultHandler cosSourcingResultHandler)
	{
		this.cosSourcingResultHandler = cosSourcingResultHandler;
	}

	/**
	 * @return the reservationService
	 */
	public ReservationService getReservationService()
	{
		return reservationService;
	}

	/**
	 * @param reservationService
	 *           the reservationService to set
	 */
	public void setReservationService(final ReservationService reservationService)
	{
		this.reservationService = reservationService;
	}

	/**
	 * @return the configurationService
	 */
	public CosConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final CosConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the cosServiceUtils
	 */
	public CosServiceUtils getCosServiceUtils()
	{
		return cosServiceUtils;
	}

	/**
	 * @param cosServiceUtils
	 *           the cosServiceUtils to set
	 */
	public void setCosServiceUtils(final CosServiceUtils cosServiceUtils)
	{
		this.cosServiceUtils = cosServiceUtils;
	}

	@Override
	public void setRestServiceConfiguration(final RestServiceConfiguration restServiceConfiguration)
	{
		// XXX Auto-generated method stub

	}



}

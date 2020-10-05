/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.reservation.CosReservationRequestMapper;
import com.sap.sapoaacosintegration.services.reservation.CosReservationResultHandler;
import com.sap.sapoaacosintegration.services.reservation.ReservationService;
import com.sap.sapoaacosintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequest;
import com.sap.sapoaacosintegration.services.reservation.response.CosReservationResponse;
import com.sap.sapoaacosintegration.services.rest.impl.DefaultAbstractCosRestService;


/**
 * Default Reservation Service.
 */
public class DefaultCosReservationService extends DefaultAbstractCosRestService implements ReservationService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCosReservationService.class);

	private CosReservationRequestMapper cosRequestMapper;
	private CosReservationResultHandler reservationResultHandler;
	private ServiceUtils serviceUtils;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private ConfigurationService configurationService;
	private CosServiceUtils cosServiceUtils;





	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.reservation.ReservationService#updateReservation(de.hybris.platform.core.
	 * model.order.AbstractOrderModel, java.lang.String)
	 */
	@Override
	public ReservationResponse updateReservation(final AbstractOrderModel abstractOrderModel, final String reservationStatus)
	{
		ResponseEntity<List<CosReservationResponse>> response = null;
		final ReservationResponse reservationResponse = new ReservationResponse();
		final HttpEntity entity;
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final CosReservationRequest request = createCosReservationRequest(abstractOrderModel, reservationStatus,
					SapoaacosintegrationConstants.CART_ITEM_ID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.RESERVATION_RESOURCE_PATH);
			entity = prepareRestCallForUpdateReservation(request);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), request.getMethodType(), entity,
					new ParameterizedTypeReference<List<CosReservationResponse>>()
					{
					});

			return (response.getBody() != null && request.getMethodType() != HttpMethod.PATCH)
					? extractResponseAndUpdateOrder(response.getBody(), abstractOrderModel, reservationResponse)
					: reservationResponse;

		}
		catch (final AuthenticationServiceException | ReservationException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.ERROR_WHEN_CALLING_RESERVATION_WEB_SERVICE);
			throw new ReservationException(e);
		}

		catch (final HttpClientErrorException e)
		{
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST && abstractOrderModel.getCosReservationExpireFlag())
			{
				if (abstractOrderModel.getIsCosOrderAcknowledgedByBackend() == null
						|| !abstractOrderModel.getIsCosOrderAcknowledgedByBackend()
						&& reservationStatus.equals(SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER))
				{
					abstractOrderModel.setCosReservationId("");
					abstractOrderModel.setCosReservationExpireFlag(false);
					//Creating temprory reservation
					this.updateReservation(abstractOrderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_CART);
					//Updating Reservation with expiration time
					return this.updateReservation(abstractOrderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER);
				}
				else
				{
					abstractOrderModel.setCosReservationId("");
					abstractOrderModel.setCosReservationExpireFlag(false);
					return this.updateReservation(abstractOrderModel, reservationStatus);
				}
			}
			else
			{
				checkHttpStatusCode(e);
				throw new ReservationException(e.getResponseBodyAsString());
			}
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ReservationException(e);
		}
	}


	/**
	 * @param responseBody
	 * @param abstractOrderModel
	 * @param reservationResponse
	 *
	 */
	private ReservationResponse extractResponseAndUpdateOrder(final List<CosReservationResponse> responseBody,
			final AbstractOrderModel abstractOrderModel, final ReservationResponse reservationResponse)
	{
		if (!responseBody.isEmpty() && responseBody.get(0).getReservationId() != null)
		{
			LOG.debug("COS Resrvation created or Updated with Id  {}", responseBody.get(0).getReservationId());
			abstractOrderModel.setCosReservationId(responseBody.get(0).getReservationId());
			reservationResultHandler.updateReservation(abstractOrderModel, reservationResponse);

		}

		return reservationResponse;
	}


	@Override
	public ReservationResponse updateReservationForCartItem(final AbstractOrderModel abstractOrderModel,
			final String reservationStatus, final String cartItemId)
	{
		ResponseEntity<List<CosReservationResponse>> response = null;
		final ReservationResponse reservationResponse = new ReservationResponse();
		final HttpEntity entity;
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final CosReservationRequest request = createCosReservationRequest(abstractOrderModel, reservationStatus, cartItemId);

			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.RESERVATION_RESOURCE_PATH);
			entity = prepareRestCallForUpdateReservation(request);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), request.getMethodType(), entity,
					new ParameterizedTypeReference<List<CosReservationResponse>>()
					{
					});

			return (response.getBody() != null && request.getMethodType() != HttpMethod.PATCH)
					? extractResponseAndUpdateOrder(response.getBody(), abstractOrderModel, reservationResponse)
					: reservationResponse;

		}
		catch (AuthenticationServiceException | ReservationException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.ERROR_WHEN_CALLING_RESERVATION_WEB_SERVICE);
			throw new ReservationException(e);
		}
		catch (final HttpClientErrorException e)
		{
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST && abstractOrderModel.getCosReservationExpireFlag())
			{
				abstractOrderModel.setCosReservationId("");
				abstractOrderModel.setCosReservationExpireFlag(false);
				return this.updateReservation(abstractOrderModel, reservationStatus);
			}
			else
			{
				checkHttpStatusCode(e);
				throw new ReservationException(e.getResponseBodyAsString());
			}
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ReservationException(e);
		}


	}





	/**
	 *
	 *
	 */
	private CosReservationRequest createCosReservationRequest(final AbstractOrderModel abstractOrderModel,
			final String reservationStatus, final String cartItemId)
	{
		final CosReservationRequest request = new CosReservationRequest();


		if (abstractOrderModel.getIsCosOrderAcknowledgedByBackend() != null
				&& abstractOrderModel.getIsCosOrderAcknowledgedByBackend()
				&& reservationStatus.equals(SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER))
		{
			request.setReservationId(abstractOrderModel.getCosReservationId());
			request.setIncludedInAvailabilityRawDataOnNextDataUpdate(true);
			request.setMethodType(HttpMethod.PATCH);
		}
		else if ((abstractOrderModel.getIsCosOrderAcknowledgedByBackend() == null
				|| !abstractOrderModel.getIsCosOrderAcknowledgedByBackend())
				&& reservationStatus.equals(SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER))
		{
			request.setReservationId(abstractOrderModel.getCosReservationId());
			request.setMethodType(HttpMethod.PATCH);
			request.setItems(getCosRequestMapper().mapOrderModelToReservationRequest(abstractOrderModel, request, cartItemId));
			request.setExpiresInSeconds(SapoaacosintegrationConstants.RESERVATION_EXPIRE_TIME_FOR_ORDER);
		}
		else
		{
			if (StringUtils.isNotEmpty(abstractOrderModel.getCosReservationId()))
			{
				request.setReservationId(abstractOrderModel.getCosReservationId());
				request.setMethodType(HttpMethod.PATCH);
			}
			else
			{
				request.setMethodType(HttpMethod.POST);
			}
			request.setItems(getCosRequestMapper().mapOrderModelToReservationRequest(abstractOrderModel, request, cartItemId));
			request.setExpiresInSeconds(getIntValue(SapoaacosintegrationConstants.RESERVATION_EXPIRE_TIME_PROPERTY_KEY,
					SapoaacosintegrationConstants.RESERVATION_EXPIRE_TIME_FALLBACK_VALUE));
		}

		return request;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.reservation.ReservationService#deleteReservation(String)
	 */
	@Override
	public void deleteReservation(final AbstractOrderModel abstractOrderModel)
	{
		if (StringUtils.isNotEmpty(abstractOrderModel.getCosReservationId()))
		{
			this.deleteReservationRestCallForCOS(abstractOrderModel.getCosReservationId());
		}
		this.getReservationResultHandler().deleteReservation(abstractOrderModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.reservation.ReservationService#deleteReservationItem(String, String)
	 */
	@Override
	public void deleteReservationItem(final AbstractOrderModel abstractOdrerModel,
			final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		if (StringUtils.isNotEmpty(abstractOdrerModel.getCosReservationId()))
		{
			if (abstractOdrerModel.getEntries().size() == 1)
			{
				this.deleteReservationRestCallForCOS(abstractOdrerModel.getCosReservationId());
				this.getReservationResultHandler().deleteReservation(abstractOdrerModel);
			}
			else
			{
				abstractOdrerModel.setCosReservationExpireFlag(Boolean.TRUE);
				this.updateReservationForCartItem(abstractOdrerModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_CART,
						abstractOrderEntryModel.getEntryNumber().toString());
			}
		}
	}

	/**
	 * Calls delete Reservation REST Service, based on the given reservation service path
	 *
	 * @param reservationServicePath
	 */
	private void deleteReservationRestCallForCOS(final String reservationId)
	{
		final HttpEntity entity;
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.RESERVATION_RESOURCE_PATH);
			entity = prepareRestCallForDeleteReservation(reservationId);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			restOperations.exchange(finalUri.toString(), HttpMethod.DELETE, entity, String.class);

		}
		catch (AuthenticationServiceException | ReservationException | RestInitializationException | ModelNotFoundException e)
		{
			throw new ReservationException(e);
		}
		catch (final HttpClientErrorException e)
		{
			checkHttpStatusCode(e);
			throw new ReservationException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}
	}


	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}


	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	/**
	 * @return the reservationResultHandler
	 */
	public CosReservationResultHandler getReservationResultHandler()
	{
		return reservationResultHandler;
	}


	/**
	 * @param reservationResultHandler
	 *           the reservationResultHandler to set
	 */
	public void setReservationResultHandler(final CosReservationResultHandler reservationResultHandler)
	{
		this.reservationResultHandler = reservationResultHandler;
	}


	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	public CosReservationRequestMapper getCosRequestMapper()
	{
		return cosRequestMapper;
	}


	public void setCosRequestMapper(final CosReservationRequestMapper cosRequestMapper)
	{
		this.cosRequestMapper = cosRequestMapper;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
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

	/**
	 *
	 */
	private HttpEntity prepareRestCallForUpdateReservation(final CosReservationRequest request)
	{
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(request, header);
	}

	/**
	 *
	 */
	private HttpEntity prepareRestCallForDeleteReservation(final String reservationId)
	{
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(reservationId, header);
	}

	private Integer getIntValue(final String propertyKey, final Integer propertyFallbackValue)
	{
		try
		{
			final Integer limit = getConfigurationService().getConfiguration().getInt(propertyKey);
			if (limit <= 0)
			{
				LOG.warn("Property '{}' was not configured correctly. Using fallback value '{}'.", propertyKey,
						propertyFallbackValue);
				return propertyFallbackValue;
			}
			return limit;
		}
		catch (final NoSuchElementException | ConversionException e)
		{
			LOG.warn("Property '{}' was not configured or not configured correctly. Using fallback value '{}'.", propertyKey,
					propertyFallbackValue, e);
			return propertyFallbackValue;
		}
	}



}
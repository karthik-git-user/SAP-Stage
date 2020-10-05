/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacarintegration.services.reservation.ReservationRequestMapper;
import com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler;
import com.sap.sapoaacarintegration.services.reservation.ReservationService;
import com.sap.sapoaacarintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultAbstractRestService;
import com.sap.sapoaacarintegration.services.rest.util.HttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.HttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.URLProvider;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationResult;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Default Reservation Service.
 */
public class DefaultReservationService extends DefaultAbstractRestService implements ReservationService
{
	private static final String ERROR_WHEN_CALLING_RESERVATION_WEB_SERVICE = "Error when calling reservation web service.";
	private static final Logger LOG = Logger.getLogger(DefaultReservationService.class);
	private static final String SERVICE_PATH = "/sap/car/rest/oaa/reservation/";
	private static final String SERVICE_PATH_ITEMS = "/items/";

	private AuthenticationService authenticationService;
	private HttpHeaderProvider httpHeaderProvider;
	private URLProvider urlProvider;
	private HttpEntityBuilder httpEntityBuilder;
	private ReservationRequestMapper requestMapper;
	private ReservationResultHandler reservationResultHandler;
	private ServiceUtils serviceUtils;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacarintegration.services.reservation.ReservationService#updateReservation(de.hybris.platform.core.
	 * model.order.AbstractOrderModel, java.lang.String)
	 */
	@Override
	public ReservationResponse updateReservation(final AbstractOrderModel abstractOrderModel, final String reservationStatus)
	{
		final String reservationServicePath = SERVICE_PATH + abstractOrderModel.getGuid().replace("-", "").toUpperCase();

		try
		{
			initialize();
			beforeRestCall();

			final AuthenticationResult authenticationResult = authenticationService.execute(getRestServiceConfiguration().getUser(),
					getRestServiceConfiguration().getPassword(), getRestServiceConfiguration().getTargetUrl(), reservationServicePath,
					getRestServiceConfiguration().getSapCarClient());

			final HttpHeaders header = compileHTTPHeader(authenticationResult);
			final ReservationAbapRequest abap = requestMapper.mapOrderModelToReservationRequest(abstractOrderModel,
					reservationStatus, getRestServiceConfiguration());
			final HttpEntity<ReservationAbapRequest> entity = httpEntityBuilder.createHttpEntity(header, abap);

			final ResponseEntity<ReservationResponse> response = getRestTemplate()
					.exchange(urlProvider.compileURI(getRestServiceConfiguration().getTargetUrl(), reservationServicePath,
							getRestServiceConfiguration().getSapCarClient()), HttpMethod.PUT, entity, ReservationResponse.class);

			final ReservationResponse reservationResult = response.getBody();
			//Call Result Handler
			getReservationResultHandler().updateReservation(abstractOrderModel, reservationResult);

			return reservationResult;
		}
		catch (AuthenticationServiceException | URISyntaxException | ReservationException | RestInitializationException e)
		{
			LOG.error(ERROR_WHEN_CALLING_RESERVATION_WEB_SERVICE);
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
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacarintegration.services.reservation.ReservationService#deleteReservation(String)
	 */
	@Override
	public void deleteReservation(final AbstractOrderModel abstractOrderModel)
	{
		final String reservationServicePath = SERVICE_PATH + abstractOrderModel.getGuid().replace("-", "").toUpperCase();

		this.deleteReservationRestCall(reservationServicePath);

		this.getReservationResultHandler().deleteReservation(abstractOrderModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacarintegration.services.reservation.ReservationService#deleteReservationItem(String, String)
	 */
	@Override
	public void deleteReservationItem(final AbstractOrderModel abstractOdrerModel,
			final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		final String reservationServicePath = SERVICE_PATH + abstractOdrerModel.getGuid().replace("-", "").toUpperCase()
				+ SERVICE_PATH_ITEMS + serviceUtils.createExternalIdForItem(abstractOrderEntryModel.getEntryNumber().toString(),
						abstractOdrerModel.getGuid());

		this.deleteReservationRestCall(reservationServicePath);

		this.getReservationResultHandler().deleteReservationItem(abstractOrderEntryModel);
	}

	/**
	 * Calls delete Reservation REST Service, based on the given reservation service path
	 *
	 * @param reservationServicePath
	 */
	private void deleteReservationRestCall(final String reservationServicePath)
	{
		try
		{
			initialize();
			beforeRestCall();

			final AuthenticationResult authenticationResult = authenticationService.execute(getRestServiceConfiguration().getUser(),
					getRestServiceConfiguration().getPassword(), getRestServiceConfiguration().getTargetUrl(), reservationServicePath,
					getRestServiceConfiguration().getSapCarClient());

			final HttpHeaders header = compileHTTPHeader(authenticationResult);
			final HttpEntity entity = httpEntityBuilder.createHttpEntity(header);

			getRestTemplate().exchange(urlProvider.compileURI(getRestServiceConfiguration().getTargetUrl(), reservationServicePath,
					getRestServiceConfiguration().getSapCarClient()), HttpMethod.DELETE, entity, Object.class);
		}
		catch (AuthenticationServiceException | URISyntaxException | ReservationException | RestInitializationException e)
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
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}
	}

	/**
	 * @param authenticationService
	 *           the authenticationService to set
	 */
	public void setAuthenticationService(final AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
	}

	/**
	 * @return the authenticationService
	 */
	protected AuthenticationService getAuthenticationService()
	{
		return authenticationService;
	}

	/**
	 * @param urlProvider
	 *           the urlProvider to set
	 */
	public void setUrlProvider(final URLProvider urlProvider)
	{
		this.urlProvider = urlProvider;
	}

	/**
	 * @return the urlProvider
	 */
	protected URLProvider getUrlProvider()
	{
		return urlProvider;
	}

	/**
	 * @param requestMapper
	 *           the requestMapper to set
	 */
	public void setRequestMapper(final ReservationRequestMapper requestMapper)
	{
		this.requestMapper = requestMapper;
	}

	/**
	 * @return the requestMapper
	 */
	protected ReservationRequestMapper getRequestMapper()
	{
		return requestMapper;
	}

	/**
	 * @param httpHeaderProvider
	 *           the httpHeaderProvider to set
	 */
	public void setHttpHeaderProvider(final HttpHeaderProvider httpHeaderProvider)
	{
		this.httpHeaderProvider = httpHeaderProvider;
	}

	/**
	 * @return the httpHeaderProvider
	 */
	protected HttpHeaderProvider getHttpHeaderProvider()
	{
		return httpHeaderProvider;
	}

	/**
	 * @param httpEntityBuilder
	 *           the httpEntityBuilder to set
	 */
	public void setHttpEntityBuilder(final HttpEntityBuilder httpEntityBuilder)
	{
		this.httpEntityBuilder = httpEntityBuilder;
	}

	/**
	 * @return the httpEntityBuilder
	 */
	protected HttpEntityBuilder getHttpEntityBuilder()
	{
		return httpEntityBuilder;
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

	/**
	 * @return the reservationResultHandler
	 */
	public ReservationResultHandler getReservationResultHandler()
	{
		return reservationResultHandler;
	}

	/**
	 * @param reservationResultHandler
	 *           the reservationResultHandler to set
	 */
	public void setReservationResultHandler(final ReservationResultHandler reservationResultHandler)
	{
		this.reservationResultHandler = reservationResultHandler;
	}

	/**
	 * @param authenticationResult
	 * @return HttpHeaders
	 */
	private HttpHeaders compileHTTPHeader(final AuthenticationResult authenticationResult)
	{
		final HttpHeaders header = httpHeaderProvider.compileHttpHeader(getRestServiceConfiguration().getUser(),
				getRestServiceConfiguration().getPassword());
		httpHeaderProvider.appendCsrfToHeader(header, authenticationResult.getResponseHeader());
		httpHeaderProvider.appendCookieToHeader(header, authenticationResult.getResponseHeader());
		final List<MediaType> acceptList = new ArrayList<>();
		acceptList.add(MediaType.APPLICATION_XML);
		header.setAccept(acceptList);
		header.setContentType(MediaType.APPLICATION_XML);
		return header;
	}
}
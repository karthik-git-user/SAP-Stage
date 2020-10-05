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
package de.hybris.platform.outboundservices.monitoring;

import static de.hybris.platform.outboundservices.constants.OutboundservicesConstants.OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME;

import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.integrationservices.service.MediaPersistenceService;
import de.hybris.platform.integrationservices.util.HttpStatus;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;
import de.hybris.platform.outboundservices.model.OutboundRequestMediaModel;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class DefaultOutboundRequestResponseInterceptor implements ClientHttpRequestInterceptor
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundRequestResponseInterceptor.class);

	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;
	private MediaPersistenceService mediaPersistenceService;
	private OutboundServicesConfiguration outboundServicesConfiguration;

	private List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> errorParsers;
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> fallbackErrorParser;
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser;

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] payload,
			final ClientHttpRequestExecution requestExecution) throws IOException
	{
		return outboundServicesConfiguration.isMonitoringEnabled() ?
				executeRequestWithMonitoring(request, payload, requestExecution) :
				executeRequest(request, payload, requestExecution);
	}

	private ClientHttpResponse executeRequest(final HttpRequest request, final byte[] payload, final ClientHttpRequestExecution requestExecution) throws IOException
	{
		return requestExecution.execute(request, payload);
	}

	private ClientHttpResponse executeRequestWithMonitoring(final HttpRequest request, final byte[] payload, final ClientHttpRequestExecution requestExecution) throws IOException
	{
		final String messageId = extractMessageId(request);
		final OutboundRequestModel outboundRequestModel = findOutboundRequest(messageId);
		try
		{
			final ClientHttpResponse response = executeRequest(request, payload, requestExecution);
			return updateOutboundRequestWithResponse(outboundRequestModel, payload, messageId, response);
		}
		catch (final IOException e)
		{
			updateOutboundRequestWithException(outboundRequestModel, payload, messageId, e);
			throw e;
		}
	}

	protected void updateOutboundRequestWithException(final OutboundRequestModel outboundRequestModel,
			final byte[] payload, final String messageId, final Throwable t)
	{
		if (outboundRequestModel != null)
		{
			outboundRequestModel.setStatus(IntegrationRequestStatus.ERROR);
			outboundRequestModel.setError(extractError(t));
			if (getOutboundServicesConfiguration().isPayloadRetentionForErrorEnabled())
			{
				outboundRequestModel.setPayload(getPayload(payload, messageId));
			}
			getModelService().save(outboundRequestModel);
		}
	}

	protected ClientHttpResponse updateOutboundRequestWithResponse(final OutboundRequestModel outboundRequestModel,
			final byte[] payload,
			final String messageId,
			final ClientHttpResponse clientHttpResponse) throws IOException
	{
		ClientHttpResponse response = clientHttpResponse;
		if (outboundRequestModel != null)
		{
			response = HttpStatus.valueOf(clientHttpResponse.getRawStatusCode()).isSuccessful() ?
					handleSuccess(outboundRequestModel, payload, messageId, clientHttpResponse) :
					handleError(outboundRequestModel, payload, messageId, clientHttpResponse);
			getModelService().save(outboundRequestModel);
		}
		return response;
	}

	protected String extractMessageId(final HttpRequest request)
	{
		final List<String> messageIdHeaders = ListUtils.emptyIfNull(request.getHeaders().get(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME));
		return messageIdHeaders.isEmpty() || StringUtils.isEmpty(messageIdHeaders.get(0)) ? null : messageIdHeaders.get(0);
	}

	protected OutboundRequestModel findOutboundRequest(final String messageId)
	{
		if (messageId != null)
		{
			final OutboundRequestModel outboundRequestModel = getModelService().create(OutboundRequestModel.class);
			outboundRequestModel.setMessageId(messageId);

			try
			{
				return getFlexibleSearchService().getModelByExample(outboundRequestModel);
			}
			catch (final ModelNotFoundException e)
			{
				LOG.warn("No OutboundRequestModel item found for {}: {}. The monitoring response cannot be updated without this item.",
						OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, messageId);
			}
			catch (final AmbiguousIdentifierException e)
			{
				LOG.warn("Multiple OutboundRequestModel items found for {}: {}. The monitoring response cannot be updated without an unique item.",
						OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, messageId);
			}
		}
		return null;
	}

	protected ClientHttpResponse handleError(final OutboundRequestModel outboundRequestModel, final byte[] payload, final String messageId, final ClientHttpResponse clientHttpResponse) throws IOException
	{
		final ClientHttpResponse response = new WrappedClientHttpResponse(clientHttpResponse);
		outboundRequestModel.setStatus(IntegrationRequestStatus.ERROR);
		outboundRequestModel.setError(extractError(response));
		if (getOutboundServicesConfiguration().isPayloadRetentionForErrorEnabled())
		{
			outboundRequestModel.setPayload(getPayload(payload, messageId));
		}
		return response;
	}

	protected ClientHttpResponse handleSuccess(final OutboundRequestModel outboundRequestModel, final byte[] payload, final String messageId, final ClientHttpResponse clientHttpResponse)
	{
		outboundRequestModel.setStatus(IntegrationRequestStatus.SUCCESS);
		if (getOutboundServicesConfiguration().isPayloadRetentionForSuccessEnabled())
		{
			outboundRequestModel.setPayload(getPayload(payload, messageId));
		}
		return clientHttpResponse;
	}

	protected String extractError(final ClientHttpResponse response) throws IOException
	{
		final String contentType = response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
		final int statusCode = response.getRawStatusCode();
		final String responseBody = IOUtils.toString(response.getBody(), "UTF-8");

		return getErrorParsers().stream()
				.filter(extractor -> extractor.isApplicable(contentType, statusCode))
				.map(extractor -> extractor.parseErrorFrom(MonitoredRequestErrorModel.class, statusCode, responseBody))
				.findFirst()
				.orElse(getFallbackErrorParser().parseErrorFrom(MonitoredRequestErrorModel.class, statusCode, responseBody))
				.getMessage();
	}

	protected String extractError(final Throwable t)
	{
		return getExceptionErrorParser().parseErrorFrom(MonitoredRequestErrorModel.class, -1, t.getMessage()).getMessage();
	}

	protected OutboundRequestMediaModel getPayload(final byte[] payload, final String messageId)
	{
		final List<OutboundRequestMediaModel> list =
				getMediaPersistenceService().persistMedias(Collections.singletonList(new ByteArrayInputStream(payload)), OutboundRequestMediaModel.class);
		if (list == null || list.isEmpty())
		{
			LOG.warn("No payload was returned for {}: {}. The monitoring response cannot be updated without this item.",
					OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, messageId);
			return null;
		}

		return list.get(0);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected MediaPersistenceService getMediaPersistenceService()
	{
		return mediaPersistenceService;
	}

	@Required
	public void setMediaPersistenceService(final MediaPersistenceService mediaPersistenceService)
	{
		this.mediaPersistenceService = mediaPersistenceService;
	}

	protected List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> getErrorParsers()
	{
		return errorParsers;
	}

	@Required
	public void setErrorParsers(final List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> errorParsers)
	{
		this.errorParsers = errorParsers;
	}

	protected MonitoredRequestErrorParser<MonitoredRequestErrorModel> getFallbackErrorParser()
	{
		return fallbackErrorParser;
	}

	@Required
	public void setFallbackErrorParser(final MonitoredRequestErrorParser<MonitoredRequestErrorModel> fallbackErrorParser)
	{
		this.fallbackErrorParser = fallbackErrorParser;
	}

	protected MonitoredRequestErrorParser<MonitoredRequestErrorModel> getExceptionErrorParser()
	{
		return exceptionErrorParser;
	}

	@Required
	public void setExceptionErrorParser(final MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser)
	{
		this.exceptionErrorParser = exceptionErrorParser;
	}

	protected OutboundServicesConfiguration getOutboundServicesConfiguration()
	{
		return outboundServicesConfiguration;
	}

	@Required
	public void setOutboundServicesConfiguration(final OutboundServicesConfiguration outboundServicesConfiguration)
	{
		this.outboundServicesConfiguration = outboundServicesConfiguration;
	}

	public static class WrappedClientHttpResponse implements ClientHttpResponse
	{
		private final ClientHttpResponse clientHttpResponse;
		private ByteArrayInputStream inputStream;

		public WrappedClientHttpResponse(final ClientHttpResponse clientHttpResponse)
		{
			this.clientHttpResponse = clientHttpResponse;
		}

		@Override
		public org.springframework.http.HttpStatus getStatusCode() throws IOException
		{
			return clientHttpResponse.getStatusCode();
		}

		@Override
		public int getRawStatusCode() throws IOException
		{
			return clientHttpResponse.getRawStatusCode();
		}

		@Override
		public String getStatusText() throws IOException
		{
			return clientHttpResponse.getStatusText();
		}

		@Override
		public void close()
		{
			clientHttpResponse.close();
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
			}
			catch (final IOException e)
			{
				LOG.trace(e.getMessage(), e);
			}
		}

		@Override
		public InputStream getBody() throws IOException
		{
			if (inputStream == null)
			{
				inputStream = new ByteArrayInputStream(IOUtils.toByteArray(clientHttpResponse.getBody()));
			}
			return inputStream;
		}

		@Override
		public HttpHeaders getHeaders()
		{
			return clientHttpResponse.getHeaders();
		}
	}
}

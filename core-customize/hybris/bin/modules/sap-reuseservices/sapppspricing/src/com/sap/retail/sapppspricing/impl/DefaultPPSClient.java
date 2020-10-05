/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.sap.ppengine.client.dto.PriceCalculate;
import com.sap.ppengine.client.dto.PriceCalculateResponse;
import com.sap.retail.sapppspricing.PPSClient;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * PPS client supporting remote call via REST.
 * For the remote call OAuth authentication is supported.
 * via configuration (see {@link PPSConfigService})
 */
public class DefaultPPSClient extends DefaultPPSClientBeanAccessor implements PPSClient {
	// ID to relate this request to entry in server log
	protected static final String X_REQUEST_ID = "x-request-id";
	protected static final String AUTHORIZATION = "Authorization";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultPPSClient.class);
	private BaseStoreService baseStoreService;
	private FlexibleSearchService flexibleSearchService;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	protected static final String DESTINATIONID = "scpOPPServiceDestination";
	private static final double TIMECONVERSION_TO_MS = 1000000;

	protected ConsumedDestinationModel getConsumedDestinationModelById(final String destinationId) {
		ConsumedDestinationModel destination = null;
		try {
			final ConsumedDestinationModel example = new ConsumedDestinationModel();
			example.setId(destinationId);
			destination = getFlexibleSearchService().getModelByExample(example);
		} catch (final RuntimeException e) {
			LOG.warn("Failed to find ConsumedDestination with id '{}'", destinationId, e);
		}
		return destination;
	}

	public PriceCalculateResponse callPPS(final PriceCalculate priceCalculate,
			final SAPConfigurationModel sapConfig) {
		LOG.debug("entering callPPSRemote()");
		final long t1 = System.nanoTime();
		// Using OAuth2RestTemplate for OAuth Authentication
		final ConsumedDestinationModel destinationModel = getConsumedDestinationModelById(DESTINATIONID);
		if (destinationModel == null) {
			throw new ModelNotFoundException("Provided destination was not found.");
		}
		final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_XML);
		httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
		
		//Log Request Payload
		logRequestPayload(priceCalculate);
		
		try {
			ResponseEntity<PriceCalculateResponse> result = restOperations.exchange(destinationModel.getUrl(),
					HttpMethod.POST, new HttpEntity<Object>(priceCalculate, httpHeaders), PriceCalculateResponse.class);

			final PriceCalculateResponse responseBody = result.getBody();
			
			logResponsePayload(responseBody);
			
			if (!result.getStatusCode().is2xxSuccessful()) {
				throw new SapPPSPricingRuntimeException("Unexpected return code " + result.getStatusCode().value());
			} else {
				final long t2 = System.nanoTime();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Request duration in ms {}" , (t2 - t1) / TIMECONVERSION_TO_MS);
				}
				return responseBody;
			}
		}
		finally {
			LOG.debug("exiting");
		}
	}
	
	
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
		return integrationRestTemplateFactory;
	}

	public void setIntegrationRestTemplateFactory(IntegrationRestTemplateFactory integrationRestTemplateFactory) {
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}
	
	private void logRequestPayload(PriceCalculate priceCalculate) {
		
		LOG.debug("Logging request");
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(PriceCalculate.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(priceCalculate, sw);
			String xmlContent = sw.toString();
			LOG.debug(xmlContent);
		} catch (JAXBException e) {
			LOG.error("Error in parsing request payload {}", e.getMessage());
		}
		
	}
	
	
	private void logResponsePayload(PriceCalculateResponse priceCalculateResponse) {
		
		LOG.debug("Logging Response");
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(PriceCalculate.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(priceCalculateResponse, sw);
			String xmlContent = sw.toString();
			LOG.debug(xmlContent);
		} catch (JAXBException e) {
			LOG.error("Error in parsing request payload {}", e.getMessage());
		}
	}

}

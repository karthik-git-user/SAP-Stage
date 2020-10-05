/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.clients.impl;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.sap.hybris.sapcpqquoteintegration.clients.SapCpqQuoteApiClientService;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;

public class DefaultSapCpqQuoteApiClientService implements SapCpqQuoteApiClientService {

	private static final Logger logger = Logger.getLogger(DefaultSapCpqQuoteApiClientService.class.getName());

	private static final String DOWNLOAD_PROPOSAL_DESTINATION_ID = "downloadProposalDocumentId";

	private QuoteService quoteService;

	private RestTemplate restTemplate;

	private DestinationService<ConsumedDestinationModel> destinationService;

	private IntegrationRestTemplateFactory integrationRestTemplateFactory;

	@Override
	public byte[] fetchProposalDocument(String quoteCode) {
		byte[] dataFromMedia = null;
		
		try
		{
			final QuoteModel currentQuote = getQuoteService().getCurrentQuoteForCode(quoteCode);


			final String documentLink = currentQuote.getCpqQuoteProposalDocument();

			if (StringUtils.isNotEmpty(documentLink)) {
				
				final ConsumedDestinationModel destinationModel = destinationService
						.getDestinationById(DOWNLOAD_PROPOSAL_DESTINATION_ID);
				dataFromMedia = fetchQuoteProposalDocument(documentLink, destinationModel);

			}

		}
		catch (final RestClientException exp)
		{
			logger.error("Unable to Retrieve the PDF from backend");

		}
		return dataFromMedia;
	}

	private byte[] fetchQuoteProposalDocument(String documentLink, ConsumedDestinationModel destinationModel) {


		if (destinationModel != null)
		{
			destinationModel.setUrl(documentLink);
			final RestOperations restOperations = buildRestOperations(destinationModel);

			final HttpHeaders headers1 = new HttpHeaders();
			headers1.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
			final HttpEntity<String> entity = new HttpEntity<>(headers1);

			final RestTemplate template = (RestTemplate) restOperations;

			restTemplate.setInterceptors(template.getInterceptors());

			final ResponseEntity<byte[]> responseEntity = restTemplate.exchange(documentLink, HttpMethod.GET, entity,
					byte[].class);

			if (responseEntity.getStatusCode() == HttpStatus.OK)
			{
				return responseEntity.getBody();

			}
		}

		return new byte[0];

	}


	private RestOperations buildRestOperations(final ConsumedDestinationModel destinationModel)
	{

		return integrationRestTemplateFactory.create(destinationModel);
	}

	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public DestinationService<ConsumedDestinationModel> getDestinationService() {
		return destinationService;
	}

	public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
		this.destinationService = destinationService;
	}

	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
		return integrationRestTemplateFactory;
	}

	public void setIntegrationRestTemplateFactory(IntegrationRestTemplateFactory integrationRestTemplateFactory) {
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

}

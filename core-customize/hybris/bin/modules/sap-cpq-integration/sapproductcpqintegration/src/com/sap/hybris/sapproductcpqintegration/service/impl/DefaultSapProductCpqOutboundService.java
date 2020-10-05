/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapproductcpqintegration.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.sap.hybris.sapproductcpqintegration.constants.SapproductcpqintegrationConstants;
import com.sap.hybris.sapproductcpqintegration.exception.DefaultSapProductCpqIntegrationException;
import com.sap.hybris.sapproductcpqintegration.service.SapProductCpqOutboundService;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.media.MediaService;

/**
 * Implementation for sending product media object to callidus cloud.
 */
public class DefaultSapProductCpqOutboundService implements SapProductCpqOutboundService {
	
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private DestinationService<ConsumedDestinationModel> destinationService;
	private MediaService mediaService;
	private String realFileName;

	/**
	 * preparing and sending restTemplate post request for replicating product image to callidus.
	 * 
	 * @param mediaModel
	 * @return ResponseEntity<String>
	 */
	@Override
	public ResponseEntity<String> sendProductMedia(MediaModel mediaModel) {

		try {
		final ConsumedDestinationModel destinationModel = getDestinationService()
				.getDestinationByIdAndByDestinationTargetId(SapproductcpqintegrationConstants.OUTBOUND_PRODUCTIMAGE_DESTINATION,SapproductcpqintegrationConstants.OUTBOUND_PRODUCTIMAGE_TARGET);
		if (destinationModel == null) {
			throw new DefaultSapProductCpqIntegrationException("Provided destination was not found.");
		}
		final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
		final RestTemplate restTemplate = (RestTemplate) restOperations;
		restTemplate.setMessageConverters(new RestTemplate().getMessageConverters());
		return 	restTemplate.postForEntity(destinationModel.getUrl(), createHttpEntity(mediaModel), String.class);
		}
		catch (Exception e) {
			throw new DefaultSapProductCpqIntegrationException(e);
		}
	}
	
	/**
	 * Creating Entity from product media model.
	 * 
	 * @param mediaModel
	 * @return HttpEntity<MultiValueMap<String, Object>>
	 * @throws DefaultSapProductCpqIntegrationException
	 */
	private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(MediaModel mediaModel){
		try {
		if(StringUtils.isNotEmpty(mediaModel.getRealFileName())) {
		setRealFileName(mediaModel.getRealFileName());
		}
		else {
		final Path p = Paths.get(mediaModel.getCode());
		setRealFileName(p.getFileName().toString());
		}
		final HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		final ByteArrayResource contentAsResource = new ByteArrayResource(mediaService.getDataFromMedia(mediaModel)) {
			@Override
			public String getFilename() {
				return getRealFileName();
			}
		};

		final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add(SapproductcpqintegrationConstants.FORMDATA_INPUT, contentAsResource);
		return new HttpEntity<>(formData, header);
		}
		catch (Exception e) {
			throw new DefaultSapProductCpqIntegrationException(e);
		}
		
	}

	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory() {
		return integrationRestTemplateFactory;
	}

	public void setIntegrationRestTemplateFactory(IntegrationRestTemplateFactory integrationRestTemplateFactory) {
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	public DestinationService<ConsumedDestinationModel> getDestinationService() {
		return destinationService;
	}

	public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
		this.destinationService = destinationService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

}

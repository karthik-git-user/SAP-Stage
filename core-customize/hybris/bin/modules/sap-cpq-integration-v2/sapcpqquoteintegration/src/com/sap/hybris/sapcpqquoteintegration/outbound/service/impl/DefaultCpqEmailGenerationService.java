/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcpqquoteintegration.clients.SapCpqQuoteApiClientService;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 *  Implementaion of DefaultEmailGenerationService
 */
public class DefaultCpqEmailGenerationService extends DefaultEmailGenerationService {
	
	private static final Logger LOG = Logger.getLogger(DefaultCpqEmailGenerationService.class);
	
	private ModelService modelService;
	
	private SapCpqQuoteApiClientService sapCpqQuoteApiClientService;
	
	private MediaService mediaService;
	
	private CatalogService catalogService;
	
	private KeyGenerator guidKeyGenerator;
	
	/**
	 * Generate method which calls the parent and also add attachments to emailMessage
	 * 
	 *  @param businessProcessModel
	 *  @param emailPageModel
	 *  @return EmailMessageModel
	 */
	@Override
	public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
		LOG.info("Entering DefaultAttachmentEmailGenerationService#generate");
	    
		EmailMessageModel emailMessage = super.generate(businessProcessModel, emailPageModel);
        
		List<EmailAttachmentModel> attachments = businessProcessModel.getCpqEmailAttachments();
		List<EmailAttachmentModel> proposalDocuments = new ArrayList<>();
		if(businessProcessModel instanceof QuoteProcessModel) {
			QuoteProcessModel quoteProcessModel = (QuoteProcessModel) businessProcessModel;
			byte[] data = sapCpqQuoteApiClientService.fetchProposalDocument(quoteProcessModel.getQuoteCode());
			
			if(null != data && data.length > 0) {
				
				proposalDocuments.add(getAttachment(data));	
			}
			proposalDocuments.addAll(attachments);

		}
        emailMessage.setAttachments(proposalDocuments);
        getModelService().saveAll(emailMessage);
        
        LOG.info("Exiting DefaultAttachmentEmailGenerationService#generate");
        return emailMessage;
	}
	
	private EmailAttachmentModel getAttachment(byte[] dataFromMedia) {
		MediaModel uploadFileMedia = null;
		
		uploadFileMedia = modelService.create(MediaModel.class);
		String randomFileName = UUID.randomUUID().toString() + ".pdf";
		uploadFileMedia.setCode(randomFileName);
		final CatalogModel cm = catalogService.getCatalogForId("powertoolsProductCatalog");
		Set<CatalogVersionModel> catalogModelSet = cm.getCatalogVersions();

		if (catalogModelSet != null) {
			Iterator<CatalogVersionModel> iterator = catalogModelSet.iterator();
			final CatalogVersionModel catalogVersionModel = (CatalogVersionModel) iterator.next();
			uploadFileMedia.setCatalogVersion(catalogVersionModel);
		}

		uploadFileMedia.setDescription("File Description");
		modelService.save(uploadFileMedia);


		final ByteArrayInputStream inputStream = new ByteArrayInputStream(dataFromMedia);
		mediaService.setStreamForMedia(uploadFileMedia, inputStream, randomFileName, "application/pdf");
		
		final EmailAttachmentModel emailAttachmentModel = getEmailService()
				.createEmailAttachment(new DataInputStream(getMediaService().getStreamFromMedia(uploadFileMedia)),
						"Quote Proposal Document" + "-" + getGuidKeyGenerator().generate().toString(), uploadFileMedia.getMime());
		
		return emailAttachmentModel;
	}
	
	public ModelService getModelService() {
		return modelService;
	}
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public SapCpqQuoteApiClientService getSapCpqQuoteApiClientService() {
		return sapCpqQuoteApiClientService;
	}
	public void setSapCpqQuoteApiClientService(SapCpqQuoteApiClientService sapCpqQuoteApiClientService) {
		this.sapCpqQuoteApiClientService = sapCpqQuoteApiClientService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public KeyGenerator getGuidKeyGenerator() {
		return guidKeyGenerator;
	}

	public void setGuidKeyGenerator(KeyGenerator guidKeyGenerator) {
		this.guidKeyGenerator = guidKeyGenerator;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Default class to add attachment to sales quote email
 *
 */
public class DefaultAttachmentEmailGenerationService extends DefaultEmailGenerationService {
	
	private static final Logger LOG = Logger.getLogger(DefaultAttachmentEmailGenerationService.class);
	
	private ModelService modelService;
	
	public ModelService getModelService() {
		return modelService;
	}
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	/**
	 * @param businessProcessModel parameter
	 * @param emailPageModel parameter
	 * @return EmailMessageModel
	 */
	@Override
	public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
		LOG.info("Entering DefaultAttachmentEmailGenerationService#generate");
	    
		EmailMessageModel emailMessage = super.generate(businessProcessModel, emailPageModel);
        
		List<EmailAttachmentModel> attachments = businessProcessModel.getAttachments();
        emailMessage.setAttachments(attachments);
        getModelService().saveAll(emailMessage);
        
        LOG.info("Exiting DefaultAttachmentEmailGenerationService#generate");
        return emailMessage;
	}
	
}

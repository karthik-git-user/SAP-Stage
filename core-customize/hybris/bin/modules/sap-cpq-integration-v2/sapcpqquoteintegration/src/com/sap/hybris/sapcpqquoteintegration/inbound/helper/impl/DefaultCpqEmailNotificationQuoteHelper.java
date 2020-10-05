/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound.helper.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqQuoteHelper;

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultCpqEmailNotificationQuoteHelper implements CpqQuoteHelper {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultCpqEmailNotificationQuoteHelper.class);
	
	private BusinessProcessService businessProcessService;
	private ModelService modelService;
	private EmailService emailService;
	private MediaService mediaService;
	private SAPGlobalConfigurationService sapGlobalConfigurationService;

	@Override
	public QuoteModel performQuoteOperation(QuoteModel quoteModel) {
		
		boolean sapGlobalConfigurationExists = getSapGlobalConfigurationService().sapGlobalConfigurationExists();
		
		if (sapGlobalConfigurationExists) {
			
			Object enabled = getSapGlobalConfigurationService()
					.getProperty(SapcpqquoteintegrationConstants.CPQ_EMAIL_ENABLED);
			
			if (enabled != null && (java.lang.Boolean) enabled) {
				
				final QuoteProcessModel quoteBuyerProcessModel = (QuoteProcessModel) getBusinessProcessService()
						.createProcess(
								"sapQuoteEmailProcess" + "-" + quoteModel.getCode() + "-"
										+ quoteModel.getStore().getUid() + "-" + System.currentTimeMillis(),
								"sap-cpq-cpi-quote-email-notification-process");
				if (LOG.isDebugEnabled()) {
					LOG.debug(
							String.format("Created business process for QuoteBuyerSubmitEvent. Process code : [%s] ...",
									quoteBuyerProcessModel.getCode()));
				}
				quoteBuyerProcessModel.setQuoteCode(quoteModel.getCode());
				getModelService().save(quoteBuyerProcessModel);
				// start the business process
				getBusinessProcessService().startProcess(quoteBuyerProcessModel);
			}
		}
		return quoteModel;
	}


	public BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}


	public void setBusinessProcessService(BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}


	public ModelService getModelService() {
		return modelService;
	}


	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}


	public EmailService getEmailService() {
		return emailService;
	}


	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}


	public MediaService getMediaService() {
		return mediaService;
	}


	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}


	public SAPGlobalConfigurationService getSapGlobalConfigurationService() {
		return sapGlobalConfigurationService;
	}


	public void setSapGlobalConfigurationService(SAPGlobalConfigurationService sapGlobalConfigurationService) {
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}
	
	
	

}

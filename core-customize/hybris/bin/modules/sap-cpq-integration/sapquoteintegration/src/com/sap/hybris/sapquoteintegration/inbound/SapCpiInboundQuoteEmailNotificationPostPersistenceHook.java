/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.inbound;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public class SapCpiInboundQuoteEmailNotificationPostPersistenceHook implements PostPersistHook {
	private static final Logger LOG = LoggerFactory
			.getLogger(SapCpiInboundQuoteEmailNotificationPostPersistenceHook.class);
	private BusinessProcessService businessProcessService;
	private ModelService modelService;
	private EmailService emailService;
	private MediaService mediaService;

	@Override
	public void execute(ItemModel item) {
		LOG.info("Entering SapCpiInboundQuoteEmailNotificationPostPersistenceHook#execute");
		if (item instanceof QuoteModel) {
			QuoteModel quoteModel = (QuoteModel) item;
			final QuoteProcessModel quoteBuyerProcessModel = (QuoteProcessModel) getBusinessProcessService()
					.createProcess(
							"sapQuoteEmailProcess" + "-" + quoteModel.getCode() + "-"
									+ quoteModel.getStore().getUid() + "-" + System.currentTimeMillis(),
							"sap-cpi-quote-email-notification-process");

			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Created business process for QuoteBuyerSubmitEvent. Process code : [%s] ...",
						quoteBuyerProcessModel.getCode()));
			}

			quoteBuyerProcessModel.setQuoteCode(quoteModel.getCode());
			
			MediaModel proposalDocument = quoteModel.getProposalDocument();
			
			if (proposalDocument != null) {
				LOG.info("Attaching proposal document in QuoteProcessModel");
				InputStream inputStream = mediaService.getStreamFromMedia(proposalDocument);
				DataInputStream dataInputStream = new DataInputStream(inputStream);
				String randomFileName = UUID.randomUUID().toString() + ".pdf";
				EmailAttachmentModel attachment = emailService.createEmailAttachment(dataInputStream, randomFileName, "application/pdf");
				List<EmailAttachmentModel> attachments = new ArrayList<EmailAttachmentModel>();
				attachments.add(attachment);
				quoteBuyerProcessModel.setAttachments(attachments);
			}
			
			getModelService().save(quoteBuyerProcessModel);
			// start the business process
			getBusinessProcessService().startProcess(quoteBuyerProcessModel);
		}
		LOG.info("Exiting SapCpiInboundQuoteEmailNotificationPostPersistenceHook#execute");

	}

	protected BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
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

	protected EmailService getEmailService()
	{
		return emailService;
	}

	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}


	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}
	


}
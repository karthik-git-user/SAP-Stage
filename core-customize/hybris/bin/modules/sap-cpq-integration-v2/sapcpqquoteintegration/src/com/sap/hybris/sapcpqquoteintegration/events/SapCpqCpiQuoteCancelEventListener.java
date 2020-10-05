/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.events;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;


/**
 *
 */
public class SapCpqCpiQuoteCancelEventListener extends AbstractEventListener<SapCpqCpiQuoteCancelEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(SapCpqCpiQuoteCancelEventListener.class);

	@Override
	protected void onEvent(final SapCpqCpiQuoteCancelEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received SapCpiQuoteCancelEvent..");
		}

		final Map<String, Object> contextParams = new HashMap<String, Object>();
		contextParams.put("QUOTE_USER_TYPE", event.getQuoteUserType());

		final QuoteProcessModel quotePostCancellationProcessModel = (QuoteProcessModel) getBusinessProcessService().createProcess(
				"quotePostCancellationProcess" + "-" + event.getQuote().getCode() + "-" + event.getQuote().getStore().getUid() + "-"
						+ System.currentTimeMillis(),
						SapcpqquoteintegrationConstants.SAP_CPQ_QUOTE_COMPLETED_PROCESS, contextParams);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Created business process for SapCpiQuoteCancelEvent. Process code : [%s] ...",
					quotePostCancellationProcessModel.getCode()));
		}

		final QuoteModel quoteModel = event.getQuote();
		quotePostCancellationProcessModel.setQuoteCode(quoteModel.getCode());
		getModelService().save(quotePostCancellationProcessModel);
		//start the business process
		getBusinessProcessService().startProcess(quotePostCancellationProcessModel);

	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
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

}

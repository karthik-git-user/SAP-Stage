/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.actions;



import static com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService.getPropertyValue;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteConversionService;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 *
 */
public class SapCpqCpiSendQuoteStatusAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SapCpqCpiSendQuoteStatusAction.class);

	private QuoteService quoteService;
	private SapCpqCpiOutboundQuoteConversionService quoteConversionService;
	private SapCpqCpiOutboundQuoteService sapCpqOutboundQuoteService;


	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{
		Transition result = Transition.NOK;
		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			SAPCPQOutboundQuoteStatusModel scpiQuoteStatus = null;
			
			scpiQuoteStatus = getQuoteConversionService().convertQuoteToSapCpiQuoteStatus(quote);

			getSapCpqOutboundQuoteService().sendQuoteStatus(scpiQuoteStatus).subscribe(

					// onNext
					responseEntityMap -> {

						Registry.activateMasterTenant();
						final String reponseMessage = getPropertyValue(responseEntityMap, "quoteId");
						if (null != reponseMessage && !reponseMessage.equals(""))
						{
							setQuoteStatus(quote, ExportStatus.EXPORTED);
							LOG.info(String.format("The quote status for [%s] has been successfully sent to the backend through SCPI! %n%s",
									quote.getCode(), reponseMessage));
							

						}
						else
						{
							setQuoteStatus(quote, ExportStatus.NOTEXPORTED);
							LOG.error(String.format("The quote status for [%s] has not been sent to the backend! %n%s", quote.getCode(),
									reponseMessage));

						}
						resetEndMessage(process, reponseMessage);
					}
					// onError
					, error -> {

						Registry.activateMasterTenant();
						setQuoteStatus(quote, ExportStatus.NOTEXPORTED);
						LOG.error(String.format("Error , The quote status for [%s] has not been sent to the backend through SCPI! %n%s", quote.getCode()));
						resetEndMessage(process, error.getMessage());

					});
			
			if (quote.getExportStatus().equals(ExportStatus.EXPORTED))
			{
				result = Transition.OK;
			}

		}
		return result;
	}

	protected void resetEndMessage(final QuoteProcessModel process, final String responseMessage)
	{
		process.setEndMessage(responseMessage);
		modelService.save(process);
	}
	
	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}


	protected void setQuoteStatus(final QuoteModel quote, final ExportStatus exportStatus)
	{
		quote.setExportStatus(exportStatus);
		modelService.save(quote);
	}

	public SapCpqCpiOutboundQuoteConversionService getQuoteConversionService()
	{
		return quoteConversionService;
	}

	public void setQuoteConversionService(SapCpqCpiOutboundQuoteConversionService quoteConversionService) {
		this.quoteConversionService = quoteConversionService;
	}
	public SapCpqCpiOutboundQuoteService getSapCpqOutboundQuoteService() {
		return sapCpqOutboundQuoteService;
	}

	public void setSapCpqOutboundQuoteService(SapCpqCpiOutboundQuoteService sapCpqOutboundQuoteService) {
		this.sapCpqOutboundQuoteService = sapCpqOutboundQuoteService;
	}



}

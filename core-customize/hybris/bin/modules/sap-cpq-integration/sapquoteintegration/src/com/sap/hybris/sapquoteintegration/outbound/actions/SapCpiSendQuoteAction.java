/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.actions;



import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.RESPONSE_MESSAGE;
import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.getPropertyValue;
import static com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService.isSentSuccessfully;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteConversionService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService;


/**
 *
 */
public class SapCpiSendQuoteAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = LogManager.getLogger(SapCpiSendQuoteAction.class);

	private QuoteService quoteService;
	private SapCpiOutboundQuoteConversionService quoteConversionService;
	private SapCpiOutboundQuoteService sapCpiOutboundQuoteService;

	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{
		Transition result = Transition.NOK;
		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			SAPCpiOutboundQuoteModel scpiQuote = null;
			try
			{
				scpiQuote = getQuoteConversionService().convertQuoteToSapCpiQuote(quote);
			}
			catch (final IllegalArgumentException e)
			{
				LOG.error("SCPI Quote Conversion failed due to improper data for quoteId: {} - {}", quote.getCode() ,e.getCause());
				return Transition.NOK;
			}

			getSapCpiOutboundQuoteService().sendQuote(scpiQuote).subscribe(

					// onNext
					responseEntityMap -> {

						Registry.activateMasterTenant();
						final String reponseMessage = getPropertyValue(responseEntityMap, RESPONSE_MESSAGE);
						if (isSentSuccessfully(responseEntityMap))
						{
							final String externalQuoteId = getPropertyValue(responseEntityMap, SAPCpiOutboundQuoteModel.EXTERNALQUOTEID);
							setQuoteStatus(quote, ExportStatus.EXPORTED, externalQuoteId);
							LOG.info(String.format("The quote [%s] has been successfully sent to the backend through SCPI! %n%s",
									quote.getCode(), reponseMessage));

						}
						else
						{
							setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
							LOG.info("The quote {} has not been sent to the backend! {}", quote.getCode(), reponseMessage);

						}
						resetEndMessage(process, reponseMessage);
					}
					// onError
					, error -> {

						Registry.activateMasterTenant();

						setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
						LOG.info("The quote {} has not been sent to the backend through SCPI! {}", quote.getCode(),error.getMessage());
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


	protected void setQuoteStatus(final QuoteModel quote, final ExportStatus exportStatus, final String externalQuoteId)
	{
		if (externalQuoteId != null)
		{
			quote.setExternalQuoteId(externalQuoteId);
		}
		quote.setExportStatus(exportStatus);
		modelService.save(quote);
	}

	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	public SapCpiOutboundQuoteConversionService getQuoteConversionService()
	{
		return quoteConversionService;
	}

	@Required
	public void setQuoteConversionService(final SapCpiOutboundQuoteConversionService quoteConversionService)
	{
		this.quoteConversionService = quoteConversionService;
	}

	public SapCpiOutboundQuoteService getSapCpiOutboundQuoteService()
	{
		return sapCpiOutboundQuoteService;
	}

	@Required
	public void setSapCpiOutboundQuoteService(final SapCpiOutboundQuoteService sapCpiOutboundQuoteService)
	{
		this.sapCpiOutboundQuoteService = sapCpiOutboundQuoteService;
	}

}

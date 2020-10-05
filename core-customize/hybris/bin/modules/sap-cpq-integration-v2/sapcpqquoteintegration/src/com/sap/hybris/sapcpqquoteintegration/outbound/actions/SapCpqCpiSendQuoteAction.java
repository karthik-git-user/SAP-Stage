/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.actions;



import static com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService.RESPONSE_MESSAGE;
import static com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService.getPropertyValue;
import static com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteService.isSentSuccessfully;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
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
public class SapCpqCpiSendQuoteAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SapCpqCpiSendQuoteAction.class);

	private QuoteService quoteService;
	private SapCpqCpiOutboundQuoteConversionService quoteConversionService;
	private SapCpqCpiOutboundQuoteService sapCpqCpiOutboundQuoteService;

	@Override
	public Transition executeAction(final QuoteProcessModel process)
	{
		Transition result = Transition.NOK;
		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			SAPCPQOutboundQuoteModel scpiQuote = null;
			try
			{
				scpiQuote = getQuoteConversionService().convertQuoteToSapCpiQuote(quote);
			}
			catch (final IllegalArgumentException e)
			{
				LOG.error("SCPI Quote Conversion failed due to improper data for quoteId:" + quote.getCode());
				return Transition.NOK;
			}

			getSapCpqCpiOutboundQuoteService().sendQuote(scpiQuote).subscribe(

					// onNext
					responseEntityMap -> {

						Registry.activateMasterTenant();
						final String externalQuoteId = getPropertyValue(responseEntityMap, "quoteId");
						if (null != externalQuoteId && !externalQuoteId.equals(""))
						{
							setQuoteStatus(quote, ExportStatus.EXPORTED, externalQuoteId);
							LOG.info(String.format("The quote [%s] has been successfully sent to the backend through SCPI! %n%s",
									quote.getCode(), externalQuoteId));

						}
						else
						{
							setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
							LOG.error(String.format("The quote [%s] has not been sent to the backend! %n%s", quote.getCode(),
									externalQuoteId));

						}
						resetEndMessage(process, externalQuoteId);
					}
					// onError
					, error -> {

						Registry.activateMasterTenant();

						setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
						LOG.error(String.format("The quote [%s] has not been sent to the backend through SCPI! %n%s", quote.getCode()));
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
			quote.setCpqExternalQuoteId(externalQuoteId);
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

	public SapCpqCpiOutboundQuoteConversionService getQuoteConversionService()
	{
		return quoteConversionService;
	}

	@Required
	public void setQuoteConversionService(final SapCpqCpiOutboundQuoteConversionService quoteConversionService)
	{
		this.quoteConversionService = quoteConversionService;
	}

	public SapCpqCpiOutboundQuoteService getSapCpqCpiOutboundQuoteService() {
		return sapCpqCpiOutboundQuoteService;
	}

	@Required
	public void setSapCpqCpiOutboundQuoteService(SapCpqCpiOutboundQuoteService sapCpqCpiOutboundQuoteService) {
		this.sapCpqCpiOutboundQuoteService = sapCpqCpiOutboundQuoteService;
	}

	

}

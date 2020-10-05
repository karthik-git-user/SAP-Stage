/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.outbound.actions;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.util.Config;
import rx.Observable;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants;
import com.sap.hybris.c4ccpiquote.model.SAPC4CCpiOutboundQuoteModel;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteConversionService;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteService;


/**
 *
 */
public class SendCancelSalesQuoteToC4CAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendCancelSalesQuoteToC4CAction.class);

	private QuoteService quoteService;
	private SapCpiOutboundC4CQuoteService sapCpiOutboundC4CQuoteService;
	private SapCpiOutboundC4CQuoteConversionService sapCpiC4CQuoteConversionService;

	@Override
	public Transition executeAction(final QuoteProcessModel process) throws Exception
	{
		Transition result = Transition.NOK;
		if (process.getQuoteCode() != null)
		{
			final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(process.getQuoteCode());
			SAPC4CCpiOutboundQuoteModel scpiQuote = null;
			try
			{
				scpiQuote = getSapCpiC4CQuoteConversionService().convertQuoteToSapCpiQuote(quote);
				scpiQuote.setCancellationReasonCode(Config.getString(C4ccpiquoteConstants.QUOTE_CANCELLATION_CODE,StringUtils.EMPTY));
				
			}
			catch (final IllegalArgumentException e)
			{
				LOG.error("SCPI Quote Conversion failed due to improper data for quoteId:" + quote.getCode(), e);
				return Transition.NOK;
			}

			if(quote.getState().equals(QuoteState.CANCELLED) && quote.getC4cQuoteExternalQuoteId() != null) {
				
				final Observable<ResponseEntity<Map>> outboundResponse = getSapCpiOutboundC4CQuoteService().sendQuote(scpiQuote);
				outboundResponse.subscribe(// onNext
						responseEntityMap -> {

							LOG.info(String.format("The quote [%s] has been successfully sent to the backend through SCPI! %n%s",
									quote.getCode(), "reponseMessage"));
							
						}
						// onError
						, error -> {

							LOG.error(String.format("The quote [%s] has not been sent to the backend through SCPI! %n%s", quote.getCode(),
									error.getMessage()));


						});
				result = Transition.OK;
			}
		}
		return result;
	}

	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	/**
	 * @param quoteService
	 *           the quoteService to set
	 */
	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	/**
	 * @return the sapCpiOutboundC4CQuoteService
	 */
	public SapCpiOutboundC4CQuoteService getSapCpiOutboundC4CQuoteService()
	{
		return sapCpiOutboundC4CQuoteService;
	}

	/**
	 * @param sapCpiOutboundC4CQuoteService
	 *           the sapCpiOutboundC4CQuoteService to set
	 */
	@Required
	public void setSapCpiOutboundSalesQuoteService(final SapCpiOutboundC4CQuoteService sapCpiOutboundSalesQuoteService)
	{
		this.sapCpiOutboundC4CQuoteService = sapCpiOutboundSalesQuoteService;
	}

	public SapCpiOutboundC4CQuoteConversionService getSapCpiC4CQuoteConversionService() {
		return sapCpiC4CQuoteConversionService;
	}
	
	@Required
	public void setSapCpiC4CQuoteConversionService(
			SapCpiOutboundC4CQuoteConversionService sapCpiC4CQuoteConversionService) {
		this.sapCpiC4CQuoteConversionService = sapCpiC4CQuoteConversionService;
	}

}
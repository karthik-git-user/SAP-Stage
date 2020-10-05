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
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import rx.Observable;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteConversionService;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteService;



/**
 *
 */
public class SendSalesQuoteToC4CAction extends AbstractSimpleDecisionAction<QuoteProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SendSalesQuoteToC4CAction.class);

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
			C4CSalesOrderNotificationModel orderNotificationModel = null;

			try
			{
				orderNotificationModel = getSapCpiC4CQuoteConversionService().convertQuoteToSalesOrderNotification(quote);
				
				
			}
			catch (final IllegalArgumentException e)
			{
				LOG.error("SCPI Order Notification failed  :" + quote.getCode(), e);
				return Transition.NOK;
			}
			LOG.info("Quote Sales Action " + quote.getCode());
			final Observable<ResponseEntity<Map>> outboundResponse = sapCpiOutboundC4CQuoteService.sendOrderNotification(orderNotificationModel);
			outboundResponse.subscribe(// onNext
					responseEntityMap -> {

						LOG.info(String.format("The order Id [%s] has been successfully sent to the backend through SCPI! %n%s",
								quote.getQuoteOrderId(), "reponseMessage"));
						
					}
					// onError
					, error -> {

						LOG.error(String.format("The order Id [%s] has not been sent to the backend through SCPI! %n%s", quote.getQuoteOrderId(),
								error.getMessage()));


					});
			
			result = Transition.OK;

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

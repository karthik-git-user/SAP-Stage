/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.inbound.helper.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqQuoteHelper;
import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;
import com.sap.hybris.sapcpqsbintegration.outbound.service.SapCpqSbOutboundQuoteConversionService;
import com.sap.hybris.sapcpqsbintegration.outbound.service.SapCpqSbOutboundQuoteService;

import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.QuoteModel;

public class DefaultCpqSbQuoteHelper implements CpqQuoteHelper {
	
	
	private SapCpqSbOutboundQuoteConversionService quoteConversionService;
	private SapCpqSbOutboundQuoteService sapCpqSbOutboundQuoteService;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCpqSbQuoteHelper.class);

	@Override
	public QuoteModel performQuoteOperation(QuoteModel quoteModel) {

		LOG.info("Entering DefaultCpqSbQuoteHelper#performQuoteOperation");
		if(CollectionUtils.isNotEmpty(quoteModel.getCpqQuoteEntries())) {
			
				SubscriptionPricingOutboundModel subscriptionPricingRequest = quoteConversionService.createSubscriptionPricingRequest(quoteModel);
				
				if (CollectionUtils.isNotEmpty(subscriptionPricingRequest.getRequests())) {
					
					getSapCpqSbOutboundQuoteService().requestSubscriptionPricing(subscriptionPricingRequest)
							.subscribe(

									// onNext
									responseEntityMap -> {

										Registry.activateMasterTenant();
										LOG.info(String.format("Success"));
									}
									// onError
									, error -> {
										LOG.info(String.format("Failure"));
									});
				}

		}
		LOG.info("Exiting DefaultCpqSbQuoteHelper#performQuoteOperation");
		return quoteModel;
	}

	public SapCpqSbOutboundQuoteConversionService getQuoteConversionService() {
		return quoteConversionService;
	}

	public void setQuoteConversionService(SapCpqSbOutboundQuoteConversionService quoteConversionService) {
		this.quoteConversionService = quoteConversionService;
	}

	public SapCpqSbOutboundQuoteService getSapCpqSbOutboundQuoteService() {
		return sapCpqSbOutboundQuoteService;
	}

	public void setSapCpqSbOutboundQuoteService(SapCpqSbOutboundQuoteService sapCpqSbOutboundQuoteService) {
		this.sapCpqSbOutboundQuoteService = sapCpqSbOutboundQuoteService;
	}
	
	

}

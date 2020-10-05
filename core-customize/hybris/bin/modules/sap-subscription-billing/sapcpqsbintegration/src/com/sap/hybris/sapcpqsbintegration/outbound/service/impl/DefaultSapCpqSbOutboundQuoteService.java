/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.outbound.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;
import com.sap.hybris.sapcpqsbintegration.outbound.service.SapCpqSbOutboundQuoteService;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import rx.Observable;


/**
 * Default implementation of {@link SapCpqSbOutboundQuoteService}
 */
public class DefaultSapCpqSbOutboundQuoteService implements SapCpqSbOutboundQuoteService
{

    private static final String SUBSCRIPTION_PRICING = "SubscriptionPricing";
    private static final String SUBSCRIPTION_PRICING_DESTINATIOION = "scpiSubscriptionPricingDestination";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> requestSubscriptionPricing(
			SubscriptionPricingOutboundModel subscriptionPricingOutboundModel) {
		
		return getOutboundServiceFacade().send(subscriptionPricingOutboundModel, SUBSCRIPTION_PRICING, SUBSCRIPTION_PRICING_DESTINATIOION);
	}

	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	@Required
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}

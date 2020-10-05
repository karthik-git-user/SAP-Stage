/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.inbound.helper.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqInboundQuoteHelper;
import com.sap.hybris.sapcpqsbintegration.constants.SapcpqsbintegrationConstants;
import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;

import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

public class DefaultCpqSbInboundQuoteHelper implements CpqInboundQuoteHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCpqSbInboundQuoteHelper.class);

	@Override
	public QuoteModel processInboundQuote(QuoteModel inboundQuote) {
		LOG.info("Enter DefaultCpqSbInboundQuoteHelper#processInboundQuote");

		if(null != inboundQuote && CollectionUtils.isNotEmpty(inboundQuote.getCpqQuoteEntries())) {
			

			List<QuoteEntryModel> quoteEntries = inboundQuote.getCpqQuoteEntries().stream().filter(entry -> entry.getProductTypeName().equals(SapcpqsbintegrationConstants.SUBSCRIPTION_TYPE)).collect(Collectors.toList());
			
			for(QuoteEntryModel entry : quoteEntries) {
				
				CpqSubscriptionDetailModel subscriptionDetail = entry.getCpqSubscriptionDetails().get(0);
				
				subscriptionDetail.getPricingParameters().stream().forEach(priceItem -> priceItem.setItemId(subscriptionDetail.getItemId()));
				
			}
			
		}
		LOG.info("Exit DefaultCpqSbInboundQuoteHelper#processInboundQuote");
		return inboundQuote;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.outbound.service.impl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.sapcpqsbintegration.constants.SapcpqsbintegrationConstants;
import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;
import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;
import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundRequestModel;
import com.sap.hybris.sapcpqsbintegration.outbound.service.SapCpqSbOutboundQuoteConversionService;

import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 *
 */
public class DefaultSapCpqSbOutboundQuoteConversionService implements SapCpqSbOutboundQuoteConversionService {

    protected static final Logger LOG = Logger.getLogger(DefaultSapCpqSbOutboundQuoteConversionService.class);
    
    @Override
	public SubscriptionPricingOutboundModel createSubscriptionPricingRequest(QuoteModel quoteModel) {

    	SubscriptionPricingOutboundModel subscriptionPricingModel = new SubscriptionPricingOutboundModel();
    	
    	subscriptionPricingModel.setCpqQuoteId(quoteModel.getCode());
    	subscriptionPricingModel.setCpqQuoteVersion(quoteModel.getVersion().toString());
    	List<SubscriptionPricingOutboundRequestModel> requests = new ArrayList<>();
    	
    	subscriptionPricingModel.setExternalSystemId("dummy");
    	
    	if(CollectionUtils.isNotEmpty(quoteModel.getCpqQuoteEntries())) {
    		
    		List<QuoteEntryModel> subscriptionEntires = quoteModel.getCpqQuoteEntries()
    				.stream().filter(entry -> entry.getProductTypeName().equals(SapcpqsbintegrationConstants.SUBSCRIPTION_TYPE))
    				.collect(Collectors.toList());
        	
        	for(QuoteEntryModel entry : subscriptionEntires) {
            	
            	/*request to fetch discounted values for product*/
        		
        		CpqSubscriptionDetailModel subscriptionDetail = entry.getCpqSubscriptionDetails().get(0);
            	
        		if(null != subscriptionDetail) {
        			SubscriptionPricingOutboundRequestModel requestWithPriceParam = new SubscriptionPricingOutboundRequestModel();
            		requestWithPriceParam.setRatePlanId(subscriptionDetail.getRatePlanId());
            		
                	
                	requestWithPriceParam.setId(entry.getCpqExternalQuoteEntryId() +"_" +entry.getEntryNumber());
                	requestWithPriceParam.setMethod("POST");
                	requestWithPriceParam.setUrl(subscriptionDetail.getRatePlanId());
                	if(null != subscriptionDetail.getEffectiveDate()) {
                		
                		requestWithPriceParam.setEffectiveAt(subscriptionDetail.getEffectiveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
                	}
                	
                	requestWithPriceParam.setPricingParameters(subscriptionDetail.getPricingParameters());
                	
                	
                	requests.add(requestWithPriceParam);
        		}
        	}
    	}
    	
    	subscriptionPricingModel.setRequests(requests);
    	
    	return subscriptionPricingModel;
	}

}

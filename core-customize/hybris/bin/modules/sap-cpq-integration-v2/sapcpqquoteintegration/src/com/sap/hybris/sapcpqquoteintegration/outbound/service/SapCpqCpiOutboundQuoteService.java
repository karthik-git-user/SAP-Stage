/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;

import rx.Observable;


/**
 *
 */
public interface SapCpqCpiOutboundQuoteService
{

	String OK = "OK";
	String RESPONSE_STATUS = "responseStatus";
	String RESPONSE_MESSAGE = "responseMessage";

	Observable<ResponseEntity<Map>> sendQuote(SAPCPQOutboundQuoteModel sapCPQOutboundQuoteModel);
	Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCPQOutboundQuoteStatusModel sapCPQOutboundQuoteStatusModel);
	

	static boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		boolean isSentSuccessfully = false;
		if (OK.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful())
		{
			isSentSuccessfully = true;
		}
		return isSentSuccessfully;
	}

	static String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{

		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		checkArgument(next != null, String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();
		checkArgument(!responseKey.isEmpty(),
				String.format("SCPI response property cannot be empty for property [%s]!", property));

		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		checkArgument(propertyValue != null, String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}

}

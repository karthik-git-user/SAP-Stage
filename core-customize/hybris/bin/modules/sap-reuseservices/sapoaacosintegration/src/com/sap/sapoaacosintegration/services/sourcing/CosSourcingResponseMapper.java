/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing;

import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponse;


/**
 * Response mapper for COS sourcing request
 */
public interface CosSourcingResponseMapper
{

	/**
	 * @param cosSourcingResponse
	 * @return {@link SourcingResponse}
	 */
	SourcingResponse mapCosSourcingResponseToSourcingResponse(CosSourcingResponse cosSourcingResponse);

}

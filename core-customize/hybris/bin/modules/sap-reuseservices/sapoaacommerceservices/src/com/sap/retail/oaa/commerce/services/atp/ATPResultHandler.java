/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPBatchResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResponse;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;


/**
 * ATP Result Handler
 */
public interface ATPResultHandler
{

	/**
	 * Extracts the aggregated availability from the ATP response object
	 *
	 * @param atp
	 * @return list of aggregated availability info for given product
	 * @throws ATPException
	 */
	List<ATPAvailability> extractATPAvailabilityFromATPResponse(final ATPResponse atp);


	/**
	 * Extracts the aggregated availability from the ATP product response object
	 *
	 * @param atpBatch
	 * @return list of aggregated availability info for a list of products
	 * @throws ATPException
	 */
	List<ATPProductAvailability> extractATPProductAvailabilityFromATPBatchResponse(final ATPBatchResponse atpBatch);
}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;


/**
 * COS ATP Result Handler
 */
public interface CosATPResultHandler
{

	/**
	 * Extracts the aggregated availability from the ATP response object
	 *
	 * @param articleResponse
	 * @return list of aggregated availability info for given product
	 * @throws ATPException
	 */
	List<ATPAvailability> extractATPAvailabilityFromArticleResponse(final List<ArticleResponse> articles);

	/**
	 * Extracts the aggregated availability from the ATP response object
	 *
	 * @param articleResponse
	 * @return list of aggregated product availability info for given product
	 * @throws ATPException
	 */
	List<ATPProductAvailability> extractATPProductAvailabilityFromArticleResponse(List<ArticleResponse> articles);
}
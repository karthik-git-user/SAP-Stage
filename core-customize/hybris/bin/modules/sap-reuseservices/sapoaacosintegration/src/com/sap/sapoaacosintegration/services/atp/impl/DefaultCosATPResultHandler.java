/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.sapoaacosintegration.services.atp.CosATPResultHandler;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;

/**
 * Default atp result handler
 */
public class DefaultCosATPResultHandler implements CosATPResultHandler
{
	private ServiceUtils serviceUtils;


	@Override
	public List<ATPAvailability> extractATPAvailabilityFromArticleResponse(final List<ArticleResponse> articles)
	{
		return getAvailabilities(articles);
	}

	@Override
	public List<ATPProductAvailability> extractATPProductAvailabilityFromArticleResponse(
			final List<ArticleResponse> articleResponse)
	{
		return getProductAvailabilities(articleResponse);

	}


	/**
	 * Get all product availability entries from the response item.
	 *
	 * @param articleResponse
	 * @return list of all product availabilities
	 */
	protected List<ATPProductAvailability> getProductAvailabilities(final List<ArticleResponse> articleResponse)
	{
		final List<ATPProductAvailability> productAvailabilityList = new ArrayList<>();
		for (final ArticleResponse article : articleResponse)
		{
			final ATPProductAvailability productAvailability = getProductAvailability(article);
			productAvailability.setAvailabilityList(getAvailabilities(articleResponse));
			productAvailabilityList.add(productAvailability);
		}
		return productAvailabilityList;
	}

	/**
	 * Get single product availability entry from the response item.
	 *
	 * @param article
	 * @return productAvailability
	 */
	protected ATPProductAvailability getProductAvailability(final ArticleResponse article)
	{
		final ATPProductAvailability productAvailability = new ATPProductAvailability();
		productAvailability.setArticleId(serviceUtils.removeLeadingZeros(article.getProductId()));
		productAvailability.setSourceId(article.getSourceId());
		return productAvailability;
	}

	/**
	 * Get all availability entries from the response item.
	 *
	 * @param articles
	 * @return list of all availabilities
	 */
	protected List<ATPAvailability> getAvailabilities(final List<ArticleResponse> articles)
	{
		final List<ATPAvailability> availabilities = new ArrayList<>();

		if (articles.isEmpty())
		{ // in case the service return no availability lines
			final ATPAvailability noStockAvailable = new ATPAvailability();
			noStockAvailable.setQuantity(Double.valueOf("0"));
			noStockAvailable.setAtpDate(new Date());
			availabilities.add(noStockAvailable);
		}
		else
		{
			for (final ArticleResponse article : articles)
			{
				availabilities.add(getAvailability(article));
			}
		}
		return availabilities;
	}

	/**
	 * Get single availability entry from the response item.
	 *
	 * @param availability
	 * @return availability
	 */
	protected ATPAvailability getAvailability(final ArticleResponse article)

	{
		final ATPAvailability availabilityModel = new ATPAvailability();
		availabilityModel.setQuantity(article.getQuantity());
		availabilityModel.setAtpDate(serviceUtils.parseStringToDate(article.getAvailableFrom()));
		return availabilityModel;
	}


	/**
	 * Checks if the ATPResponse is valid.
	 *
	 * @param articles
	 * @throws ATPException
	 */
	protected void validateResponse(final List<ArticleResponse> articles)
	{
		if (articles.isEmpty())
		{
			throw new ATPException("COS ATP rest service has not returned sufficent data");
		}
	}

	/**
	 * @param serviceUtils
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}




}
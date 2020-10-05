/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.dao;

/**
 * Holds data needed to make offer recommendation request
 */
public class OfferRecommendationContext extends RecommendationContext
{
	protected String contentPosition;

	public String getContentPosition()
	{
		return contentPosition;
	}

	public void setContentPosition(final String contentPosition)
	{
		this.contentPosition = contentPosition;
	}

}

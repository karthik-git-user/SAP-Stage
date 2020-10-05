/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.dao;

/**
 *
 * data structure that holds the interaction data context
 *
 */
public class InteractionContext
{
	private String productId;
	private String productType;
	private String scenarioId;
	private String sourceObjectId;

	/**
	 * @return productId
	 */
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @return productType
	 */
	public String getProductType()
	{
		return productType;
	}

	/**
	 * @return scenarioId
	 */
	public String getScenarioId()
	{
		return scenarioId;
	}

	/**
	 * @return the sourceObjectId
	 */
	public String getSourceObjectId()
	{
		return sourceObjectId;
	}

	/**
	 * @param productId
	 */
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @param productType
	 */
	public void setProductType(final String productType)
	{
		this.productType = productType;
	}

	/**
	 * @param scenarioId
	 */
	public void setScenarioId(final String scenarioId)
	{
		this.scenarioId = scenarioId;
	}

	/**
	 * @param sourceObjectId
	 *           the sourceObjectId to set
	 */
	public void setSourceObjectId(final String sourceObjectId)
	{
		this.sourceObjectId = sourceObjectId;
	}

}

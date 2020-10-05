/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.utils;

/**
 *
 */
public class ImpressionCounters
{
	private int impressionCount = 0;
	private int itemCount = 0;

	public void addToImpressionCount(final int newCount)
	{
		this.impressionCount += newCount;
	}

	public void addToItemCount(final int newCount)
	{
		this.itemCount += newCount;
	}

	public int getImpressionCount()
	{
		return impressionCount;
	}

	public int getItemCount()
	{
		return itemCount;
	}
}

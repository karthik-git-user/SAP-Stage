/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.pojos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 */
public class ATPProductAvailability
{
	private String articleId;
	private String sourceId;
	private List<ATPAvailability> availabilityList;


	public ATPProductAvailability()
	{
		availabilityList = new ArrayList<>();
	}

	public ATPProductAvailability(final String articleId, final String sourceId, final List<ATPAvailability> availabilityList)
	{
		this();
		this.articleId = articleId;
		this.sourceId = sourceId;
		this.availabilityList = availabilityList;
	}

	/**
	 * @return the articleId
	 */
	public String getArticleId()
	{
		return articleId;
	}

	/**
	 * @param articleId
	 *           the articleId to set
	 */
	public void setArticleId(final String articleId)
	{
		this.articleId = articleId;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId()
	{
		return sourceId;
	}

	/**
	 * @param sourceId
	 *           the sourceId to set
	 */
	public void setSourceId(final String sourceId)
	{
		this.sourceId = sourceId;
	}

	/**
	 * @return the availabilityList
	 */
	public List<ATPAvailability> getAvailabilityList()
	{
		return availabilityList;
	}

	/**
	 * @param availabilityList
	 *           the availabilityList to set
	 */
	public void setAvailabilityList(final List<ATPAvailability> availabilityList)
	{
		this.availabilityList = availabilityList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[articleId=");
		strBuilder.append(this.articleId);
		strBuilder.append(", ");
		strBuilder.append("sourceId=");
		strBuilder.append(this.sourceId);
		strBuilder.append(", ");
		strBuilder.append("availabilityList=");
		final Iterator<ATPAvailability> iterator = this.availabilityList.iterator();

		while (iterator.hasNext())
		{
			strBuilder.append(iterator.next().toString());
			if (iterator.hasNext())
			{
				strBuilder.append(", ");
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}
}

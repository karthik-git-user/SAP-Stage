/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilitiesResponse;


/**
 * Jaxb Pojo for XML reading
 */
public class ATPResultItem
{
	private String articleId;
	private String sourceId;
	private AvailabilitiesResponse availabilities;

	@XmlElement(name = "ARTICLE_ID")
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

	@XmlElement(name = "SOURCE_ID")
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

	@XmlElement(name = "AVAILABILITY")
	public AvailabilitiesResponse getAtpAvailabilities()
	{
		return availabilities;
	}

	/**
	 * @param availabilities
	 *           the availabilities to set
	 */
	public void setAtpAvailabilities(final AvailabilitiesResponse availabilities)
	{
		this.availabilities = availabilities;
	}
}

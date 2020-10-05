/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.dao;

public class SAPOfferContentPosition
{
	protected String communicationMediumName;
	protected final String contentPositionId;

	public SAPOfferContentPosition(final String contentPositionId)
	{
		this.contentPositionId = contentPositionId;
	}

	public String getCommunicationMediumName()
	{
		return communicationMediumName;
	}

	public String getContentPositionId()
	{
		return contentPositionId;
	}

	public void setCommunicationMediumName(final String communicationMediumName)
	{
		this.communicationMediumName = communicationMediumName;
	}
}

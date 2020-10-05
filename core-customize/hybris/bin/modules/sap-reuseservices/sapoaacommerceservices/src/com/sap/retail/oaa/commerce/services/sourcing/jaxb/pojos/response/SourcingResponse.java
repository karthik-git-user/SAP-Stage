/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;


/**
 * Jaxb Pojo for XML reading
 */
@XmlRootElement(name = "SOURCING")
public class SourcingResponse
{
	private SourcingResultsResponse sourcingResults;
	private String reservationSuccessful;
	private boolean reservationSuccessfulBoolean;
	private MessagesList messages;

	@XmlElement(name = "SOURCING_RESULTS")
	public SourcingResultsResponse getSourcingResults()
	{
		return sourcingResults;
	}

	/**
	 * @param sourcingResults
	 *           the sourcingResults to set
	 */
	public void setSourcingResults(final SourcingResultsResponse sourcingResults)
	{
		this.sourcingResults = sourcingResults;
	}

	@XmlElement(name = "RESERVATION_SUCCESSFUL")
	public String getReservationSuccessful()
	{
		return reservationSuccessful;
	}

	/**
	 * @param reservationSuccessful
	 *           the reservationSuccessful to set
	 */
	public void setReservationSuccessful(final String reservationSuccessful)
	{
		this.reservationSuccessful = reservationSuccessful;

		if ("X".equals(reservationSuccessful))
		{
			this.setReservationSuccessfulBoolean(true);
		}
	}

	@XmlElement(name = "MESSAGES")
	public MessagesList getMessages()
	{
		return messages;
	}

	/**
	 * @param messages
	 *           the messages to set
	 */
	public void setMessages(final MessagesList messages)
	{
		this.messages = messages;
	}

	/**
	 * @return isReservationSuccessfulBoolean
	 */
	public boolean isReservationSuccessfulBoolean()
	{
		return reservationSuccessfulBoolean;
	}

	/**
	 * @param reservationSuccessfulBoolean
	 *           the reservationSuccessfulBoolean to set
	 */
	public void setReservationSuccessfulBoolean(final boolean reservationSuccessfulBoolean)
	{
		this.reservationSuccessfulBoolean = reservationSuccessfulBoolean;
	}
}

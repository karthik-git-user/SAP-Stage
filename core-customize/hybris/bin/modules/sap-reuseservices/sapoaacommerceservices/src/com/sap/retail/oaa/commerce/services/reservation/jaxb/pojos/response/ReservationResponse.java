/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;



/**
 * Jaxb Pojo for XML reading
 */
@XmlRootElement(name = "RESERVATION")
public class ReservationResponse
{
	private ReservationResultResponse reservationResult;
	private MessagesList messages;


	@XmlElement(name = "RESERVATION_RESULT")
	public ReservationResultResponse getReservationResult()
	{
		return reservationResult;
	}

	/**
	 * @param reservationResult
	 *           the reservationResult to set
	 */
	public void setReservationResult(final ReservationResultResponse reservationResult)
	{
		this.reservationResult = reservationResult;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("ReservationResponse ");
		strBuilder.append("[");
		if (reservationResult != null)
		{
			strBuilder.append(reservationResult.toString());
		}
		if (messages != null)
		{
			strBuilder.append(this.messages.toString());
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilitiesResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;


/**
 * Jaxb Pojo for XML reading
 */
@XmlRootElement(name = "ATP")
public class ATPResponse
{
	private AvailabilitiesResponse availabilities;
	private MessagesList messages;

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
}

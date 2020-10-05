/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;


/**
 * Jaxb Pojo for XML reading
 */
@XmlRootElement(name = "ATP")
public class ATPBatchResponse
{
	private ATPResultItems atpResultItems;
	private MessagesList messages;

	@XmlElement(name = "ATP_RESULT_ITEMS")
	public ATPResultItems getAtpResultItems()
	{
		return atpResultItems;
	}

	/**
	 * @param atpResultItems
	 *           the atpResultItems to set
	 */
	public void setAtpResultItems(final ATPResultItems atpResultItems)
	{
		this.atpResultItems = atpResultItems;
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

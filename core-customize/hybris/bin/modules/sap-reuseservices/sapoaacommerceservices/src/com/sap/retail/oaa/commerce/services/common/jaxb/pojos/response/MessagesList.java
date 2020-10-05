/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class MessagesList
{
	private List<Message> messages;

	@XmlElement(name = "MESSAGE")
	public List<Message> getMessages()
	{
		return messages;
	}

	/**
	 * @param messages
	 *           the messages to set
	 */
	public void setMessages(final List<Message> messages)
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
		strBuilder.append("MessagesList ");
		strBuilder.append("[");
		if (messages != null && !messages.isEmpty())
		{
			for (final Message message : messages)
			{
				strBuilder.append(message.toString());
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class Message
{
	private String type;
	private String id;
	private String number;
	private String messageText;
	private String logNo;
	private String logMsgNo;
	private String messageV1;
	private String messageV2;
	private String messageV3;
	private String messageV4;
	private String parameter;
	private String row;
	private String field;
	private String system;

	@XmlElement(name = "TYPE")
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *           the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}

	@XmlElement(name = "ID")
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	@XmlElement(name = "NUMBER")
	public String getNumber()
	{
		return number;
	}

	/**
	 * @param number
	 *           the number to set
	 */
	public void setNumber(final String number)
	{
		this.number = number;
	}

	@XmlElement(name = "MESSAGE")
	public String getMessageText()
	{
		return messageText;
	}

	/**
	 * @param message
	 *           the message to set
	 */
	public void setMessageText(final String message)
	{
		this.messageText = message;
	}

	@XmlElement(name = "LOG_NO")
	public String getLogNo()
	{
		return logNo;
	}

	/**
	 * @param logNo
	 *           the logNo to set
	 */
	public void setLogNo(final String logNo)
	{
		this.logNo = logNo;
	}

	@XmlElement(name = "LOG_MSG_NO")
	public String getLogMsgNo()
	{
		return logMsgNo;
	}

	/**
	 * @param logMsgNo
	 *           the logMsgNo to set
	 */
	public void setLogMsgNo(final String logMsgNo)
	{
		this.logMsgNo = logMsgNo;
	}

	@XmlElement(name = "MESSAGE_V1")
	public String getMessageV1()
	{
		return messageV1;
	}

	/**
	 * @param messageV1
	 *           the messageV1 to set
	 */
	public void setMessageV1(final String messageV1)
	{
		this.messageV1 = messageV1;
	}

	@XmlElement(name = "MESSAGE_V2")
	public String getMessageV2()
	{
		return messageV2;
	}

	/**
	 * @param messageV2
	 *           the messageV2 to set
	 */
	public void setMessageV2(final String messageV2)
	{
		this.messageV2 = messageV2;
	}

	@XmlElement(name = "MESSAGE_V3")
	public String getMessageV3()
	{
		return messageV3;
	}

	/**
	 * @param messageV3
	 *           the messageV3 to set
	 */
	public void setMessageV3(final String messageV3)
	{
		this.messageV3 = messageV3;
	}

	@XmlElement(name = "MESSAGE_V4")
	public String getMessageV4()
	{
		return messageV4;
	}

	/**
	 * @param messageV4
	 *           the messageV4 to set
	 */
	public void setMessageV4(final String messageV4)
	{
		this.messageV4 = messageV4;
	}

	@XmlElement(name = "PARAMETER")
	public String getParameter()
	{
		return parameter;
	}

	/**
	 * @param parameter
	 *           the parameter to set
	 */
	public void setParameter(final String parameter)
	{
		this.parameter = parameter;
	}

	@XmlElement(name = "ROW")
	public String getRow()
	{
		return row;
	}

	/**
	 * @param row
	 *           the row to set
	 */
	public void setRow(final String row)
	{
		this.row = row;
	}

	@XmlElement(name = "FIELD")
	public String getField()
	{
		return field;
	}

	/**
	 * @param field
	 *           the field to set
	 */
	public void setField(final String field)
	{
		this.field = field;
	}

	@XmlElement(name = "SYSTEM")
	public String getSystem()
	{
		return system;
	}

	/**
	 * @param system
	 *           the system to set
	 */
	public void setSystem(final String system)
	{
		this.system = system;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Message [id=" + id + ", number=" + number + ", type=" + type + ", messageV1=" + messageV1 + ", messageV2="
				+ messageV2 + ", messageV3=" + messageV3 + ", messageV4=" + messageV4 + ", messageText=" + messageText + ", system="
				+ system + "]";
	}
}

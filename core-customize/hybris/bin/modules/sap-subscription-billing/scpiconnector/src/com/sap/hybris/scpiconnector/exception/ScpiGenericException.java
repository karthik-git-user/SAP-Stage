/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.scpiconnector.exception;

/**
 *
 */
public class ScpiGenericException extends Exception
{

	private String code;
	private String reason;


	/**
	 * Constructor with errorCode and errorDescription
	 */
	public ScpiGenericException(final String code, final String reason)
	{
		super();
		this.code = code;
		this.reason = reason;
	}

	/**
	 * No argument constructor
	 */
	public ScpiGenericException()
	{
		super();
	}




	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return the reason
	 */
	public String getReason()
	{
		return reason;
	}

	@Override
	public String toString()
	{
		return String.format("SCPI generic exception. Error code [%s] and error reason [%s].", this.code, this.reason);
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * SAP Digital payment transaction result fields class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentTransactionResult
{
	@JsonProperty("DigitalPaymentTransaction")
	private String digitalPaymentTransaction;

	@JsonProperty("DigitalPaymentDateTime")
	private String digitalPaymentDateTime;

	@JsonProperty("DigitalPaytTransResult")
	private String digitalPaytTransResult;

	@JsonProperty("DigitalPaytTransRsltDesc")
	private String digitalPaytTransRsltDesc;

	@JsonProperty("ReferenceDocument")
	private String referenceDocument;

	/**
	 * @return the digitalPaymentTransaction
	 */
	public String getDigitalPaymentTransaction()
	{
		return digitalPaymentTransaction;
	}

	/**
	 * @param digitalPaymentTransaction
	 *           the digitalPaymentTransaction to set
	 */
	public void setDigitalPaymentTransaction(final String digitalPaymentTransaction)
	{
		this.digitalPaymentTransaction = digitalPaymentTransaction;
	}

	/**
	 * @return the digitalPaymentDateTime
	 */
	public String getDigitalPaymentDateTime()
	{
		return digitalPaymentDateTime;
	}

	/**
	 * @param digitalPaymentDateTime
	 *           the digitalPaymentDateTime to set
	 */
	public void setDigitalPaymentDateTime(final String digitalPaymentDateTime)
	{
		this.digitalPaymentDateTime = digitalPaymentDateTime;
	}

	/**
	 * @return the digitalPaytTransResult
	 */
	public String getDigitalPaytTransResult()
	{
		return digitalPaytTransResult;
	}

	/**
	 * @param digitalPaytTransResult
	 *           the digitalPaytTransResult to set
	 */
	public void setDigitalPaytTransResult(final String digitalPaytTransResult)
	{
		this.digitalPaytTransResult = digitalPaytTransResult;
	}

	/**
	 * @return the digitalPaytTransRsltDesc
	 */
	public String getDigitalPaytTransRsltDesc()
	{
		return digitalPaytTransRsltDesc;
	}

	/**
	 * @param digitalPaytTransRsltDesc
	 *           the digitalPaytTransRsltDesc to set
	 */
	public void setDigitalPaytTransRsltDesc(final String digitalPaytTransRsltDesc)
	{
		this.digitalPaytTransRsltDesc = digitalPaytTransRsltDesc;
	}

	/**
	 * @return the referenceDocument
	 */
	public String getReferenceDocument()
	{
		return referenceDocument;
	}

	/**
	 * @param referenceDocument
	 *           the referenceDocument to set
	 */
	public void setReferenceDocument(final String referenceDocument)
	{
		this.referenceDocument = referenceDocument;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment poll registered card result fields
 */
public class CisSapDigitalPaymentPollRegisteredCardResult extends CisSapDigitalPaymentTokenizedCardResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult;

	/**
	 * @return the cisSapDigitalPaymentTransactionResult
	 */
	public CisSapDigitalPaymentTransactionResult getCisSapDigitalPaymentTransactionResult()
	{
		return cisSapDigitalPaymentTransactionResult;
	}

	/**
	 * @param cisSapDigitalPaymentTransactionResult
	 *           the cisSapDigitalPaymentTransactionResult to set
	 */
	public void setCisSapDigitalPaymentTransactionResult(
			final CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult)
	{
		this.cisSapDigitalPaymentTransactionResult = cisSapDigitalPaymentTransactionResult;
	}



}

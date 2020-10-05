/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment deletion result list
 */
public class CisSapDigitalPaymentCardDeletionResultList
{

	@JsonProperty("DeletedPaymentCards")
	private List<CisSapDigitalPaymentCardDeletionResult> cisSapDigitalPaymentCardDeletionResult;

	/**
	 * @return the cisSapDigitalPaymentCardDeletionResult
	 */
	public List<CisSapDigitalPaymentCardDeletionResult> getCisSapDigitalPaymentCardDeletionResult()
	{
		return cisSapDigitalPaymentCardDeletionResult;
	}

	/**
	 * @param cisSapDigitalPaymentCardDeletionResult
	 *           the cisSapDigitalPaymentCardDeletionResult to set
	 */
	public void setCisSapDigitalPaymentCardDeletionResult(
			final List<CisSapDigitalPaymentCardDeletionResult> cisSapDigitalPaymentCardDeletionResult)
	{
		this.cisSapDigitalPaymentCardDeletionResult = cisSapDigitalPaymentCardDeletionResult;
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment Charge result list
 */
public class CisSapDigitalPaymentChargeResultList
{

	@JsonProperty("Charges")
	private List<CisSapDigitalPaymentChargeResult> cisSapDigitalPaymentChargeResults;

	/**
	 * @return the cisSapDigitalPaymentChargeResults
	 */
	public List<CisSapDigitalPaymentChargeResult> getCisSapDigitalPaymentChargeResults()
	{
		return cisSapDigitalPaymentChargeResults;
	}

	/**
	 * @param cisSapDigitalPaymentChargeResults
	 *           the cisSapDigitalPaymentChargeResults to set
	 */
	public void setCisSapDigitalPaymentChargeResults(
			final List<CisSapDigitalPaymentChargeResult> cisSapDigitalPaymentChargeResults)
	{
		this.cisSapDigitalPaymentChargeResults = cisSapDigitalPaymentChargeResults;
	}

}

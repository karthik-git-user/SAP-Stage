/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment refund result list
 */
public class CisSapDigitalPaymentRefundResultList
{

	@JsonProperty("Refunds")
	private List<CisSapDigitalPaymentRefundResult> cisSapDigitalPaymentRefundResuts;

	/**
	 * @return the cisSapDigitalPaymentRefundResuts
	 */
	public List<CisSapDigitalPaymentRefundResult> getCisSapDigitalPaymentRefundResuts()
	{
		return cisSapDigitalPaymentRefundResuts;
	}

	/**
	 * @param cisSapDigitalPaymentRefundResuts
	 *           the cisSapDigitalPaymentRefundResuts to set
	 */
	public void setCisSapDigitalPaymentRefundResuts(final List<CisSapDigitalPaymentRefundResult> cisSapDigitalPaymentRefundResuts)
	{
		this.cisSapDigitalPaymentRefundResuts = cisSapDigitalPaymentRefundResuts;
	}

}


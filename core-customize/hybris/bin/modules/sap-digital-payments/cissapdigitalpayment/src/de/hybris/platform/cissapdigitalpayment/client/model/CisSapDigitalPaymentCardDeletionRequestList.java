/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital Payment Card Deletion Request List
 */
public class CisSapDigitalPaymentCardDeletionRequestList
{
	@JsonProperty("PaymentCards")
	private List<CisSapDigitalPaymentTokenizedCardResult> cisSapDigitalPaymentCardDeletionReqList;

	/**
	 * @return the cisSapDigitalPaymentCardDeletionReqList
	 */
	public List<CisSapDigitalPaymentTokenizedCardResult> getCisSapDigitalPaymentCardDeletionReqList()
	{
		return cisSapDigitalPaymentCardDeletionReqList;
	}

	/**
	 * @param cisSapDigitalPaymentCardDeletionReqList
	 *           the cisSapDigitalPaymentCardDeletionReqList to set
	 */
	public void setCisSapDigitalPaymentCardDeletionReqList(
			final List<CisSapDigitalPaymentTokenizedCardResult> cisSapDigitalPaymentCardDeletionReqList)
	{
		this.cisSapDigitalPaymentCardDeletionReqList = cisSapDigitalPaymentCardDeletionReqList;
	}





}

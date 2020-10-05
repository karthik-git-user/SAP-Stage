/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment card source field class
 */
public class CisSapDigitalPaymentSource
{
	@JsonProperty("Card")
	private CisSapDigitalPaymentCard cisSapDigitalPaymentCard;

	/**
	 * @return the cisSapDigitalPaymentCard
	 */
	public CisSapDigitalPaymentCard getCisSapDigitalPaymentCard()
	{
		return cisSapDigitalPaymentCard;
	}

	/**
	 * @param cisSapDigitalPaymentCard
	 *           the cisSapDigitalPaymentCard to set
	 */
	public void setCisSapDigitalPaymentCard(final CisSapDigitalPaymentCard cisSapDigitalPaymentCard)
	{
		this.cisSapDigitalPaymentCard = cisSapDigitalPaymentCard;
	}



}

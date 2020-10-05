/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment payment service provider field class
 */
public class CisSapDigitalPaymentCard
{
	@JsonProperty("PaytCardByDigitalPaymentSrvc")
	private String paytCardByDigitalPaymentSrvc;

	/**
	 * @return the paytCardByDigitalPaymentSrvc
	 */
	public String getPaytCardByDigitalPaymentSrvc()
	{
		return paytCardByDigitalPaymentSrvc;
	}

	/**
	 * @param paytCardByDigitalPaymentSrvc
	 *           the paytCardByDigitalPaymentSrvc to set
	 */
	public void setPaytCardByDigitalPaymentSrvc(final String paytCardByDigitalPaymentSrvc)
	{
		this.paytCardByDigitalPaymentSrvc = paytCardByDigitalPaymentSrvc;
	}

}

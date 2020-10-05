/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * hold response of SAP Digital Payment after requesting for deletion of card(s).
 */
public class CisSapDigitalPaymentCardDeletionResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisDigitalPaymentCardDeletionTxResult;

	@JsonProperty("PaytCardByDigitalPaymentSrvc")
	private String paytCardByDigitalPaymentSrvc;



	/**
	 * @return the cisDigitalPaymentCardDeletionTxResult
	 */
	public CisSapDigitalPaymentTransactionResult getCisDigitalPaymentCardDeletionTxResult()
	{
		return cisDigitalPaymentCardDeletionTxResult;
	}

	/**
	 * @param cisDigitalPaymentCardDeletionTxResult
	 *           the cisDigitalPaymentCardDeletionTxResult to set
	 */
	public void setCisDigitalPaymentCardDeletionTxResult(
			final CisSapDigitalPaymentTransactionResult cisDigitalPaymentCardDeletionTxResult)
	{
		this.cisDigitalPaymentCardDeletionTxResult = cisDigitalPaymentCardDeletionTxResult;
	}

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

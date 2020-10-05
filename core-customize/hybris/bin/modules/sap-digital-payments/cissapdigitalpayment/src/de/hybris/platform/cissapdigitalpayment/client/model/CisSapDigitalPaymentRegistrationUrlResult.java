/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment registration URL result class. Contains the registration URL and the SAP Digital payment session
 * ID
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentRegistrationUrlResult
{
	@JsonProperty("PaymentCardRegistrationURL")
	private String paymentCardRegistrationURL;

	@JsonProperty("PaymentCardRegistrationSession")
	private String paymentCardRegistrationSession;

	/**
	 *
	 */
	public CisSapDigitalPaymentRegistrationUrlResult()
	{
		// YTODO Auto-generated constructor stub
	}


	/**
	 *
	 */
	public CisSapDigitalPaymentRegistrationUrlResult(final String paymentCardRegistrationURL,
			final String paymentCardRegistrationSession)
	{
		super();
		this.paymentCardRegistrationURL = paymentCardRegistrationURL;
		this.paymentCardRegistrationSession = paymentCardRegistrationSession;
	}



	/**
	 * @return the paymentCardRegistrationURL
	 */
	public String getPaymentCardRegistrationURL()
	{
		return paymentCardRegistrationURL;
	}

	/**
	 * @param paymentCardRegistrationURL
	 *           the paymentCardRegistrationURL to set
	 */
	public void setPaymentCardRegistrationURL(final String paymentCardRegistrationURL)
	{
		this.paymentCardRegistrationURL = paymentCardRegistrationURL;
	}

	/**
	 * @return the paymentCardRegistrationSession
	 */
	public String getPaymentCardRegistrationSession()
	{
		return paymentCardRegistrationSession;
	}

	/**
	 * @param paymentCardRegistrationSession
	 *           the paymentCardRegistrationSession to set
	 */
	public void setPaymentCardRegistrationSession(final String paymentCardRegistrationSession)
	{
		this.paymentCardRegistrationSession = paymentCardRegistrationSession;
	}

	@Override
	public String toString()
	{
		// YTODO Auto-generated method stub
		return "{PaymentCardRegistrationURL='" + paymentCardRegistrationURL + '\'' + "PaymentCardRegistrationSession='"
				+ paymentCardRegistrationSession + '}';
	}




}

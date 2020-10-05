/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment authorization result class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentAuthorizationResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult;

	@JsonProperty("Authorization")
	private CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization;

	@JsonProperty("Source")
	private CisSapDigitalPaymentSource cisSapDigitalPaymentSource;



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

	/**
	 * @return the cisSapDigitalPaymentAuthorization
	 */
	public CisSapDigitalPaymentAuthorization getCisSapDigitalPaymentAuthorization()
	{
		return cisSapDigitalPaymentAuthorization;
	}

	/**
	 * @param cisSapDigitalPaymentAuthorization
	 *           the cisSapDigitalPaymentAuthorization to set
	 */
	public void setCisSapDigitalPaymentAuthorization(final CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization)
	{
		this.cisSapDigitalPaymentAuthorization = cisSapDigitalPaymentAuthorization;
	}

	/**
	 * @return the cisSapDigitalPaymentSource
	 */
	public CisSapDigitalPaymentSource getCisSapDigitalPaymentSource()
	{
		return cisSapDigitalPaymentSource;
	}

	/**
	 * @param cisSapDigitalPaymentSource
	 *           the cisSapDigitalPaymentSource to set
	 */
	public void setCisSapDigitalPaymentSource(final CisSapDigitalPaymentSource cisSapDigitalPaymentSource)
	{
		this.cisSapDigitalPaymentSource = cisSapDigitalPaymentSource;
	}

	@Override
	public String toString()
	{
		return "{ " + "Authorizations : [ " + "{ \'DigitalPaymentTransaction\' : {" + " \'DigitalPaymentTransaction\' :" + "\'"
				+ getCisSapDigitalPaymentTransactionResult().getDigitalPaymentTransaction() + "\'," + " \'DigitalPaymentDateTime\' :"
				+ "\'" + getCisSapDigitalPaymentTransactionResult().getDigitalPaymentDateTime() + "\',"
				+ " \'DigitalPaytTransResult\' :" + "\'" + getCisSapDigitalPaymentTransactionResult().getDigitalPaytTransResult()
				+ "\'," + " \'DigitalPaytTransRsltDesc\' :" + "\'"
				+ getCisSapDigitalPaymentTransactionResult().getDigitalPaytTransRsltDesc() + "\'" + "}," + "\'Authorization\': {"
				+ " \'AuthorizationByPaytSrvcPrvdr\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizationByPaytSrvcPrvdr() + "\'," + " \'AuthorizationByAcquirer\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationByAcquirer() + "\',"
				+ " \'AuthorizationByDigitalPaytSrvc\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizationByDigitalPaytSrvc() + "\',"
				+ " \'AuthorizedAmountInAuthznCrcy\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizedAmountInAuthznCrcy() + "\'," + " \'AuthorizationCurrency\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationCurrency() + "\'," + " \'AuthorizationDateTime\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationDateTime() + "\'," + " \'AuthorizationStatus\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationStatus() + "\'," + " \'AuthorizationStatusName\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationStatusName() + "\'" + "}," + "\'Source\': {"
				+ "\'Card\': {" + " \'PaytCardByDigitalPaymentSrvc\' :" + "\'"
				+ getCisSapDigitalPaymentSource().getCisSapDigitalPaymentCard().getPaytCardByDigitalPaymentSrvc() + "\'" + "}" + "}"
				+ "}" + "]" + "}";
	}



}

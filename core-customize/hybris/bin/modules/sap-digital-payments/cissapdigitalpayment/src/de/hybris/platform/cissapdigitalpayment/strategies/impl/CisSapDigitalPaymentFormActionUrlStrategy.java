/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRegistrationUrlResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRegisterUrlException;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation class for PaymentFormActionUrlStrategy. SAP Digital payment extension only handle HOP request.
 */
public class CisSapDigitalPaymentFormActionUrlStrategy implements PaymentFormActionUrlStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(CisSapDigitalPaymentFormActionUrlStrategy.class);

	private CisSapDigitalPaymentService cisSapDigitalPaymentService;
	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;
	private SessionService sessionService;


	/**
	 * Invoke the CisSapDigitalPaymentService to get the registration URL and the session ID. Session ID is saved to
	 * Hybris session which is ater used to poll the registered card with SAP Digital payment. The URL is passed to the
	 * facade layer and the user will be redirected to this URL to make payment
	 */
	@Override
	public String getHopRequestUrl()
	{
		CisSapDigitalPaymentRegistrationUrlResult cisSapDigitalPaymentRegistrationUrlResult;

		try
		{
			cisSapDigitalPaymentRegistrationUrlResult = getCisSapDigitalPaymentService().getRegistrationUrl().toBlocking().first();
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while requesting for the card registration URL from SAP Digital payment [%s]", e));
			}
			LOG.error(String.format("Error while requesting for the card registration URL from SAP Digital payment [%s]", e.getMessage()));
			throw new SapDigitalPaymentRegisterUrlException(
					"Digital payment getregistrationurl request failed. Service unavalable" + e.getMessage());
		}

		if (null != cisSapDigitalPaymentRegistrationUrlResult.getPaymentCardRegistrationSession())
		{
			getSessionService().setAttribute(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_REG_CARD_SESSION_ID,
					cisSapDigitalPaymentRegistrationUrlResult.getPaymentCardRegistrationSession());
		}


		return cisSapDigitalPaymentRegistrationUrlResult.getPaymentCardRegistrationURL() != null
				? cisSapDigitalPaymentRegistrationUrlResult.getPaymentCardRegistrationURL()
				: StringUtils.EMPTY;
	}

	@Override
	public String getSopRequestUrl(final String clientRef)
	{
		//Dummy implementation. For digital payment the site.pci.strategy is always HOP.
		return null;
	}



	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}

	/**
	 * @return the paymentFormActionUrlStrategy
	 */
	public PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	/**
	 * @param paymentFormActionUrlStrategy
	 *           the paymentFormActionUrlStrategy to set
	 */
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


}

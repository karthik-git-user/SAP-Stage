/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;

import org.apache.log4j.Logger;


/**
 * Out of the box split authorization amount implementation for {@link SapDigitalPaymentAuthorizationStrategy}
 */
public class SapDigitalPaymentSplitAuthorizationStrategy implements SapDigitalPaymentAuthorizationStrategy
{
	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentSplitAuthorizationStrategy.class);


	//out of the box implementation for the customer to override
	@Override
	public boolean authorizePayment(final CommerceCheckoutParameter parameter)
	{
		LOG.error("Dummy implementation. Customer should provide logic for payment amount authorzation split");

		return false;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service.impl;

import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;

import org.apache.log4j.Logger;


/**
 * SAP Digital-Payment Addon specific implementation extending {@link DefaultCardPaymentServiceImpl } for creating
 * subscription
 */
public class DefaultSapDigitalCardPaymentService extends DefaultCardPaymentServiceImpl
{

	private static final Logger LOG = Logger.getLogger(DefaultSapDigitalCardPaymentService.class);

	//Subscription ID is copied from the
	@Override
	public SubscriptionResult createSubscription(final CreateSubscriptionRequest request) throws AdapterException
	{
		SubscriptionResult subscriptionResult = new SubscriptionResult();
		try
		{


			if (null != request && null != request.getCard() && null != request.getCard().getCardToken())
			{
				subscriptionResult.setSubscriptionID(request.getCard().getCardToken());
				subscriptionResult.setTransactionStatus(TransactionStatus.ACCEPTED);
				subscriptionResult.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
			}
			else
			{
				//If the card token value is empty, call the default implementation for create subscription
				subscriptionResult = super.createSubscription(request);
			}
		}
		catch (final AdapterException e)
		{
			subscriptionResult.setTransactionStatus(TransactionStatus.REJECTED);
			subscriptionResult.setTransactionStatusDetails(TransactionStatusDetails.AUTHORIZATION_REJECTED_BY_PSP);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
			LOG.error("Error while creating subscription" + e.getMessage());
		}
		return subscriptionResult;

	}

}

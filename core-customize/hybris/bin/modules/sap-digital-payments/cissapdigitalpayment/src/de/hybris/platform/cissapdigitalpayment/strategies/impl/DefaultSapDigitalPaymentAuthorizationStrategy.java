/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;


/**
 * Default implementation for {@link SapDigitalPaymentAuthorizationStrategy}
 */
public class DefaultSapDigitalPaymentAuthorizationStrategy implements SapDigitalPaymentAuthorizationStrategy
{


	private CommerceCheckoutService commerceCheckoutService;


	@Override
	public boolean authorizePayment(final CommerceCheckoutParameter parameter)
	{
		final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService().authorizePayment(parameter);

		final boolean authorizePayment = paymentTransactionEntryModel != null
				&& (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
						|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));

		return authorizePayment;
	}

	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}



}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionRequestList;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

import java.util.List;


/**
 * Define the customer account service related to SAP Digital Payments
 */
public interface SapDigitalPaymentCustomerAccountService
{



	/**
	 * Create delete card request list
	 *
	 * @param creditCardPaymentInfoList
	 *           - credit card payment info list
	 * @return CisSapDigitalPaymentCardDeletionRequestList
	 */
	CisSapDigitalPaymentCardDeletionRequestList createDeleteCardRequestList(
			final List<CreditCardPaymentInfoModel> creditCardPaymentInfoList);

}

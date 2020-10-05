/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.events;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.AbstractQuoteSubmitEvent;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Event to indicate that buyer submitted a quote.
 */
public class SapCpiQuoteBuyerSubmitEvent extends AbstractQuoteSubmitEvent<BaseSiteModel>
{
	/**
	 * Default Constructor
	 *
	 * @param quote
	 * @param userModel
	 * @param quoteUserType
	 */
	public SapCpiQuoteBuyerSubmitEvent(final QuoteModel quote, final UserModel userModel, final QuoteUserType quoteUserType)
	{
		super(quote, userModel, quoteUserType);
	}
}

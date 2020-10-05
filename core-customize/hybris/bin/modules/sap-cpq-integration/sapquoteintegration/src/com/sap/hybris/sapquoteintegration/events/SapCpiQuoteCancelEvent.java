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
 *
 */
public class SapCpiQuoteCancelEvent extends AbstractQuoteSubmitEvent<BaseSiteModel>
{

	/**
	 *
	 */
	public SapCpiQuoteCancelEvent(final QuoteModel quote, final UserModel userModel, final QuoteUserType quoteUserType)
	{
		super(quote, userModel, quoteUserType);
	}

}

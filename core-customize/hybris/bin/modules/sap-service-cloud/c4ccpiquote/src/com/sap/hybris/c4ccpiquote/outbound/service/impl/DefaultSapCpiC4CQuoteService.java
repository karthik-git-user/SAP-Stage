/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.outbound.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;

import com.sap.hybris.c4ccpiquote.events.SapC4CCpiQuoteBuyerSubmitEvent;
import com.sap.hybris.c4ccpiquote.events.SapC4CCpiQuoteCancelEvent;


/**
 * Default Implementation for DefaultCommerceQuoteService
 */
public class DefaultSapCpiC4CQuoteService extends DefaultCommerceQuoteService
{

	@Override
	public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel)
	{


		QuoteModel quoteToCancel = quoteModel;
		validateParameterNotNullStandardMessage("quoteModel", quoteToCancel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		/* final String externalQuoteId = quoteToCancel.getExternalQuoteId() */
		getQuoteActionValidationStrategy().validate(QuoteAction.CANCEL, quoteToCancel, userModel);

		if (isSessionQuoteSameAsRequestedQuote(quoteToCancel))
		{
			final Optional<CartModel> optionalCart = Optional.ofNullable(getCartService().getSessionCart());
			if (optionalCart.isPresent())
			{
				quoteToCancel = updateQuoteFromCartInternal(optionalCart.get());
				removeQuoteCart(quoteToCancel);
			}
		}

		quoteToCancel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CANCEL, quoteToCancel, userModel);

		getModelService().save(quoteToCancel);
		getModelService().refresh(quoteToCancel);

		if (quoteToCancel.getC4cQuoteExternalQuoteId() != null && !quoteToCancel.getC4cQuoteExternalQuoteId().isEmpty())
		{
			getEventService().publishEvent(new SapC4CCpiQuoteCancelEvent(quoteToCancel, userModel,
					getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get()));
		}




	}

	@Override
	public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

		QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
				? updateQuoteFromCart(getCartService().getSessionCart(), userModel)
				: quoteModel;

		validateQuoteTotal(updatedQuoteModel);

		getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);

		updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT, updatedQuoteModel,
				userModel);
		updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get();
		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			final SapC4CCpiQuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new SapC4CCpiQuoteBuyerSubmitEvent(updatedQuoteModel,
					userModel, quoteUserType);
			getEventService().publishEvent(quoteBuyerSubmitEvent);
		}
		else if (QuoteUserType.SELLER.equals(quoteUserType))
		{
			final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel, userModel,
					quoteUserType);
			getEventService().publishEvent(quoteSalesRepSubmitEvent);
		}

		return updatedQuoteModel;



	}
}

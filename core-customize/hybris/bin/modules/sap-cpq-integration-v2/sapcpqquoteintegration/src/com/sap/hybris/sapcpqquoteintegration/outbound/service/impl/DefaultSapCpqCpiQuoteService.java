/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.sapcpqquoteintegration.events.SapCpqCpiQuoteBuyerSubmitEvent;
import com.sap.hybris.sapcpqquoteintegration.events.SapCpqCpiQuoteCancelEvent;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;

/**
 * Extending the default DefaultCommerceQuoteService 
 */
public class DefaultSapCpqCpiQuoteService extends DefaultCommerceQuoteService {


	@Override
	public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel) {

		QuoteModel quoteToCancel = quoteModel;
		QuoteUserType quoteUserType = null;

		List<String> externalEntryIds = new ArrayList<>();
		for(AbstractOrderEntryModel quoteEntry: quoteModel.getEntries()) {
			String externalEntryId =((QuoteEntryModel) quoteEntry).getCpqExternalQuoteEntryId();
			externalEntryIds.add(externalEntryId);
		}
		validateParameterNotNullStandardMessage("quoteModel", quoteToCancel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.CANCEL, quoteToCancel, userModel);
		if (isSessionQuoteSameAsRequestedQuote(quoteToCancel)) {
			final Optional<CartModel> optionalCart = Optional.ofNullable(getCartService().getSessionCart());
			if (optionalCart.isPresent()) {
				quoteToCancel = updateQuoteFromCartInternal(optionalCart.get());
				removeQuoteCart(quoteToCancel);
			}
		}
		quoteToCancel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CANCEL, quoteToCancel, userModel);

		getModelService().save(quoteToCancel);
		getModelService().refresh(quoteToCancel);

		final Optional<QuoteUserType> optional = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel);

		if(optional.isPresent()) {

			quoteUserType =  optional.get();
		}

		if (quoteToCancel.getCpqExternalQuoteId() != null &&  !quoteToCancel.getCpqExternalQuoteId().isEmpty() && quoteUserType != null) {
			getEventService().publishEvent(new SapCpqCpiQuoteCancelEvent(quoteToCancel, userModel,
					quoteUserType));
		}

	}

	@Override
	public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel) {

		QuoteUserType quoteUserType = null;
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

		QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
				? updateQuoteFromCart(getCartService().getSessionCart(), userModel) : quoteModel;

				validateQuoteTotal(updatedQuoteModel);

				getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);

				updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT,
						updatedQuoteModel, userModel);
				updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel,
						userModel);
				getModelService().save(updatedQuoteModel);
				getModelService().refresh(updatedQuoteModel);

				final Optional<QuoteUserType> optional = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel);

				if(optional.isPresent()) {

					quoteUserType =  optional.get();
				}

				if (quoteUserType != null && QuoteUserType.BUYER.equals(quoteUserType)) {
					final SapCpqCpiQuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new SapCpqCpiQuoteBuyerSubmitEvent(updatedQuoteModel,
							userModel, quoteUserType);
					getEventService().publishEvent(quoteBuyerSubmitEvent);
				} else if (quoteUserType != null && QuoteUserType.SELLER.equals(quoteUserType)) {
					final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel,
							userModel, quoteUserType);
					getEventService().publishEvent(quoteSalesRepSubmitEvent);
				}

				return updatedQuoteModel;
	}

	@Override
	public Set<QuoteAction> getAllowedActions(final QuoteModel quoteModel, final UserModel userModel)
	{
		Set<QuoteAction> allowedActions = super.getAllowedActions(quoteModel, userModel);
		allowedActions.remove(QuoteAction.DOWNLOAD_QUOTE_PROPOSAL_DOCUMENT);
		if(quoteModel.getCpqQuoteProposalDocument() != null && StringUtils.isNotEmpty(quoteModel.getCpqQuoteProposalDocument())) {
			allowedActions.add(QuoteAction.DOWNLOAD_QUOTE_PROPOSAL_DOCUMENT);
		}
		return allowedActions;
	}
	
	@Override
    public QuoteModel requote(final QuoteModel quote, final UserModel user)
    {
        validateParameterNotNullStandardMessage("quoteModel", quote);
        getQuoteActionValidationStrategy().validate(QuoteAction.REQUOTE, quote, user);
        final QuoteModel quoteModel = super.requote(quote, user);
        quoteModel.setCpqExternalQuoteId(null);
        quoteModel.setTotalDiscounts(null);
        getModelService().save(quoteModel);
        getModelService().refresh(quoteModel);
        return quoteModel;
    }

	@Override
	protected QuoteModel saveUpdate(final CartModel cart, final QuoteModel outdatedQuote, final QuoteModel updatedQuote)
	{

		if(outdatedQuote.getCpqExternalQuoteId()!=null && !outdatedQuote.getCpqExternalQuoteId().isEmpty()) {
			updatedQuote.setCpqExternalQuoteId(outdatedQuote.getCpqExternalQuoteId());
			for(int i= 0 ; i < outdatedQuote.getEntries().size() ; i++) {
				String externalEntryId =  ((QuoteEntryModel) outdatedQuote.getEntries().get(i)).getCpqExternalQuoteEntryId();
				((QuoteEntryModel) updatedQuote.getEntries().get(i)).setCpqExternalQuoteEntryId(externalEntryId);
			}
		}
		try
		{
			final Transaction tx = Transaction.current();
			tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			return (QuoteModel) tx.execute(new TransactionBody()
			{
				@Override
				public QuoteModel execute() throws Exception
				{
					getModelService().remove(outdatedQuote);
					getModelService().saveAll(updatedQuote, cart);
					return updatedQuote;
				}
			});
		}
		catch (final Exception e)
		{
			throw new SystemException(String.format("Updating quote with code [%s] and version [%s] from cart [%s] failed.",
					outdatedQuote.getCode(), outdatedQuote.getVersion(), cart.getCode()), e);
		}
	}

}

package com.sap.hybris.sapcpqsbintegration.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.sap.hybris.sapcpqquoteintegration.outbound.service.impl.DefaultSapCpqCpiQuoteService;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;

public class DefaultSapCpqSbCpiQuoteService extends DefaultSapCpqCpiQuoteService {
	private ModelService modelService;
	private QuoteService quoteService;
	
    @Override
    public QuoteModel createQuoteSnapshotWithState(final QuoteModel quoteModel, final QuoteState quoteState)
    {
   
        final QuoteModel updatedQuote = getQuoteService().createQuoteSnapshot(quoteModel, quoteState);
        
        updatedQuote.getEntries().forEach(entry -> {
        	if(!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails()) && entry.getProduct().getSubscriptionCode() !=null) {
				entry.getCpqSubscriptionDetails().forEach(discountEntry -> {
					discountEntry.setOneTimeChargeEntries(null);
					discountEntry.setRecurringChargeEntries(null);
					discountEntry.setUsageCharges(null);
				});
			}
		});
        getModelService().save(updatedQuote);
        return updatedQuote;
    }
    
    
    @Override
	public QuoteModel requote(final QuoteModel quote, final UserModel user)
	{
		validateParameterNotNullStandardMessage("quoteModel", quote);

		getQuoteActionValidationStrategy().validate(QuoteAction.REQUOTE, quote, user);

		final QuoteModel quoteModel = getRequoteStrategy().requote(quote);
		quoteModel.getEntries().forEach(entry -> {
			if(!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails()) && entry.getProduct().getSubscriptionCode() !=null) {
				entry.getCpqSubscriptionDetails().forEach(discountEntry -> {
					discountEntry.setOneTimeChargeEntries(null);
					discountEntry.setRecurringChargeEntries(null);
					discountEntry.setUsageCharges(null);
				});
			}
		});
		quoteModel.setCpqExternalQuoteId(null);
		quoteModel.setTotalDiscounts(null);
		getModelService().save(quoteModel);
		getModelService().refresh(quoteModel);

		return quoteModel;
	}
    
    public ModelService getModelService() {
		return modelService;
	}
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
	public QuoteService getQuoteService() {
		return quoteService;
	}
	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}
}

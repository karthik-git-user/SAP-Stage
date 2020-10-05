/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteConversionService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Default implementation of SapCpqCpiOutboundQuoteConversionService
 */
public class DefaultSapCpqCpiOutboundQuoteConversionService implements SapCpqCpiOutboundQuoteConversionService {

    protected static final Logger LOG = Logger.getLogger(DefaultSapCpqCpiOutboundQuoteConversionService.class);
    private QuoteService quoteService;
    private B2BUnitService<B2BUnitModel, ?> b2bUnitService;
    private ConfigurationService configurationService; 

    @Override
    public SAPCPQOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel) {
        final SAPCPQOutboundQuoteModel scpiQuoteModel = new SAPCPQOutboundQuoteModel();
        List<CommentModel> quoteComments = quoteModel.getComments();
        
        scpiQuoteModel.setExternalQuoteId(quoteModel.getCode());
        scpiQuoteModel.setName(quoteModel.getName());
        scpiQuoteModel.setDescription(quoteModel.getDescription());
        scpiQuoteModel.setVersion(quoteModel.getVersion().toString());
        scpiQuoteModel.setQuoteId(quoteModel.getCpqExternalQuoteId());
        scpiQuoteModel.setCreationDate(new Date().toString());
        scpiQuoteModel.setDistributionChannel("");   
        scpiQuoteModel.setDivison(quoteModel.getCpqDivision());
        scpiQuoteModel.setMarketID("");
        scpiQuoteModel.setMarketCode(quoteModel.getStore().getSAPConfiguration().getSapcommon_salesOrganization());
        scpiQuoteModel.setPricebookId("");
        scpiQuoteModel.setOrigin(SapcpqquoteintegrationConstants.QUOTE_ORIGIN);
        
        if(quoteModel.getDescription() != null) {
        	scpiQuoteModel.setGlobalComment(quoteModel.getDescription());	
        }else {
        	scpiQuoteModel.setGlobalComment("");
        }
        

        List<SAPCPQOutboundQuoteItemModel> listItem = new ArrayList<>();
        int counter=1;
        for(AbstractOrderEntryModel abstractEntryModel : quoteModel.getEntries()) {
        	
        	
        	if( abstractEntryModel instanceof QuoteEntryModel) {

            	
            	QuoteEntryModel quoteEntryModel = (QuoteEntryModel) abstractEntryModel;
            	SAPCPQOutboundQuoteItemModel model = new SAPCPQOutboundQuoteItemModel();

            	
            	model.setExternalItemID(quoteEntryModel.getPk().toString());
            	model.setProductSystemId(quoteEntryModel.getProduct().getCode());
            	model.setConfigurationId("");
            	model.setPartNumber("");
            	model.setQuantity(quoteEntryModel.getQuantity().toString());
            	model.setItemNumber(counter);

            	model.setSapCPQOutboundQuoteItemComments(populateComments(quoteEntryModel.getComments()));
            	listItem.add(model);          	   	
            
        	}
        	counter++;
        	
        }
        scpiQuoteModel.setSapCPQOutboundQuoteItems(listItem);
        
        /*Filter Comments in case of Edit Quote Scenario
         * Send the comments which are not present in CPQ
         * */
        if(null != quoteModel.getCpqExternalQuoteId() && CollectionUtils.isNotEmpty( quoteModel.getComments())) {
        	quoteComments = new ArrayList<>();
        	for(CommentModel comment : quoteModel.getComments()) {
            	
            	if(null!=comment.getCode() &&  !comment.getCode().startsWith(SapcpqquoteintegrationConstants.COMMENT_PREFIX_CPQ)) {
            		quoteComments.add(comment);
            	}
            }
        }
        
        scpiQuoteModel.setSapCPQOutboundQuoteComments(populateComments(quoteComments));
        
       
        mapCustomer(quoteModel,scpiQuoteModel);
        
        return scpiQuoteModel;
    }

    private List<SAPCPQOutboundQuoteCommentModel> populateComments(List<CommentModel> comments) {
    	
    	List<SAPCPQOutboundQuoteCommentModel> quoteOutboundComments = new ArrayList<>(); 
    	
    	for(CommentModel comment :  comments) {
    		
        	SAPCPQOutboundQuoteCommentModel quoteOutboundComment = new SAPCPQOutboundQuoteCommentModel();
        	
        	if(comment.getAuthor() instanceof B2BCustomerModel) {
    			final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
    			final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
    			quoteOutboundComment.setUserCompany(rootB2BUnit.getDisplayName());
    			quoteOutboundComment.setUserName(comment.getAuthor().getDisplayName());
    		}
        	quoteOutboundComment.setEmail(comment.getAuthor().getUid());
        	quoteOutboundComment.setComment(comment.getText());
        	quoteOutboundComment.setSource(SapcpqquoteintegrationConstants.COMMENT_SOURCE);
        	quoteOutboundComments.add(quoteOutboundComment);
        	  	
        
    	}
    	
    	return quoteOutboundComments;
    	
    }
    
    private void mapCustomer(QuoteModel quoteModel, SAPCPQOutboundQuoteModel scpiQuoteModel) {

    	 //Quote Customer Model
        Set<SAPCPQOutboundQuoteCustomerModel> customers = new HashSet<>();
        CustomerModel customerModel = (B2BCustomerModel) quoteModel.getUser();
        	
        SAPCPQOutboundQuoteCustomerModel customer = new SAPCPQOutboundQuoteCustomerModel();
        	customer.setId(customerModel.getCustomerID());
        	customer.setCustomerCode(customerModel.getDisplayName());
        	
        	/*Bill To Customer*/
        	customer.setRoleType(getConfigurationService().getConfiguration()
                    .getString(SapcpqquoteintegrationConstants.CUSTOMER_ROLE_CODE));
        	
        	customers.add(customer);
        	
            scpiQuoteModel.setSapCPQOutboundQuoteCustomers(customers);
	}

	@Override
    public SAPCPQOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(QuoteModel quote) {

        final SAPCPQOutboundQuoteStatusModel scpiQuoteStatus = new SAPCPQOutboundQuoteStatusModel();

        scpiQuoteStatus.setQuoteId(quote.getCpqExternalQuoteId());
        if(QuoteState.CANCELLED.equals(quote.getState())) {
        	
            scpiQuoteStatus.setStatus(SapcpqquoteintegrationConstants.REJECT);

            scpiQuoteStatus.setCancellationComment(populateCanellationComments(quote));
            
        }else if(QuoteState.BUYER_ORDERED.equals(quote.getState())) {
        	
        	scpiQuoteStatus.setStatus(SapcpqquoteintegrationConstants.SALESORDER);
        	scpiQuoteStatus.setOrderId(quote.getCpqOrderCode());
        }

        return scpiQuoteStatus;
    }
	
	private List<SAPCPQOutboundQuoteCommentModel> populateCanellationComments(QuoteModel quoteModel) {
		
		List<SAPCPQOutboundQuoteCommentModel> list = new ArrayList<>();
		
		for(CommentModel comment : quoteModel.getComments()) {
			
        	if(null == comment.getSource()) {
        		
        		SAPCPQOutboundQuoteCommentModel cancelComment = new SAPCPQOutboundQuoteCommentModel();
        		
        		if(comment.getAuthor() instanceof B2BCustomerModel) {
        			
        			final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
        			final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
        			cancelComment.setUserCompany(rootB2BUnit.getDisplayName());
            		cancelComment.setUserName(comment.getAuthor().getDisplayName());
        		}
        		cancelComment.setEmail(comment.getAuthor().getUid());
        		cancelComment.setQuoteId(quoteModel.getCpqExternalQuoteId());
        		cancelComment.setComment(comment.getText());
        		cancelComment.setSource(SapcpqquoteintegrationConstants.COMMENT_SOURCE);
        		
        		list.add(cancelComment);
        	}
        }
		return list;
	}
    
    protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
    	
		final B2BUnitModel parent = b2bUnitService.getParent(customerModel);
		
		return b2bUnitService.getRootUnit(parent);
	}


    public B2BUnitService<B2BUnitModel, ?> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, ?> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public QuoteService getQuoteService() {
        return quoteService;
    }

    @Required
    public void setQuoteService(final QuoteService quoteService) {
        this.quoteService = quoteService;
    }

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
    
    


}

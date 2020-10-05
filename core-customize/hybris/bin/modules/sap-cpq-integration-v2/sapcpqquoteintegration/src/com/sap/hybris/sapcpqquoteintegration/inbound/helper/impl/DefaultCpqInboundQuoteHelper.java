/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.inbound.helper.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.exception.DefaultSapCpqQuoteIntegrationException;
import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqInboundQuoteHelper;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import com.sap.hybris.sapcpqquoteintegration.service.SapCpqQuoteService;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DiscountValue;

public class DefaultCpqInboundQuoteHelper implements CpqInboundQuoteHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCpqInboundQuoteHelper.class);
	private QuoteService quoteService;
	private UserService userService;
	private KeyGenerator keyGenerator;
	private CommentService commentService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private CatalogService catalogService;
	private ModelService modelService;
	private MediaService mediaService;
	private ProductService productService;
	private SapCpqQuoteService sapCpqQuoteService;

	@Override
	public QuoteModel processInboundQuote(QuoteModel inboundQuote) {

		LOG.info("Enter DefaultCpqInboundQuoteHelper#processInboundQuote");

		if (inboundQuote.getCode().equals(inboundQuote.getCpqExternalQuoteId())) {
			// process new quote
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating new quote");
			inboundQuote.setState(QuoteState.BUYER_OFFER);
			inboundQuote.setDate(new Date());
			String baseStoreUid = sapCpqQuoteService.getSiteAndStoreFromSalesArea(inboundQuote.getCpqSalesOrganization(),
					inboundQuote.getCpqDistributionChannel(), inboundQuote.getCpqDivision());
			inboundQuote.setStore(baseStoreService.getBaseStoreForUid(baseStoreUid));
			inboundQuote.setSite(baseSiteService.getBaseSiteForUID(baseStoreUid));
			inboundQuote.setGuid(UUID.randomUUID().toString());
			inboundQuote.setVersion(1);

		} else {
			// create a Vendor Quote of Existing Quote
			
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating Vendor Quote of Existing quote");
			replicateCurrentQuote(inboundQuote);
		}
		inboundQuote.setExpirationTime(inboundQuote.getQuoteExpirationDate());
		processQuoteEntries(inboundQuote);
		processDiscount(inboundQuote);
		
		processComments(inboundQuote);
		
		return inboundQuote;
	}
	
	/**
	 * @param inboundQuote
	 */
	private void replicateCurrentQuote(QuoteModel inboundQuote) {
		QuoteModel currentQuote = quoteService.getCurrentQuoteForCode(inboundQuote.getCode());
		if (inboundQuote.getCpqExternalQuoteId() == null
				|| inboundQuote.getCpqExternalQuoteId().isEmpty()) {
			inboundQuote.setCpqExternalQuoteId(currentQuote.getCpqExternalQuoteId());
		}
		inboundQuote.setState(QuoteState.BUYER_OFFER);
		inboundQuote.setB2bcomments(currentQuote.getB2bcomments());
		inboundQuote.setCreationtime(currentQuote.getCreationtime());

		inboundQuote.setDate(currentQuote.getDate());
		inboundQuote.setGuid(currentQuote.getGuid());
		inboundQuote.setLocale(currentQuote.getLocale());
		inboundQuote.setName(currentQuote.getName());
		inboundQuote.setOwner(currentQuote.getOwner());
		inboundQuote.setPreviousEstimatedTotal(currentQuote.getTotalPrice());
		inboundQuote.setSite(currentQuote.getSite());
		inboundQuote.setStore(currentQuote.getStore());
		inboundQuote.setUnit(currentQuote.getUnit());
		inboundQuote.setUser(currentQuote.getUser());
		inboundQuote.setVersion(currentQuote.getVersion().intValue() + 1);
		inboundQuote.setWorkflow(currentQuote.getWorkflow());
	}
	
	public void processQuoteEntries(QuoteModel sapQuoteModel) {
		LOG.info("Entering DefaultInboundQuoteHelper#processQuoteEntries");
		List<QuoteEntryModel> quoteEntries = sapQuoteModel.getCpqQuoteEntries();
		boolean productFoundInBaseStore = false;
		Double totalDiscount = 0.0d;
		
		quoteEntries.stream().forEach(entry -> entry.getCpqPricingDetails().forEach(priceItem -> priceItem.setItemId(entry.getCpqItemId())));
		
		for(QuoteEntryModel quoteEntryModel : quoteEntries){

			quoteEntryModel.setOrder(sapQuoteModel);
			quoteEntryModel.setCpqItemId(quoteEntryModel.getCpqItemId());
			quoteEntryModel.setEntryNumber(quoteEntryModel.getEntryNumber());
			quoteEntryModel.setCpqRank(quoteEntryModel.getCpqRank());

			quoteEntryModel.setQuantity(quoteEntryModel.getQuantity());

			BaseStoreModel store = sapQuoteModel.getStore();
			List<CatalogModel> catalogs = store.getCatalogs();
			ProductModel productForCode = null;
			for (CatalogModel catalogModel : catalogs) {
				CatalogVersionModel activeCatalogVersion = catalogModel.getActiveCatalogVersion();
				try {
					productForCode = productService.getProductForCode(activeCatalogVersion, quoteEntryModel.getProductId());
					productFoundInBaseStore = true;
					break;
				} catch (IndexOutOfBoundsException e) {
					LOG.info("Product " + quoteEntryModel.getProductId() + " not found in Catalog " + activeCatalogVersion);
				}
			}
			if (!productFoundInBaseStore) {
				LOG.error("Product " + quoteEntryModel.getProductId() + " not found in Base Store -  " + store
						+ "Replication of Quote will not proceed.");
				throw (new DefaultSapCpqQuoteIntegrationException(
						"Product " + quoteEntryModel.getProductId() + " not found in Base Store -  " + store));
			}else {
				quoteEntryModel.setProduct(productForCode);
				handleQuoteEntryPricing(quoteEntryModel);
				totalDiscount = totalDiscount + quoteEntryModel.getCpqEntryDiscount();
			}
			
			processQuoteEntryComments(quoteEntryModel);
			processEntryDiscounts(quoteEntryModel);
		}
		
		sapQuoteModel.setTotalDiscounts(totalDiscount);
	}
	
	private void handleQuoteEntryPricing(QuoteEntryModel quoteEntryModel) {
	
		if(CollectionUtils.isNotEmpty(quoteEntryModel.getCpqPricingDetails())) {
			Optional<CpqPricingDetailModel> priceDetailOptional = quoteEntryModel.getCpqPricingDetails().stream().filter(priceItem -> priceItem.getPricingType().equals("Fixed")).findFirst();
			
			if(priceDetailOptional.isPresent()) {
				CpqPricingDetailModel priceDetail =  priceDetailOptional.get();
				Double basePrice = Double.valueOf(priceDetail.getListPrice());
				Double entryDiscount = Double.valueOf(priceDetail.getRolledUpDiscountAmount());
				Double totalPrice =  Double.valueOf(priceDetail.getNetPrice());
				quoteEntryModel.setDiscountPercent(priceDetail.getDiscountPercent());
				quoteEntryModel.setBasePrice(basePrice);
				quoteEntryModel.setTotalPrice(totalPrice);
				quoteEntryModel.setEntryNumber(Integer.parseInt(quoteEntryModel.getCpqRank()));
				quoteEntryModel.setCpqEntryDiscount(entryDiscount);
				
			}
			
		}
	}
	
	
	
	/**
	 * @param inboundQuote
	 */
	protected void processDiscount(QuoteModel inboundQuote) {
		
		Double headerDiscount = inboundQuote.getTotalDiscounts();
		inboundQuote.setSubtotal(inboundQuote.getTotalPrice() + inboundQuote.getTotalDiscounts());
		final List<DiscountValue> dvList = new ArrayList<>();
		final DiscountValue dv = new DiscountValue(CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				headerDiscount.doubleValue(), true, headerDiscount.doubleValue(), inboundQuote.getCurrency().getIsocode());
		dvList.add(dv);
		inboundQuote.setGlobalDiscountValues(dvList);
		inboundQuote.setQuoteDiscountValuesInternal("<" + dv.toString() + ">");
	}
	
	/**
	 * @param inboundQuote
	 */
	protected void processComments(QuoteModel inboundQuote) {
		List<CommentModel> comments = inboundQuote.getComments();
		if (comments != null) {
			final String domainCode = "quoteDomain";
			final DomainModel domain = getCommentService().getDomainForCode(domainCode);
			final String componentCode = "quoteComponent";
			final ComponentModel component = getCommentService().getComponentForCode(domain, componentCode);
			final String commentTypeCode = "quoteComment";
			final CommentTypeModel commentType = getCommentService().getCommentTypeForCode(component, commentTypeCode);
			for (CommentModel comment : comments) {
				if (comment.getAuthor() == null) {
					final UserModel author = getUserService().getUserForUID(comment.getCpqCommentAuthorEmail());
					comment.setAuthor(author);
					comment.setComponent(component);
					comment.setCommentType(commentType);
				}
			}
			inboundQuote.setComments(comments);
		}
	}
	
	private void processEntryDiscounts(QuoteEntryModel inboundQuoteEntry) {
		if (inboundQuoteEntry.getCpqEntryDiscount() != null && inboundQuoteEntry.getCpqEntryDiscount() > 0.0d) {
			Double discountPerItem = inboundQuoteEntry.getCpqEntryDiscount() / inboundQuoteEntry.getQuantity();
			boolean discountType = isAbsouluteDiscount(inboundQuoteEntry);
			List<DiscountValue> dvList = new ArrayList<DiscountValue>();
			// Format of dv = [<DV<QuoteDiscount#20.0#false#28.8#USD#false>VD>]
			
			if(!discountType) {
				
				discountPerItem = (discountPerItem/inboundQuoteEntry.getBasePrice())*100 ;
			}
			DiscountValue dv = new DiscountValue(SapcpqquoteintegrationConstants.QUOTE_ENTRY_DISCOUNT_CODE,
					discountPerItem, discountType, discountPerItem, inboundQuoteEntry.getOrder().getCurrency().getIsocode());
			dvList.add(dv);
			inboundQuoteEntry.setDiscountValues(dvList);
			inboundQuoteEntry.setCpqEntryDiscountInternal("<" + dv.toString() + ">");
		}
	}
	
	
private boolean isAbsouluteDiscount(QuoteEntryModel inboundQuoteEntry) {
		
		boolean isAbsolute = false;
		
		
		if(null!= inboundQuoteEntry.getOrder().getStore() && null!= inboundQuoteEntry.getOrder().getStore().getSAPConfiguration() && null != inboundQuoteEntry.getOrder().getStore().getSAPConfiguration().getCpqAbsoluteDiscountEnabled()) {
			
			isAbsolute = inboundQuoteEntry.getOrder().getStore().getSAPConfiguration().getCpqAbsoluteDiscountEnabled().booleanValue();
			
		}
		
		return isAbsolute;
		
	}
	
	/**
	 * @param inboundQuoteEntry
	 */
	protected void processQuoteEntryComments(QuoteEntryModel inboundQuoteEntry) {
		List<CommentModel> comments = inboundQuoteEntry.getComments();
		if (comments != null) {
			final String domainCode = "quoteDomain";
			final DomainModel domain = getCommentService().getDomainForCode(domainCode);
			final String componentCode = "quoteComponent";
			final ComponentModel component = getCommentService().getComponentForCode(domain, componentCode);
			final String commentTypeCode = "quoteEntryComment";
			final CommentTypeModel commentType = getCommentService().getCommentTypeForCode(component, commentTypeCode);
			for (CommentModel comment : comments) {
				final UserModel author = getUserService().getUserForUID(comment.getCpqCommentAuthorEmail());
				comment.setAuthor(author);
				comment.setComponent(component);
				comment.setCommentType(commentType);
				comment.setCode(UUID.randomUUID().toString());
				
			}
			inboundQuoteEntry.setComments(comments);
		}
	}

	
	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(KeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public SapCpqQuoteService getSapCpqQuoteService() {
		return sapCpqQuoteService;
	}

	public void setSapCpqQuoteService(SapCpqQuoteService sapCpqQuoteService) {
		this.sapCpqQuoteService = sapCpqQuoteService;
	}

	
	
}

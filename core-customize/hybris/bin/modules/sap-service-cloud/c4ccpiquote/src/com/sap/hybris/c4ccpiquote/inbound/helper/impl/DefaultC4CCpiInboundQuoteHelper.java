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
package com.sap.hybris.c4ccpiquote.inbound.helper.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants;
import com.sap.hybris.c4ccpiquote.inbound.helper.C4CCpiInboundQuoteHelper;
import com.sap.hybris.c4ccpiquote.model.SAPC4CPriceComponentModel;
import com.sap.hybris.c4ccpiquote.service.impl.DefaultC4CCpiQuoteService;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;
import de.hybris.platform.commerceservices.util.CommerceCommentUtils;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.impl.DefaultDiscountService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;


/**
 *
 */
public class DefaultC4CCpiInboundQuoteHelper implements C4CCpiInboundQuoteHelper
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultC4CCpiInboundQuoteHelper.class);
	public static final String KEYGEN_ORDER_CODE_DIGITS = "keygen.order.code.digits";
	private QuoteService quoteService;
	private UserService userService;
	private CommentService commentService;
	private ModelService modelService;
	private ProductService productService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private DefaultC4CCpiQuoteService sapSalesQuoteService;
	private DefaultDiscountService discountService;


	@Override
	public QuoteModel processSalesInboundQuote(final QuoteModel inboundQuote)
	{

		LOG.info("DefaultC4CCpiInboundQuoteHelper#processSalesInboundQuote Creating Vendor Quote of Existing quote");
		inboundQuote.setState(QuoteState.BUYER_OFFER);
		replicateCurrentQuote(inboundQuote);
		inboundQuote.setExpirationTime(inboundQuote.getQuoteExpirationDate());
		processQuoteEntries(inboundQuote);
		processDiscount(inboundQuote);
		processTax(inboundQuote);
		return inboundQuote;
	}


	/**
	 *
	 */
	private void processTax(final QuoteModel inboundQuote)
	{
		final String taxCode = "TAX" + inboundQuote.getCode();
		final Double taxValue = inboundQuote.getTotalTax();
		final TaxValue tax = new TaxValue(taxCode, taxValue, true, inboundQuote.getCurrency().toString());
		final List<TaxValue> taxValues = new ArrayList<>();
		taxValues.add(tax);

		inboundQuote.setTotalTax(taxValue);
		inboundQuote.setTotalTaxValues(taxValues);

	}



	/**
	 *
	 */
	private void processDiscount(final QuoteModel inboundQuote)
	{


		final String priceTypeCode = inboundQuote.getStore().getSAPConfiguration().getSaporderexchange_itemPriceConditionType();

		if (CollectionUtils.isNotEmpty(inboundQuote.getHeaderPriceComponentList()) && StringUtils.isNotEmpty(priceTypeCode))
		{

			final Optional<SAPC4CPriceComponentModel> optional = inboundQuote.getHeaderPriceComponentList().stream()
					.filter(discount -> discount.getCode().equals(priceTypeCode)).findFirst();

			if (optional.isPresent())
			{
				final Double basePrice = Double.valueOf(optional.get().getValue());
				inboundQuote.setSubtotal(basePrice);

				final Double headerDiscount = basePrice - inboundQuote.getTotalPrice();
				if (headerDiscount.doubleValue() > 0.0d)
				{
					final List<DiscountValue> dvList = new ArrayList<DiscountValue>();
					final DiscountValue dv = new DiscountValue(CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
							headerDiscount.doubleValue(), true, headerDiscount.doubleValue(), inboundQuote.getCurrency().getIsocode());
					dvList.add(dv);
					inboundQuote.setGlobalDiscountValues(dvList);
				}
			}
			/* Saving Other Header Discounts Logic goes here */
			final List<SAPC4CPriceComponentModel> priceComponents =
			inboundQuote.getHeaderPriceComponentList().stream()
					.filter(discount -> discount.getOriginCode().equals(Config.getString(C4ccpiquoteConstants.SAP_C4C_PRICECOMPONENT_ORIGIN_CODE, StringUtils.EMPTY)))
					.collect(Collectors.toList());

			for (final SAPC4CPriceComponentModel priceItemModel : priceComponents)
			{
				
				DiscountValue dv = fetchDiscountValue(inboundQuote, priceItemModel);
				if(null != dv) {
					inboundQuote.getGlobalDiscountValues().add(dv);	
				}

			}
		}
	}


	private void processQuoteEntries(final QuoteModel inboundQuote)
	{

		final String priceTypeCode = inboundQuote.getStore().getSAPConfiguration().getSaporderexchange_itemPriceConditionType();
		if(inboundQuote.getQuoteEntries() != null) {
			inboundQuote.getQuoteEntries().forEach(item -> {
				item.setOrder(inboundQuote);
				item.setProduct(productService.getProductForCode(item.getC4cItemId()));
				if (CollectionUtils.isNotEmpty(item.getItemPriceComponentList()) && StringUtils.isNotEmpty(priceTypeCode))
				{
					final Optional<SAPC4CPriceComponentModel> optional = item.getItemPriceComponentList().stream()
							.filter(discount -> discount.getCode().equals(priceTypeCode)).findFirst();
					if (optional.isPresent())
					{
						final Double totalPrice = Double.valueOf(optional.get().getValue());
						final Double basePrice = totalPrice / item.getQuantity();
						item.setBasePrice(basePrice);
						item.setTotalPrice(totalPrice);
					}
				}
				final List<DiscountValue> dvList = new ArrayList<DiscountValue>();
				item.setDiscountValues(dvList);

				final List<SAPC4CPriceComponentModel> priceComponents = item.getItemPriceComponentList().stream()
						.filter(discount -> discount.getOriginCode().equals(Config.getString(C4ccpiquoteConstants.SAP_C4C_PRICECOMPONENT_ORIGIN_CODE, StringUtils.EMPTY)))
						.collect(Collectors.toList());
				for (final SAPC4CPriceComponentModel priceItemModel : priceComponents)
				{
					DiscountValue dv = fetchDiscountValue(inboundQuote, priceItemModel);
					if(null != dv) {
						item.getDiscountValues().add(dv);	
					}
				}
			});
		}
	}

	private DiscountValue fetchDiscountValue(QuoteModel inboundQuote , SAPC4CPriceComponentModel priceItemModel) {
		
		final String code = priceItemModel.getCode();
		DiscountValue dv = null;
		try
		{
			final DiscountModel model = discountService.getDiscountForCode(code);
			dv = new DiscountValue(model.getCode(), Double.valueOf(priceItemModel.getValue()), true,
					Double.valueOf(priceItemModel.getValue()), inboundQuote.getCurrency().getIsocode());
		}
		catch (final UnknownIdentifierException | AmbiguousIdentifierException exp)
		{
				LOG.info("Error while fetching the discounts", exp);
		}
		return dv;
	}
	
	/**
	 *
	 */
	private void replicateCurrentQuote(final QuoteModel inboundQuote)
	{
		final QuoteModel currentQuote = quoteService.getCurrentQuoteForCode(inboundQuote.getCode());

		inboundQuote.setCurrency(currentQuote.getCurrency());
		inboundQuote.setAssignee(currentQuote.getAssignee());
		inboundQuote.setB2bcomments(currentQuote.getB2bcomments());
		inboundQuote.setCreationtime(currentQuote.getCreationtime());
		inboundQuote.setDate(currentQuote.getDate());
		inboundQuote.setDescription(currentQuote.getDescription());
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
		
		processComments(inboundQuote,currentQuote);
	}

	/**
	 * @param currentQuote 
	 *
	 */
	private void processComments(final QuoteModel inboundQuote, QuoteModel currentQuote)
	{

		final List<CommentModel> comments = inboundQuote.getComments();
		if (comments != null)
		{
			for (final CommentModel comment : comments)
			{
				if (comment.getAuthor()!= null)
				{
					CommerceCommentParameter parameter = CommerceCommentUtils.buildQuoteCommentParameter(inboundQuote, comment.getAuthor(), comment.getText());
					validateCommentParameter(parameter);
					final DomainModel domainModel = getCommentService().getDomainForCode(parameter.getDomainCode());
					final ComponentModel componentModel = getCommentService().getComponentForCode(domainModel, parameter.getComponentCode());
					final CommentTypeModel commentTypeModel = getCommentService().getCommentTypeForCode(componentModel, parameter.getCommentTypeCode());
					comment.setText(parameter.getText());
					comment.setAuthor(parameter.getAuthor());
					retainCommerceComment(currentQuote ,comment);
					comment.setComponent(componentModel);
					comment.setCommentType(commentTypeModel);
					comment.setCode(UUID.randomUUID().toString());
				}
			}
			
		}

	}
	
	private void retainCommerceComment(QuoteModel currentQuote,CommentModel comment ) {
		
		if(comment.getCode().equals(Config.getString(C4ccpiquoteConstants.COMMEMT_TEXT_TYPE_CODE, StringUtils.EMPTY))) {
			Optional<CommentModel> oldComment = currentQuote.getComments().stream().filter(originalComment -> originalComment.getText().equals(comment.getText())).findFirst();
			if(oldComment.isPresent()) {
				comment.setCreationtime(oldComment.get().getCreationtime());	
				comment.setAuthor(oldComment.get().getAuthor());
			}
			
		}
	}
	
	protected void validateCommentParameter(final CommerceCommentParameter parameter) {
		ServicesUtil.validateParameterNotNullStandardMessage("parameter", parameter);
		ServicesUtil.validateParameterNotNullStandardMessage("author", parameter.getAuthor());
		checkArgument(isNotEmpty(parameter.getText()), "Text cannot not be empty");
		checkArgument(isNotEmpty(parameter.getDomainCode()), "Domain cannot not be empty");
		checkArgument(isNotEmpty(parameter.getComponentCode()), "Component cannot not be empty");
		checkArgument(isNotEmpty(parameter.getCommentTypeCode()), "CommentType cannot not be empty");
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the commentService
	 */
	public CommentService getCommentService()
	{
		return commentService;
	}

	/**
	 * @param commentService
	 *           the commentService to set
	 */
	public void setCommentService(final CommentService commentService)
	{
		this.commentService = commentService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	/**
	 * @param quoteService
	 *           the quoteService to set
	 */
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the sapSalesQuoteService
	 */
	public DefaultC4CCpiQuoteService getSapSalesQuoteService()
	{
		return sapSalesQuoteService;
	}

	/**
	 * @param sapSalesQuoteService
	 *           the sapSalesQuoteService to set
	 */
	public void setSapSalesQuoteService(final DefaultC4CCpiQuoteService sapSalesQuoteService)
	{
		this.sapSalesQuoteService = sapSalesQuoteService;
	}


	/**
	 * @return the discountService
	 */
	public DefaultDiscountService getDiscountService()
	{
		return discountService;
	}


	/**
	 * @param discountService
	 *           the discountService to set
	 */
	public void setDiscountService(final DefaultDiscountService discountService)
	{
		this.discountService = discountService;
	}


}

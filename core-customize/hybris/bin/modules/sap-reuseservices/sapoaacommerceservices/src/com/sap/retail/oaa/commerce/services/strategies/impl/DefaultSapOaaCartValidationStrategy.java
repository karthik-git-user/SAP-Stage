/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.strategies.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.order.impl.SapOaaCommerceCartModificationStatus;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


/**
 * Default Cart validation Strategy for Omni channel Commerce.
 */
public class DefaultSapOaaCartValidationStrategy extends DefaultCartValidationStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSapOaaCartValidationStrategy.class);

	private CommerceStockService oaaStockService;
	private ATPAggregationStrategy atpAggregationStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy#validateCart(de.hybris.platform
	 * .commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public List<CommerceCartModification> validateCart(final CommerceCartParameter parameter)
	{
		List<CommerceCartModification> cartModificationList = new ArrayList<>();

		final CartModel cartModel = parameter.getCart();
		cleanCart(cartModel);

		if (cartModel != null && cartModel.getEntries() != null && !cartModel.getEntries().isEmpty())
		{
			cartModificationList = checkAvailabilityOfCartItems(cartModel);
		}
		return cartModificationList;
	}

	/**
	 * Validates the cart entry with the ATP results and make quantity adjustments if necessary.
	 *
	 * @param cartModel
	 * @param cartItem
	 * @param aggregatedATPResultQty
	 * @return CommerceCartModification
	 */
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final AbstractOrderEntryModel cartItem,
			final long aggregatedATPResultQty)
	{
		final CartEntryModel cartEntryModel = (CartEntryModel) cartItem;
		final CommerceCartModification modification = new CommerceCartModification();

		// First verify that the product exists
		try
		{
			getProductService().getProductForCode(cartEntryModel.getProduct().getCode());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.error("Product could not be found", e);
			modification.setStatusCode(CommerceCartModificationStatus.UNAVAILABLE);
			modification.setQuantityAdded(0);
			modification.setQuantity(0);

			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());

			modification.setEntry(entry);

			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);

			return modification;
		}

		// Overall availability of this product
		final long stockLevel = aggregatedATPResultQty;

		// Overall stock quantity in the cart
		final long cartLevel = getCartLevel(cartEntryModel, cartModel);

		// Stock quantity for this cartEntry
		final long cartEntryLevel = cartEntryModel.getQuantity().longValue();

		// New stock quantity for this cartEntry
		final long newOrderEntryLevel;

		if (stockLevel == 0)
		{
			// if stock is not available.. play save.. only allow quantity that was already in cart.
			newOrderEntryLevel = Math.min(cartEntryLevel, cartLevel);
		}
		else
		{
			newOrderEntryLevel = Math.min(cartEntryLevel, stockLevel);
		}

		if (stockLevel <= 0 || newOrderEntryLevel < 0)
		{
			// If no stock is available or the cart is full for this product, remove the entry from the cart
			modification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
			modification.setQuantityAdded(0);//New quantity for this entry
			modification.setQuantity(cartEntryLevel);//Old quantity for this entry
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());
			modification.setEntry(entry);
			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);

			return modification;
		}
		else if (cartEntryLevel != newOrderEntryLevel)
		{
			// If the orderLevel has changed for this cartEntry, then recalculate the quantity
			modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			modification.setQuantityAdded(newOrderEntryLevel);
			modification.setQuantity(cartEntryLevel);
			modification.setEntry(cartEntryModel);
			cartEntryModel.setQuantity(Long.valueOf(newOrderEntryLevel));
			getModelService().save(cartEntryModel);
			getModelService().refresh(cartModel);

			return modification;
		}
		else
		{
			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			modification.setQuantityAdded(cartEntryLevel);
			modification.setQuantity(cartEntryLevel);
			modification.setEntry(cartEntryModel);
			return modification;
		}
	}

	/**
	 * Checks the availability of all items in the specified cart.
	 *
	 * @param cart
	 * @return list of commerceCartModifications
	 */
	protected List<CommerceCartModification> checkAvailabilityOfCartItems(final CartModel cart)
	{
		final List<CommerceCartModification> cartModificationList = new ArrayList<>();
		LOG.debug("Checking Availability for all Cart Items...");

		// Check and validate determined availability for each cart item...
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			List<ATPAvailability> atpAvailabilityList = new ArrayList<>();

			try
			{
				if (entry.getDeliveryPointOfService() != null)
				{
					final String source = entry.getDeliveryPointOfService().getName();

					atpAvailabilityList = ((SapOaaCommerceStockService)oaaStockService).getAvailabilityForProductAndSource(cart.getGuid(),
							entry.getEntryNumber().toString(), entry.getProduct(), source);
				}
				else
				{
					atpAvailabilityList = ((SapOaaCommerceStockService)oaaStockService).getAvailabilityForProduct(cart.getGuid(), entry.getEntryNumber().toString(),
							entry.getProduct());
				}
			}
			catch (final ATPException e)
			{
				LOG.error("Failed to check availability for product: " + entry.getProduct().getCode(), e);

				// in case the ATP call fails for the product set OAA specific modification status
				final CommerceCartModification checkingATPFailedModification = new CommerceCartModification();
				checkingATPFailedModification.setEntry(entry);
				checkingATPFailedModification.setProduct(entry.getProduct());
				checkingATPFailedModification.setQuantity(entry.getQuantity().longValue());
				checkingATPFailedModification.setQuantityAdded(entry.getQuantity().longValue());
				checkingATPFailedModification.setStatusCode(SapOaaCommerceCartModificationStatus.ATP_VALIDATION_FAILURE);
				cartModificationList.add(checkingATPFailedModification);
				return cartModificationList;
			}
			catch (final BackendDownException e)
			{
				LOG.error(e.getMessage(), e);
				// return modification list when ATP call fails for first cart entry due to back end not responding
				return createModificationListForBackendDown(cart);
			}

			final Long aggregatedATPResult = getAtpAggregationStrategy().aggregateAvailability(cart.getGuid(), entry.getProduct(),
					entry.getDeliveryPointOfService(), atpAvailabilityList);
			cartModificationList.add(validateCartEntry(cart, entry, aggregatedATPResult.longValue()));
		}
		return cartModificationList;
	}

	/**
	 * Create modification list for back end down.
	 *
	 * @param cart
	 * @return cartModificationList
	 */
	protected List<CommerceCartModification> createModificationListForBackendDown(final CartModel cart)
	{
		final List<CommerceCartModification> cartModificationList = new ArrayList<>();

		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			//When Pick up in Store -> remove Item
			if (entry.getDeliveryPointOfService() != null)
			{
				cartModificationList.add(validateCartEntry(cart, entry, 0));
			}
			//When Delivery -> return requested quantity
			else
			{
				cartModificationList.add(validateCartEntry(cart, entry, entry.getQuantity().longValue()));
			}
		}
		return cartModificationList;
	}

	/**
	 * @param oaaStockService
	 *           the oaaStockService to set
	 */
	public void setOaaStockService(final CommerceStockService oaaStockService)
	{
		this.oaaStockService = oaaStockService;
	}

	/**
	 * @return the oaaStockService
	 */
	protected CommerceStockService getOaaStockService()
	{
		return oaaStockService;
	}

	/**
	 * @param atpAggregationStrategy
	 *           the atpStrategy to set
	 */
	public void setAtpAggregationStrategy(final ATPAggregationStrategy atpAggregationStrategy)
	{
		this.atpAggregationStrategy = atpAggregationStrategy;
	}

	/**
	 * @return the atpAggregationStrategy
	 */
	protected ATPAggregationStrategy getAtpAggregationStrategy()
	{
		return atpAggregationStrategy;
	}

}

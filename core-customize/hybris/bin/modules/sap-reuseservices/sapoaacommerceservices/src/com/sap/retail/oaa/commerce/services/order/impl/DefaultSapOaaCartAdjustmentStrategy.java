/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.order.impl;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.order.SapOaaCartAdjustmentStrategy;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;


/**
 * Default implementation of the Cart Adjustment Strategy for OAA
 */
public class DefaultSapOaaCartAdjustmentStrategy implements SapOaaCartAdjustmentStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSapOaaCartAdjustmentStrategy.class);

	private CommerceStockService oaaStockService;

	public CommerceStockService getOaaStockService() {
		return oaaStockService;
	}

	public void setOaaStockService(CommerceStockService oaaStockService) {
		this.oaaStockService = oaaStockService;
	}

	@Override
	public Long determineAllowedCartAdjustmentForProduct(@Nonnull final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final long cartItemQty, final PointOfServiceModel pointOfServiceModel)
	{
		// How many will we have in our cart if we add quantity
		final long newTotalQuantity = cartItemQty + quantityToAdd;

		// In case the current cart item will be removed, we can skip the AvailableStockLevel check
		if (newTotalQuantity == 0)
		{
			return Long.valueOf(-cartItemQty);
		}

		// Get current stock information calling ATP Service (CAR)
		long stockLevel = 0;


		final String itemId = determineItemId(cartModel, productModel, pointOfServiceModel);

		try
		{
			stockLevel = ((SapOaaCommerceStockService)oaaStockService).getAvailableStockLevel(cartModel.getGuid(), itemId, productModel, pointOfServiceModel)
					.longValue();
		}
		catch (final ATPException e)
		{
			LOG.error(e.getMessage(), e);
			stockLevel = 0;
		}
		catch (final BackendDownException e)
		{

			LOG.error(e.getMessage(), e);

			//When Pick up in Store -> remove Item
			if (pointOfServiceModel != null)
			{
				stockLevel = 0;
			}
			//When Delivery -> return requested quantity
			else
			{
				stockLevel = newTotalQuantity;
			}

		}

		// Now limit that to the total available in stock
		final long newTotalQuantityAfterStockLimit = Math.min(newTotalQuantity, stockLevel);

		// So now work out what the maximum allowed to be added is (note that
		// this may be negative!)
		final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();

		if (maxOrderQuantity != null)
		{
			final long newTotalQuantityAfterProductMaxOrder = Math.min(newTotalQuantityAfterStockLimit,
					maxOrderQuantity.longValue());
			return Long.valueOf(newTotalQuantityAfterProductMaxOrder - cartItemQty);
		}
		return Long.valueOf(newTotalQuantityAfterStockLimit - cartItemQty);
	}

	/**
	 * Returns the Item id of the entry, based that only one item combination of Product and POS is in the cart
	 *
	 * @param cartModel
	 * @param productModel
	 * @param pointOfServiceModel
	 * @return String Item ID
	 */
	private String determineItemId(final CartModel cartModel, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel)
	{
		if (cartModel == null || cartModel.getEntries() == null)
		{
			return null;
		}

		// click and ship case
		if (pointOfServiceModel == null)
		{
			return determineItemIdClickAndShip(cartModel, productModel);
		}
		else
		// click and collect case
		{
			return determineItemIdClickAndCollect(cartModel, productModel, pointOfServiceModel);
		}
	}

	private String determineItemIdClickAndShip(final CartModel cartModel, final ProductModel productModel)
	{
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getProduct().getCode().equals(productModel.getCode()) && entry.getDeliveryPointOfService() == null)
			{
				return entry.getEntryNumber().toString();
			}
		}
		return null;
	}

	private String determineItemIdClickAndCollect(final CartModel cartModel, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel)
	{
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getProduct().getCode().equals(productModel.getCode()) && entry.getDeliveryPointOfService() != null
					&& entry.getDeliveryPointOfService().equals(pointOfServiceModel))
			{
				return entry.getEntryNumber().toString();
			}
		}
		return null;
	}

	

}

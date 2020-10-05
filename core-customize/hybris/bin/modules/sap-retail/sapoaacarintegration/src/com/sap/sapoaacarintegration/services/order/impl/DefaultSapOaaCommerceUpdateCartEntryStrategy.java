/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.order.impl;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.order.SapOaaCartAdjustmentStrategy;
import com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

/**
 * Update Cart Entry Strategy for Omni Channel Availability. Check Allowed quantity on cart update
 */
public class DefaultSapOaaCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultSapOaaCommerceUpdateCartEntryStrategy.class);

	private SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategy;
	private ReservationStrategy reservationStrategy;

	@Override
	protected long getAllowedCartAdjustmentForProduct(final CartModel cartModel, final ProductModel productModel,
			final long quantityToAdd, final PointOfServiceModel pointOfServiceModel)
	{
		LOG.debug(
				"Get allowed Cart Adjustment for Product: " + productModel.getCode() + " according SapOaaCartAdjustmenStrategy...");

		final long cartLevel = super.checkCartLevel(productModel, cartModel, pointOfServiceModel);

		return oaaCartAdjustmentStrategy
				.determineAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd, cartLevel, pointOfServiceModel)
				.longValue();
	}

	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		//When Entry will be removed delete reservation
		if ((entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange) <= 0)
		{
			this.getReservationStrategy().deleteReservationItem(cartModel, entryToUpdate);
		}

		return super.modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
	}

	/**
	 * @param oaaCartAdjustmentStrategy
	 *           the oaaCartAdjustmentStrategy to set
	 */
	public void setOaaCartAdjustmentStrategy(final SapOaaCartAdjustmentStrategy oaaCartAdjustmentStrategy)
	{
		this.oaaCartAdjustmentStrategy = oaaCartAdjustmentStrategy;
	}

	/**
	 * @return the oaaCartAdjustmentStrategy
	 */
	protected SapOaaCartAdjustmentStrategy getOaaCartAdjustmentStrategy()
	{
		return oaaCartAdjustmentStrategy;
	}

	/**
	 * @return the reservationStrategy
	 */
	public ReservationStrategy getReservationStrategy()
	{
		return reservationStrategy;
	}

	/**
	 * @param reservationStrategy
	 *           the reservationStrategy to set
	 */
	public void setReservationStrategy(final ReservationStrategy reservationStrategy)
	{
		this.reservationStrategy = reservationStrategy;
	}
}

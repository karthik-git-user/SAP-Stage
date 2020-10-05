/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.strategy;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;



/**
 * Strategy for Omni Channel Availability Sourcing
 */
public interface ATPAggregationStrategy
{
	/**
	 * Aggregate the Schedule Lines Quantity returned by the ATP Service
	 *
	 * @param cartGuid
	 * @param productModel
	 * @param pointOfServiceModel
	 */
	public Long aggregateAvailability(final String cartGuid, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel, final List<ATPAvailability> availabilities);

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;


import java.util.List;

import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;


/**
 * Immutable Object
 * 
 * 
 */
public class SapProductAvailabilityImpl implements SapProductAvailability
{

	private final Long currentStockLevel;

	private final List<FutureStockModel>  futureAvailability;
	private final StockLevelModel stockLevelModel;

	/**
	 * @param currentStockLevel
	 * @param futureAvailability
	 */
	public SapProductAvailabilityImpl(final Long currentStockLevel, final List<FutureStockModel>  futureAvailability,StockLevelModel stockLevelModel)
	{
		this.currentStockLevel = currentStockLevel;
		this.futureAvailability = futureAvailability;
		this.stockLevelModel  = stockLevelModel;
	}

	@Override
	public Long getCurrentStockLevel()
	{
		return this.currentStockLevel;
	}


	public List<FutureStockModel> getFutureAvailability() {
		return futureAvailability;
	}

	public StockLevelModel getStockLevelModel() {
		return stockLevelModel;
	}


}

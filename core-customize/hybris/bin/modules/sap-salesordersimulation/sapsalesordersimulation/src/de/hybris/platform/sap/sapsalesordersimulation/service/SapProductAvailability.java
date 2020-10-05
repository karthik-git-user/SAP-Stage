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
package de.hybris.platform.sap.sapsalesordersimulation.service;

import java.util.List;

import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;


public interface SapProductAvailability
{

	Long getCurrentStockLevel();
	List<FutureStockModel> getFutureAvailability();
	StockLevelModel getStockLevelModel();
}

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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.acceleratorservices.futurestock.impl.DefaultFutureStockService;
import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapsalesordersimulation.service.AvailabilityService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;




/**
 * retrieve the future availability
 *
 */
public class DefaultSapFutureStockService extends DefaultFutureStockService
{
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private AvailabilityService availabilityService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.futurestock.FutureStockService#getFutureAvailability(java.util.List)
	 */

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		
		
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			
			final SapProductAvailability productAvailability =
					getAvailabilityService().getProductAvailability(products.get(0), null);
					
			final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();
			final List<FutureStockModel> futureStocks = productAvailability.getFutureAvailability();
			if (!CollectionUtils.isEmpty(futureStocks))
			{
				final HashMap<Date, Integer> futureAvailability = new HashMap<>();
				for (final FutureStockModel futureStock : futureStocks)
				{
					futureAvailability.put(futureStock.getDate(), futureStock.getQuantity());
				}
				productsMap.put(products.get(0).getCode(), futureAvailability);
			}
			return productsMap;
		} else {
			return super.getFutureAvailability(products);
		}
		
		
	}

	

	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}



	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}



	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}





}

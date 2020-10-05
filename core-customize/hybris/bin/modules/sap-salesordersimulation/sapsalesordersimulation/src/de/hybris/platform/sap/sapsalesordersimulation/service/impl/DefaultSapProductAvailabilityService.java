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

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapsalesordersimulation.service.AvailabilityService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;

/**
 * determined the product availability
 *
 */
public class DefaultSapProductAvailabilityService extends DefaultCommerceStockService {
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private ModuleConfigurationAccess moduleConfigurationAccess;
	private SalesOrderSimulationService salesOrderSimulationService;
	private AvailabilityService availabilityService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService#
	 * getStockLevelForProductAndBaseStore
	 * (de.hybris.platform.core.model.product.ProductModel,
	 * de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore) {
		ServicesUtil.validateParameterNotNull(product, "product cannot be null");
		ServicesUtil.validateParameterNotNull(baseStore, "baseStore cannot be null");

		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			// not available stock return 0
			final SapProductAvailability productAvailability =  getAvailabilityService().getProductAvailability(product, baseStore);

			if (productAvailability == null) {
				return Long.valueOf(0); // no stock available
			}
			return productAvailability.getCurrentStockLevel();
		} else {
			return super.getStockLevelForProductAndBaseStore(product,baseStore);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService#
	 * getStockLevelStatusForProductAndBaseStore
	 * (de.hybris.platform.core.model.product.ProductModel,
	 * de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product,
			final BaseStoreModel baseStore) {
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			final Long stockLevel = getStockLevelForProductAndBaseStore(product, baseStore);

			if (stockLevel.compareTo(Long.valueOf(0)) > 0) {
				return StockLevelStatus.INSTOCK;
			} else {
				return StockLevelStatus.OUTOFSTOCK;
			}
		} else {
			return super.getStockLevelStatusForProductAndBaseStore(product, baseStore);
		}
	}


	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}


	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}

	public SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}

	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}
	

}

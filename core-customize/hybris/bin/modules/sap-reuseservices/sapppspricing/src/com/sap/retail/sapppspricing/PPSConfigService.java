/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;

import com.sap.retail.sapppspricing.enums.InterfaceVersion;


/**
 * Access to the configuration relevant for price calculation via OPPS
 */
public interface PPSConfigService
{
	/**
	 * Determines if price calculation via PPS functionality is active 
	 * 
	 * @param prod
	 *           Product
	 * @return true 
	 * 			 if price calculation via PPS is active for given product
	 */
	boolean isPpsActive(ProductModel prod);

	/**
	 * 
	 * Determines if price calculation via PPS functionality is active
	 *
	 * @param order
	 *           cart / order
	 * @return true 
	 * 			 if price calculation via PPS is active for given order
	 */
	boolean isPpsActive(AbstractOrderModel order);

	/**
	 * Find the business unit ID for which the price calculation shall take place for given product
	 * 
	 * @param prod
	 *           Product
	 * @return the business unit ID for which the price calculation shall take place for given product
	 */
	String getBusinessUnitId(ProductModel prod);

	/**
	 * Find the business unit ID for which the price calculation shall take place for given order
	 * 
	 * @param order
	 * 			order
	 * @return the business unit ID for which the price calculation shall take place for given order
	 */
	String getBusinessUnitId(AbstractOrderModel order);

	/**
	 * Find SAPConfiguration Object from given product
	 * 
	 * @param prod
	 *           Product
	 * @return the relevant SAP base store configuration for this product
	 */
	SAPConfigurationModel getSapConfig(ProductModel prod);
	
	/**
	 * Find client interface version for the given order
	 * 
	 * @param order
	 * 			order
	 * @return the client interface version for the given order
	 */
	InterfaceVersion getClientInterfaceVersion(AbstractOrderModel order);


	/**
	 * Find client interface version for the given product
	 * 
	 * @param prod
	 * 			product
	 * @return the client interface version for the given product
	 */
	InterfaceVersion getClientInterfaceVersion(ProductModel prod);
	
	/**
	 * Find the source system ID for given order
	 * 
	 * @param order
	 * 			order
	 * @return the master system source id for the given order
	 */
	String getSourceSystemId(AbstractOrderModel order);
	
	/**
	 * Find the source system ID for given product
	 * 
	 * @param prod 
	 * 			product id
	 * 
	 * @return the master system source id for this product
	 */
	String getSourceSystemId(ProductModel prod);
	
}

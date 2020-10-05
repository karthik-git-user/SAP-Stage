/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.facade;


import java.util.List;


import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;


/**
 * Facade to manage entitlements in SAP EMS.
 */
public interface SapEntitlementFacade
{
	/**
	 * Get all Entitlements for current customer
	 *
	 * @param pageNumber
	 *           Page number for pagination
	 * @param pageSize
	 *           Page size
	 *
	 * @return List of EntitlementData for the current customer
	 */

	public List<EntitlementData> getEntitlementsForCurrentCustomer(final int pageNumber, final int pageSize);


	/**
	 * Get Entitlement details for the given Entitlement Number if it exists for current customer
	 *
	 * @param entitlementNumber
	 *           Entitlement Number
	 *
	 * @return EntitlementData object for the given Entitlement Number
	 */
	public EntitlementData getEntitlementForNumber(String entitlementNumber);

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service;

import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;


/**
 * Service to manage entitlement with SAP Entitlements
 */
public interface SapEntitlementService
{
	/**
	 * Get all Entitlements from SAP Entitlements for current customer
	 *
	 * @param pageNumber
	 *           Page number for pagination
	 * @param pageSize
	 *           Page size
	 * @return Entitlements data from SAP Entitlements
	 */
	public Entitlements getEntitlementsForCurrentCustomer(final int pageNumber, final int pageSize);

	/**
	 * Get Entitlement details for the given Entitlement Number if it exists for current customer
	 *
	 * @param entitlementNumber
	 *           Entitlement Number
	 *
	 * @return Entitlements data from SAP Entitlements
	 */
	public Entitlements getEntitlementForNumber(String entitlementNumber);
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.facades;

import java.util.Date;

/**
 *  Facade interface for Service order checkout
 */
public interface SapServiceOrderCheckoutFacade {

	/**
	 * Retrieves the lead days configured in SAPConfiguration
	 * @return number of lead days
	 */
	public Integer getLeadDaysForService();
	
	/**
	 * Update cart model with schedule service date
	 * @param date schedule service date
	 */
	public void updateCartWithServiceScheduleDate(final Date date);
	
	/**
	 * Checks for Service Products in Cart
	 * @return true if cart contains service products
	 */
	public Boolean containsServiceProductInCart();

	/**
	 * Reteives requested service date from the current cart
	 * @return requested service date
	 */
	public Date getRequestedServiceDate();
	
	/**
	 * Returns service lead date
	 * @return service lead date
	 */
	public Date getServiceLeadDate();
	
	/**
	 * Reschdeules service order
	 * @param orderCode order id
	 * @param rescheduleDate rescheduleDate
	 * @return returns true on success and false otherwise
	 */
	public boolean rescheduleServiceRequestDate(final String orderCode, final Date rescheduleDate);
}

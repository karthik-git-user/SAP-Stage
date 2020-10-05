/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;


/**
 * override the generateCustomerID method of class {@link DefaultCustomerAccountService} and use the
 * sapCustomerIdGenerator to generate a customer ID
 */

/**
 * @deprecated (since = "2005", forRemoval = true)
 */
@Deprecated(since = "2005", forRemoval = true)
public class DefaultB2CSapCustomerAccountService extends DefaultCustomerAccountService
{

	private PersistentKeyGenerator sapCustomerIdGenerator;
	private PersistentKeyGenerator sapContactIdGenerator;

	/**
	 * Generates with sapCustomerIdGenerator a customer ID during registration
	 * 
	 * @param customerModel
	 */
	@Override
	protected void generateCustomerId(final CustomerModel customerModel)
	{
		customerModel.setCustomerID((String) getSapCustomerIdGenerator().generate());
	}

	/**
	 * Generates with sapCustomerIdGenerator a customer ID during registration
	 * 
	 * @param customerModel
	 */
	protected void generateContactId(final CustomerModel customerModel)
	{
		customerModel.setSapContactID((String) getSapContactIdGenerator().generate());
	}

	/**
	 * Returns the sapCustomerIdGenerator
	 * 
	 * @return sapCustomerIdGenerator
	 */
	public PersistentKeyGenerator getSapCustomerIdGenerator()
	{
		return sapCustomerIdGenerator;
	}

	/**
	 * Sets the sapCustomerIdGenerator
	 * 
	 * @param sapCustomerIdGenerator
	 */
	public void setSapCustomerIdGenerator(final PersistentKeyGenerator sapCustomerIdGenerator)
	{
		this.sapCustomerIdGenerator = sapCustomerIdGenerator;
	}

	/**
	 * Returns the Generator instance
	 * 
	 * @return sapContactIdGenerator
	 */
	public PersistentKeyGenerator getSapContactIdGenerator()
	{
		return sapContactIdGenerator;
	}

	/**
	 * Sets the Contact ID Generator
	 * 
	 * @param sapContactIdGenerator
	 */
	public void setSapContactIdGenerator(final PersistentKeyGenerator sapContactIdGenerator)
	{
		this.sapContactIdGenerator = sapContactIdGenerator;
	}

}

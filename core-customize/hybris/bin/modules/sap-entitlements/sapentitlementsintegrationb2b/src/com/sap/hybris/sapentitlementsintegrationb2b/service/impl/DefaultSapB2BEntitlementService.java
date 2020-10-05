/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegrationb2b.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;


import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService;
import com.sap.hybris.sapentitlementsintegration.service.impl.DefaultSapEntitlementService;


/**
 * B2B implementation of SapEntitlementService
 */
public class DefaultSapB2BEntitlementService extends DefaultSapEntitlementService implements SapEntitlementService
{
	private B2BUnitService b2bUnitService;

	@Override
	protected String getCustomerId()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
			final B2BUnitModel b2bUnit = (B2BUnitModel) b2bUnitService.getParent(b2bCustomer);
			final B2BUnitModel rootUnit = (B2BUnitModel) b2bUnitService.getRootUnit(b2bUnit);
			return rootUnit.getUid();
		}
		if (user instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) user;
			return customer.getCustomerID();
		}
		else
		{
			throw new SapEntitlementException("User " + user.getUid() + " is not an instance of CustomerModel");
		}
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */

  @Required
	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}


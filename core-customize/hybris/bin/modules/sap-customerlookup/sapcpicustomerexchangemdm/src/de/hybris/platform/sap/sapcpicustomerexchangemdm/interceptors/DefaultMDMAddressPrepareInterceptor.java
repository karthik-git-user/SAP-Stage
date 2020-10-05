/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.interceptors;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;


/**
 * Interceptor to set address.sapAddressUUID if it is null.
 */
public class DefaultMDMAddressPrepareInterceptor implements PrepareInterceptor<AddressModel>
{

	@Override
	public void onPrepare(final AddressModel address, final InterceptorContext ctx) throws InterceptorException
	{
		if (StringUtils.isEmpty(address.getSapAddressUUID()))
		{
			address.setSapAddressUUID(UUID.randomUUID().toString());
		}
	}

}

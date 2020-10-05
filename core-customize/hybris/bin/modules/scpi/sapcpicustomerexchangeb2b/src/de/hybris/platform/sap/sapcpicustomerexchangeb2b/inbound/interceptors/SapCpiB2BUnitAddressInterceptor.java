/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.inbound.interceptors;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import com.sap.hybris.sapcustomerb2b.inbound.DefaultSAPCustomerAddressConsistencyInterceptor;


public class SapCpiB2BUnitAddressInterceptor extends DefaultSAPCustomerAddressConsistencyInterceptor
{
	@Override
	public void onPrepare(Object model, InterceptorContext context) throws InterceptorException
	{
		// Disable the interceptor logic form the sapcustomerb2b extension
		return;
	}

}

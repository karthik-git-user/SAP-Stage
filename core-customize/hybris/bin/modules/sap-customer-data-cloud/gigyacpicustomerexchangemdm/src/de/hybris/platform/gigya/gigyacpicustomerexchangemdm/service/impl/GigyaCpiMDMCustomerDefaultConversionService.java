/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyacpicustomerexchangemdm.service.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchangemdm.service.impl.SapCpiMDMCustomerDefaultConversionService;


/**
 * Default implementation of GigyaCpiMDMCustomerDefaultConversionService
 */
public class GigyaCpiMDMCustomerDefaultConversionService extends SapCpiMDMCustomerDefaultConversionService
{
	@Override
	public SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(final CustomerModel customerModel,
			final AddressModel addressModel, final String baseStoreUid, final String sessionLanguage)
	{

		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = super.convertCustomerToSapCpiCustomer(customerModel,
				addressModel, baseStoreUid, sessionLanguage);
		//set the gigyauid  here gyUID
		sapCpiOutboundCustomer.setGigyaUID(customerModel.getGyUID());
		return sapCpiOutboundCustomer;
	}

}

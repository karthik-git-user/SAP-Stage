/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyacpicustomerexchangemdmb2b.outbound.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BContactModel;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.impl.SapCpiB2BCustomerDefaultConversionService;


/**
 * Enhanced implementation of 'SapCpiB2BCustomerDefaultConversionService'
 */
public class GigyaCpiB2BCustomerDefaultConversionService extends SapCpiB2BCustomerDefaultConversionService
{

	@Override
	protected SAPCpiOutboundB2BContactModel convertB2BContactToSapCpiBb2BContact(final B2BUnitModel b2bUnitModel,
			final B2BCustomerModel b2bCustomerModel, final String sessionLanguage)
	{
		final SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact = super.convertB2BContactToSapCpiBb2BContact(b2bUnitModel,
				b2bCustomerModel, sessionLanguage);

		sapCpiOutboundB2BContact.setGigyaUID(b2bCustomerModel.getGyUID());
		return sapCpiOutboundB2BContact;
	}
}

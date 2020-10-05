/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.google.gson.JsonObject;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;


public class ExternalBillingDocumentPopulator implements Populator<JsonObject, ExternalSystemBillingDocumentData>
{
	@Override
	public void populate(final JsonObject source, final ExternalSystemBillingDocumentData target) throws ConversionException
	{
		target.setBillingDocumentId( source.get("BillingDocument").getAsString());

	}

}

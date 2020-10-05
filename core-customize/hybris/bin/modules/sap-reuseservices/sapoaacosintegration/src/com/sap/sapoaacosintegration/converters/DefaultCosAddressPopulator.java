/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.converters;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.data.AddressData;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.user.AddressModel} as source and
 * {@link de.hybris.platform.storelocator.data.AddressData} as target type.
 */
public class DefaultCosAddressPopulator implements Populator<AddressModel, AddressData>
{
	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		if (source != null && target != null)
		{
			if (source.getCountry() != null)
			{
				target.setCountryCode(source.getCountry().getIsocode());
			}
			target.setCity(source.getTown());
			target.setStreet(source.getStreetname());
			target.setZip(source.getPostalcode());
		}
	}
}

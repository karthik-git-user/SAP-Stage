/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaIdentityData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.collections.MapUtils;



/**
 * Populator to populate 'GigyaIdentityData' to fetch authorizations from SAP CDC
 */
public class GigyaAuthRequestIdentityPopulator implements Populator<CustomerModel, GigyaIdentityData>
{

	@Override
	public void populate(final CustomerModel customer, final GigyaIdentityData identityData) throws ConversionException
	{
		identityData.setId(customer.getGyUID());
		identityData.setAttributes(MapUtils.EMPTY_MAP);
	}

}

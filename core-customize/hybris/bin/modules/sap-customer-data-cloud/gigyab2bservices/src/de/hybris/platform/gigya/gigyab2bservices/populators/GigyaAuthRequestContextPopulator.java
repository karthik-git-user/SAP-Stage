/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaContextData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populator to populate 'GigyaContextData' to fetch authorizations from SAP CDC
 */
public class GigyaAuthRequestContextPopulator implements Populator<B2BUnitModel, GigyaContextData>
{

	@Override
	public void populate(final B2BUnitModel b2bUnit, final GigyaContextData contextData) throws ConversionException
	{
		contextData.setOrganization(b2bUnit.getGyUID());
	}
}

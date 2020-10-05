/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import org.springframework.util.Assert;

import com.sap.retail.oaa.commerce.facades.schedulelines.OaaScheduleLineData;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.platform.converters.Populator;


/**
 * Populator for ScheduleLineModel to ScheduleLineData conversion
 */
public class DefaultOaaScheduleLinePopulator implements Populator<ScheduleLineModel, OaaScheduleLineData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ScheduleLineModel source, final OaaScheduleLineData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		Assert.notNull(source.getConfirmedDate(), "Parameter confirmedDate cannot be null.");
		Assert.notNull(source.getConfirmedQuantity(), "Parameter confirmedQuantity cannot be null.");


		target.setConfirmedQuantity(source.getConfirmedQuantity());
		target.setConfirmedDate(source.getConfirmedDate());

	}

}

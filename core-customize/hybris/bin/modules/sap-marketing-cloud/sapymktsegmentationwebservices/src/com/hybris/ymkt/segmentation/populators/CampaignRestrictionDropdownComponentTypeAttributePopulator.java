/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.segmentation.populators;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;


public class CampaignRestrictionDropdownComponentTypeAttributePopulator
		implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	
	/**
	 * The following method will set the URI of the seDropdown. 
	 * The seDropdown will call the following Uri to populate its values
	 */
	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target)
	{
		target.setUri("/sapymktsegmentationwebservices/v1/data/segmentation/" + source.getQualifier());
		target.setPaged(true);
		target.setEditable(true);
		target.setRequired(true);
	}
}
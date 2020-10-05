/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.segmentation.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

import com.hybris.ymkt.segmentation.model.CMSYmktCampaignRestrictionModel;



/**
 * 
 */
public class CampaignRestrictionDescriptionHandler implements DynamicAttributeHandler<String, CMSYmktCampaignRestrictionModel>
{
	@Override
	public String get(final CMSYmktCampaignRestrictionModel model)
	{
		return Localization.getLocalizedString("type.CMSYmktCampaignRestriction.description");
	}

	@Override
	public void set(final CMSYmktCampaignRestrictionModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}
}

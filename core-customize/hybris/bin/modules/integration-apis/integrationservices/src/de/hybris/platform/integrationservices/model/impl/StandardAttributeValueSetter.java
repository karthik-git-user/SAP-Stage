/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.AttributeValueSetter;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.servicelayer.model.ModelService;

import com.google.common.base.Preconditions;

public class StandardAttributeValueSetter implements AttributeValueSetter
{
	private final TypeAttributeDescriptor attribute;
	private final ModelService modelService;

	public StandardAttributeValueSetter(final TypeAttributeDescriptor attribute, final ModelService service)
	{
		Preconditions.checkArgument(attribute != null, "Type attribute descriptor is required and cannot be null");
		Preconditions.checkArgument(service != null, "Model service is required and cannot be null");
		this.attribute = attribute;
		modelService = service;
	}

	@Override
	public void setValue(final Object model, final Object value)
	{
		modelService.setAttributeValue(model, attribute.getQualifier(), value);
	}
}

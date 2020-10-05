/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;

public class FilterByClassificationAttributeNotSupportedException extends RuntimeException
{
	private static final String MSG_TEMPLATE = "Filtering by classification attribute %s is not supported.";
	private final AbstractIntegrationObjectItemAttributeModel attribute;

	public FilterByClassificationAttributeNotSupportedException(final AbstractIntegrationObjectItemAttributeModel attr)
	{
		super(String.format(MSG_TEMPLATE, attr.getAttributeName()));
		attribute = attr;
	}

	public AbstractIntegrationObjectItemAttributeModel getAttribute()
	{
		return attribute;
	}
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.facade;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

import java.util.Collections;

/**
 * This {@link ConsumedDestinationModel} indicates that an actual ConsumedDestinationModel was not found
 */
public class ConsumedDestinationNotFoundModel extends ConsumedDestinationModel
{
	private static final String DESTINATION_NOT_FOUND = "Destination '%s' was not found.";

	private final String destinationId;

	public ConsumedDestinationNotFoundModel(final String destinationId)
	{
		this.destinationId = destinationId;
		super.setUrl(String.format(DESTINATION_NOT_FOUND, destinationId));
		super.setAdditionalProperties(Collections.emptyMap());
	}

	public String getDestinationId()
	{
		return destinationId;
	}
}

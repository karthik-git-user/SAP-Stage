/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator.impl;

import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * This decorator throws a {@link ModelNotFoundException} exception when the destination in
 * the {@link DecoratorContext} is of type {@link ConsumedDestinationNotFoundModel}.
 * By throwing the exception, the error in the {@link de.hybris.platform.outboundservices.model.OutboundRequestModel}
 * will be populated with the exception message.
 */
public class DefaultConsumedDestinationNotFoundDecorator implements OutboundRequestDecorator
{
	private static final String ERROR_MSG = "Provided destination '%s' was not found.";

	@Override
	public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload, final DecoratorContext context, final DecoratorExecution execution)
	{
		if (context.getDestinationModel() instanceof ConsumedDestinationNotFoundModel)
		{
			throw new ModelNotFoundException(String.format(ERROR_MSG,
					((ConsumedDestinationNotFoundModel) context.getDestinationModel()).getDestinationId()));
		}
		return execution.createHttpEntity(httpHeaders, payload, context);
	}
}

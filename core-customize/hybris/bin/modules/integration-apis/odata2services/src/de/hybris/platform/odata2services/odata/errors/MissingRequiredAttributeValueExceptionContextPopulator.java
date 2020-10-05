/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.inboundservices.persistence.populator.MissingRequiredAttributeValueException;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

/**
 * An {@link ErrorContextPopulator} that provides context error values for any {@link MissingRequiredAttributeValueException}s.
 */
public class MissingRequiredAttributeValueExceptionContextPopulator implements ErrorContextPopulator
{
	@Override
	public void populate(@NotNull final ODataErrorContext context)
	{
		final var contextException = context.getException();
		if (contextException instanceof MissingRequiredAttributeValueException)
		{
			final var exception = (MissingRequiredAttributeValueException) contextException;
			context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
			context.setErrorCode("missing_property");
			context.setMessage(exception.getMessage());
			context.setInnerError(exception.getIntegrationKey());
			context.setLocale(Locale.ENGLISH);
		}
	}

	@Override
	public Class<? extends Exception> getExceptionClass()
	{
		return MissingRequiredAttributeValueException.class;
	}
}

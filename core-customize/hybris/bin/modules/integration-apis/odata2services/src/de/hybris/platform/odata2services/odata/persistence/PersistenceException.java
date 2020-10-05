/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmException;

public class PersistenceException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.INTERNAL_SERVER_ERROR;
	private static final String DEFAULT_ERROR_CODE = "internal_error";

	/**
	 * Constructor to create PersistenceException
	 *
	 * @param e              exception that was thrown
	 * @param storageRequest object that holds values for creating or updating an item
	 */
	public PersistenceException(final Throwable e, final StorageRequest storageRequest)
	{
		super(generateMessage(e, storageRequest), STATUS_CODE, DEFAULT_ERROR_CODE, e, storageRequest.getIntegrationKey());
	}

	private static String generateMessage(final Throwable t, final StorageRequest storageRequest)
	{
		try
		{
			return String.format("An error occurred while attempting to save entries for entityType: %s",
					storageRequest.getEntityType().getName()) +
					gedAdditionalMessage(t);
		}
		catch (final EdmException e)
		{
			return "An error occurred while attempting to save entries.";
		}
	}

	private static String gedAdditionalMessage(final Throwable t)
	{
		final String prefix = ", with error message ";
		if (t.getCause() instanceof InterceptorException)
		{
			return prefix + extractExceptionCauseDetail(t);
		}
		else if (t instanceof SystemException)
		{
			return prefix + t.getMessage();
		}
		return "";
	}

	private static String extractExceptionCauseDetail(final Throwable t)
	{
		return messageContainsPackageAndClassName(t) ? extractDetailMessageWithoutClassName(t) : t.getCause().getMessage();
	}

	private static String extractDetailMessageWithoutClassName(final Throwable t)
	{
		return StringUtils.substringAfter(t.getCause().getMessage(), "]:");
	}

	private static boolean messageContainsPackageAndClassName(final Throwable t)
	{
		return t.getCause().getMessage().contains("]:");
	}
}

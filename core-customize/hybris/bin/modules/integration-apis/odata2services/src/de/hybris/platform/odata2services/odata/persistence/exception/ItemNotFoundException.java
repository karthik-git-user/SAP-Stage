/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence.exception;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;


/**
 * Exception to throw for error scenarios produced by invalid Data.
 * Will result in HttpStatus 404
 */
public class ItemNotFoundException extends ODataRuntimeApplicationException
{
	private static final String ITEM_NOT_FOUND_MESSAGE = "[%s] with integration key [%s] was not found.";
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.NOT_FOUND;
	private static final String NOT_FOUND_CODE = "not_found";

	private final String entityType;
	private final String integrationKey;

	/**
	 * Instantiates this exception
	 * @param context context for the exception to instantiate.
	 */
	public ItemNotFoundException(final PersistenceContext context)
	{
		this(contextEntityType(context), contextIntegrationKey(context));
	}

	/**
	 * Instantiates this exception for the specified item search request
	 * @param request item search request that resulted in not finding the item in the persistence storage
	 */
	public ItemNotFoundException(final ItemLookupRequest request)
	{
		this(request.getTypeDescriptor().getItemCode(), request.getIntegrationKey());
	}

	/**
	 * Constructor to create ItemNotFoundException
	 *
	 * @param entityType entity type
	 * @param integrationKey integration key
	 */
	public ItemNotFoundException(final String entityType, final String integrationKey)
	{
		this(entityType, integrationKey, null);
	}

	/**
	 * Constructor to create ItemNotFoundException
	 *
	 * @param entityType entity type
	 * @param integrationKey integration key
	 * @param e exception to get Message from
	 */
	public ItemNotFoundException(final String entityType, final String integrationKey, final Throwable e)
	{
		super(String.format(ITEM_NOT_FOUND_MESSAGE, entityType, integrationKey), Locale.ENGLISH, STATUS_CODE, NOT_FOUND_CODE, e);
		this.entityType = entityType;
		this.integrationKey = integrationKey;
	}

	private static String contextEntityType(final PersistenceContext context)
	{
		return context != null
				? context.getIntegrationItem().getItemType().getItemCode()
				: null;
	}

	private static String contextIntegrationKey(final PersistenceContext context)
	{
		return context != null
				? context.getIntegrationItem().getIntegrationKey()
				: null;
	}

	/**
	 * Gets entity type
	 * @return entity/item type searched
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * Gets integration key
	 * @return integration key value by which the item was searched
	 */
	public String getIntegrationKey()
	{
		return integrationKey;
	}
}

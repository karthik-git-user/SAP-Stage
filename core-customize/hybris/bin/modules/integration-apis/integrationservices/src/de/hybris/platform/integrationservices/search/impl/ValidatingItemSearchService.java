/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.search.ItemSearchRequest;
import de.hybris.platform.integrationservices.search.ItemSearchResult;
import de.hybris.platform.integrationservices.search.ItemSearchService;
import de.hybris.platform.integrationservices.search.validation.ItemSearchRequestValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * This implementation validates {@link ItemSearchRequest} before performing a search. If request is valid, then
 * processing is delegated to {@link DefaultItemSearchService}, otherwise the search is vetoed by throwing an exception.
 */
public class ValidatingItemSearchService implements ItemSearchService
{
	private final ItemSearchService itemSearchService;
	private List<ItemSearchRequestValidator> uniqueItemSearchValidators = Collections.emptyList();
	private List<ItemSearchRequestValidator> itemsSearchValidators = Collections.emptyList();
	private List<ItemSearchRequestValidator> countSearchValidators = Collections.emptyList();

	/**
	 * Instantiates this service.
	 * @param service a service to delegate search to in case when no validation problems found.
	 */
	public ValidatingItemSearchService(@NotNull final ItemSearchService service)
	{
		Preconditions.checkArgument(service != null, "ItemSearchService to delegate search to is required");
		itemSearchService = service;
	}

	@Override
	public Optional<ItemModel> findUniqueItem(final ItemSearchRequest request)
	{
		runValidators(request, uniqueItemSearchValidators);
		return itemSearchService.findUniqueItem(request);
	}

	@Override
	public ItemSearchResult<ItemModel> findItems(final ItemSearchRequest request)
	{
		runValidators(request, itemsSearchValidators);
		return itemSearchService.findItems(request);
	}

	@Override
	public int countItems(final ItemSearchRequest request)
	{
		runValidators(request, countSearchValidators);
		return itemSearchService.countItems(request);
	}

	private ItemSearchRequest runValidators(final ItemSearchRequest request, final List<ItemSearchRequestValidator> validators)
	{
		for (final ItemSearchRequestValidator v : validators)
		{
			v.validate(request);
		}
		return request;
	}

	/**
	 * Provides validators to be used for validating item search requests when {@link #findUniqueItem(ItemSearchRequest)} is called.
	 * The validators can be configured programmatically or by overriding {@code integrationServicesUniqueItemSearchValidators}
	 * list definition in the Spring configuration.
	 * @param validators validators to use. If empty or this method is not called, then {@link #findUniqueItem(ItemSearchRequest)}
	 *                   will be called unconditionally.
	 */
	public void setUniqueItemSearchValidators(@NotNull final List<ItemSearchRequestValidator> validators)
	{
		uniqueItemSearchValidators = validators;
	}

	/**
	 * Provides validators to be used for validating item search requests when {@link #findItems(ItemSearchRequest)} is called.
	 * The validators can be configured programmatically or by overriding {@code integrationServicesItemsSearchValidators}
	 * list definition in the Spring configuration.
	 * @param validators validators to use. If empty or this method is not called, then {@link #findItems(ItemSearchRequest)}
	 *                   will be called unconditionally.
	 */
	public void setItemsSearchValidators(@NotNull final List<ItemSearchRequestValidator> validators)
	{
		itemsSearchValidators = validators;
	}

	/**
	 * Provides validators to be used for validating item search requests when {@link #countItems(ItemSearchRequest)} is called.
	 * The validators can be configured programmatically or by overriding {@code integrationServicesCountSearchValidators}
	 * list definition in the Spring configuration.
	 * @param validators validators to use. If empty or this method is not called, then {@link #countItems(ItemSearchRequest)}
	 *                   will be called unconditionally.
	 */
	public void setCountSearchValidators(@NotNull final List<ItemSearchRequestValidator> validators)
	{
		countSearchValidators = validators;
	}
}

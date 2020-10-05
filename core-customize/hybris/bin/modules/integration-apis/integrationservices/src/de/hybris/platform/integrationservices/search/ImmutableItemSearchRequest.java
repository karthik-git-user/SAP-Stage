/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search;

import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Immutable value object for representing a search request
 */
public class ImmutableItemSearchRequest implements ItemSearchRequest
{
	@NotNull
	private final TypeDescriptor typeDescriptor;
	private IntegrationItem integrationItem;
	private PaginationParameters paginationParameters;
	private WhereClauseConditions filter;
	private List<OrderExpression> orderBy;
	private Locale locale;
	private boolean totalCount;
	private boolean countOnly;

	private ImmutableItemSearchRequest(final TypeDescriptor type)
	{
		Preconditions.checkArgument(type != null, "TypeDescriptor must be specified for a ImmutableItemSearchRequest");
		typeDescriptor = type;
	}

	@Override
	public Optional<PaginationParameters> getPaginationParameters()
	{
		return Optional.ofNullable(paginationParameters);
	}

	@Override
	public @NotNull TypeDescriptor getTypeDescriptor()
	{
		return typeDescriptor;
	}

	@Override
	public Optional<IntegrationItem> getRequestedItem()
	{
		return Optional.ofNullable(integrationItem);
	}

	@Override
	public WhereClauseConditions getFilter()
	{
		return filter;
	}

	@Override
	public List<OrderExpression> getOrderBy()
	{
		return orderBy != null ? Collections.unmodifiableList(orderBy) : Collections.emptyList();
	}

	@Override
	public Locale getAcceptLocale()
	{
		return locale;
	}

	@Override
	public boolean includeTotalCount()
	{
		return totalCount;
	}

	@Override
	public boolean isCountOnly()
	{
		return countOnly;
	}

	/**
	 * A builder for {@link ImmutableItemSearchRequest}
	 */
	public static class Builder
	{
		private TypeDescriptor typeDescriptor;
		private IntegrationItem integrationItem;
		private PaginationParameters pageParameters;
		private WhereClauseConditions whereClause;
		private List<OrderExpression> orderBy;
		private Locale locale;
		private boolean totalCount;
		private boolean countOnly;

		public Builder withIntegrationItem(final IntegrationItem item)
		{
			integrationItem = item;
			return withItemType(item.getItemType());
		}

		public Builder withItemType(final TypeDescriptor type)
		{
			typeDescriptor = type;
			return this;
		}

		public Builder withFilter(final WhereClauseConditions filter)
		{
			whereClause = filter;
			return this;
		}

		public Builder withOrderBy(final List<OrderExpression> orderByExpressions)
		{
			orderBy = orderByExpressions;
			return this;
		}

		Builder withLocale(final Locale locale)
		{
			this.locale = locale;
			return this;
		}

		public Builder withCountOnly()
		{
			countOnly = true;
			return withTotalCount();
		}

		public Builder withTotalCount()
		{
			totalCount = true;
			return this;
		}

		public Builder withPageParameters(final PaginationParameters params)
		{
			pageParameters = params;
			return this;
		}

		public ImmutableItemSearchRequest build()
		{
			final var request = new ImmutableItemSearchRequest(typeDescriptor);
			request.integrationItem = integrationItem;
			request.paginationParameters = pageParameters;
			request.filter = whereClause;
			request.orderBy = orderBy;
			request.totalCount = totalCount;
			request.countOnly = countOnly;
			request.locale = locale;
			return request;
		}
	}
}

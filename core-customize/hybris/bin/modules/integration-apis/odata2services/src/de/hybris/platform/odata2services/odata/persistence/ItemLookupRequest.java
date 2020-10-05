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

package de.hybris.platform.odata2services.odata.persistence;

import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.ItemSearchRequest;
import de.hybris.platform.integrationservices.search.OrderExpression;
import de.hybris.platform.integrationservices.search.PaginationParameters;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

import com.google.common.base.Preconditions;

public class ItemLookupRequest extends CrudRequest implements ItemSearchRequest
{
	private Integer skip;
	private Integer top;
	private boolean count;
	private boolean countOnly;
	private List<ArrayList<NavigationPropertySegment>> expand;
	private List<NavigationSegment> navigationSegments;
	private WhereClauseConditions filter;
	private List<OrderExpression> orderBy;
	private boolean noFilterResult;
	private TypeDescriptor typeDescriptor;

	protected ItemLookupRequest()
	{
		// protected constructor
	}

	static ItemLookupRequestBuilder itemLookupRequestBuilder()
	{
		return new ItemLookupRequestBuilder(new ItemLookupRequest());
	}

	public Integer getSkip()
	{
		return skip;
	}

	public Integer getTop()
	{
		return top;
	}

	public List<NavigationSegment> getNavigationSegments()
	{
		return navigationSegments;
	}

	@Override
	public Optional<PaginationParameters> getPaginationParameters()
	{
		return top != null || skip != null
				? Optional.of(PaginationParameters.create(safeInt(skip), safeInt(top)))
				: Optional.empty();
	}

	private static int safeInt(final Integer value)
	{
		return value != null ? value : 0;
	}

	@Override
	public @NotNull TypeDescriptor getTypeDescriptor()
	{
		return typeDescriptor;
	}

	@Override
	public Optional<IntegrationItem> getRequestedItem()
	{
		return Optional.ofNullable(getIntegrationItem());
	}

	@Override
	public boolean includeTotalCount()
	{
		return count;
	}

	@Override
	public WhereClauseConditions getFilter()
	{
		return filter;
	}

	@Override
	public List<OrderExpression> getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @deprecated since 1905.2002-CEP attribute conditions are converted to filter conditions and therefore
	 * can be retrieved by {@link #getFilter()} call.
	 */
	@Deprecated(since = "1905.2002-CEP", forRemoval = true)
	public Pair<String, String> getAttribute()
	{
		return null;
	}

	public boolean isNoFilterResult()
	{
		return noFilterResult;
	}

	/**
	 * Determines whether total number of items matching this request should be included in the response or not.
	 *
	 * @return {@code true}, if the response must include the total number of matching items; {@code false}, if the response
	 * needs to contain item(s) only and does not need total count.
	 * @deprecated use {@link #includeTotalCount()} method instead
	 */
	@Deprecated(since = "1905.01-CEP", forRemoval = true)
	public boolean isCount()
	{
		return includeTotalCount();
	}

	@Override
	public boolean isCountOnly()
	{
		return countOnly;
	}

	public List<ArrayList<NavigationPropertySegment>> getExpand()
	{
		return expand;
	}

	/**
	 * Builds a conversion request from this item lookup request
	 *
	 * @param item    an item to be converted to an ODataEntry
	 * @param options conversion options to apply
	 * @return an item conversion request built from this item lookup request
	 * @throws EdmException if underlying EDM is invalid. This is not likely because in such case this request could not be built
	 *                      at the first place
	 */
	public ItemConversionRequest toConversionRequest(final ItemModel item, final ConversionOptions options) throws EdmException
	{
		return itemConversionRequestBuilder()
				.withEntitySet(getEntitySet())
				.withValue(item)
				.withAcceptLocale(getAcceptLocale())
				.withOptions(options)
				.withIntegrationObject(getIntegrationObjectCode())
				.build();
	}

	static class ItemLookupRequestBuilder extends DataRequestBuilder<ItemLookupRequestBuilder, ItemLookupRequest>
	{
		ItemLookupRequestBuilder(final ItemLookupRequest itemLookupRequest)
		{
			super(itemLookupRequest);
		}

		ItemLookupRequestBuilder withNavigationSegments(final List<NavigationSegment> navigationSegments)
		{
			request().navigationSegments = navigationSegments;
			return myself();
		}

		ItemLookupRequestBuilder withSkip(final Integer skip)
		{
			request().skip = skip;
			return myself();
		}

		ItemLookupRequestBuilder withTop(final Integer top)
		{
			request().top = top;
			return myself();
		}

		ItemLookupRequestBuilder withCount(final boolean count)
		{
			request().count = count;
			return myself();
		}

		ItemLookupRequestBuilder withCountOnly(final boolean count)
		{
			request().countOnly = count;
			return myself();
		}

		ItemLookupRequestBuilder withExpand(final List<ArrayList<NavigationPropertySegment>> expand)
		{
			request().expand = expand;
			return myself();
		}

		ItemLookupRequestBuilder withFilter(final WhereClauseConditions filter)
		{
			request().filter = filter;
			return this;
		}

		ItemLookupRequestBuilder withOrderBy(final List<OrderExpression> orderBy)
		{
			request().orderBy = orderBy;
			return this;
		}

		ItemLookupRequestBuilder withLocale(final Locale locale)
		{
			request().setAcceptLocale(locale);
			return this;
		}

		ItemLookupRequestBuilder withHasNoFilterResult(final boolean hasNoFilterResult)
		{
			request().noFilterResult = hasNoFilterResult;
			return this;
		}

		ItemLookupRequestBuilder withTypeDescriptor(final TypeDescriptor descriptor)
		{
			request().typeDescriptor = descriptor;
			return this;
		}

		@Override
		public ItemLookupRequestBuilder withIntegrationItem(final IntegrationItem item)
		{
			final var type = item != null ? item.getItemType() : null;
			withTypeDescriptor(type);
			return super.withIntegrationItem(item);
		}

		@Override
		public ItemLookupRequestBuilder from(final ItemLookupRequest request)
		{
			return super.from(request)
			            .withTypeDescriptor(request.getTypeDescriptor())
			            .withIntegrationKey(request.getIntegrationKey())
			            .withNavigationSegments(request.getNavigationSegments())
			            .withSkip(request.getSkip())
			            .withTop(request.getTop())
			            .withCount(request.includeTotalCount())
			            .withCountOnly(request.isCountOnly())
			            .withExpand(request.getExpand())
			            .withServiceRoot(request.getServiceRoot())
			            .withContentType(request.getContentType())
			            .withRequestUri(request.getRequestUri())
			            .withFilter(request.getFilter())
			            .withOrderBy(request.getOrderBy())
			            .withLocale(request.getAcceptLocale())
			            .withHasNoFilterResult(request.isNoFilterResult());
		}

		@Override
		protected void assertValidValues() throws EdmException
		{
			super.assertValidValues();
			final Integer top = request().getTop();
			final Integer skip = request().getSkip();
			Preconditions.checkArgument(top == null || top >= 0, "Top cannot be less than 0");
			Preconditions.checkArgument(skip == null || skip >= 0, "Skip cannot be less than 0");
		}
	}
}

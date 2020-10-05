/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.persistence;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

/**
 * Captures all parameters related to persistence of an item data.
 * While persisting a complex item with nested item(s), a context is created for each item being persisted.
 */
public interface PersistenceContext
{
	/**
	 * Retrieves item data to be persisted.
	 * @return item data to be persisted into an {@link ItemModel}.
	 */
	IntegrationItem getIntegrationItem();

	/**
	 * Retrieves the persistence context for an item referenced by the specified attribute
	 * @param attribute specifies referenced item
	 * @return the context containing information about the referenced item
	 */
	PersistenceContext getReferencedContext(TypeAttributeDescriptor attribute);

	/**
	 * Retrieves the persistence contexts for the items referenced by the specified attribute
	 * @param attribute specifies referenced items
	 * @return the context containing information about the referenced items
	 */
	Collection<PersistenceContext> getReferencedContexts(TypeAttributeDescriptor attribute);

	/**
	 * Retrieves the persistence context that the referenced context was gotten from
	 * @return the source context. If the context is already the source, Optional.empty is returned
	 */
	Optional<PersistenceContext> getSourceContext();

	/**
	 * Retrieves the top most persistence context
	 * @return the root context
	 */
	PersistenceContext getRootContext();

	/**
	 * Indicates the persistence is to replace the item attributes with what's provided in the {@link IntegrationItem}.
	 * This is primarily applicable to collections. The default behavior is to append to the collection.
	 * With this method being true, the item's collection attribute will be replaced instead of appended.
	 * @return true means to replace attributes, otherwise false.
	 */
	default boolean isReplaceAttributes()
	{
		return false;
	}

	/**
	 * Determines whether a new item model can be created for the context payload.
	 * @return {@code true}, when new item can be create, if an item matching the context was not found in the persistent storage;
	 * {@code false}, if the context implies update only and therefore the item should not be created, if it does not exist yet.
	 */
	boolean isItemCanBeCreated();

	/**
	 * Indicates the language, in which localized attributes content is provided.
	 *
	 * @return {@link Locale} or {@code null}
	 */
	default Locale getContentLocale() { return null; }
}

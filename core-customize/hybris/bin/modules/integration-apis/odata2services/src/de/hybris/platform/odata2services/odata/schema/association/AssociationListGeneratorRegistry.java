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
package de.hybris.platform.odata2services.odata.schema.association;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * <p>Aggregates associations generated by registered associations generators.</p>
 * <p>Do not extend this class, if needs to be customized. Customization can be done easily by plugging/unplugging registered
 * associations generators in the Spring configuration. And even if it's absolutely necessary to override this class's methods,
 * write a custom registry that delegates to this registry and does custom things around the delegate call (use composition over
 * inheritance)</p>
 */
public class AssociationListGeneratorRegistry
{
	private List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> associationListGenerators;
	private List<SchemaElementGenerator<List<Association>, Collection<TypeDescriptor>>> associationGenerators = Collections.emptyList();
	private DescriptorFactory descriptorFactory;

	/**
	 * Generates associations that should be present in the schema by delegating to the registered generators and then combining
	 * their results.
	 * @param itemModels item models, for which associations should be generated.
	 * @return combined list of all associations generated by the nested generators.
	 * @deprecated use {@link #setAssociationGenerators(List)} instead. There is no need to get generators, if this class should not be extended.
	 * @see AssociationListGeneratorRegistry
	 */
	@Deprecated(since = "1905.10-CEP", forRemoval = true)
	public List<Association> generate(final Collection<IntegrationObjectItemModel> itemModels)
	{
		Preconditions.checkArgument(itemModels != null, "An association generator cannot be created for null IntegrationObjectItemModels.");

		final List<Association> associationsFromItemModels = getAssociationsFromItemModels(itemModels);
		final List<Association> associations = generateFor(toDescriptors(itemModels));
		return combineUniqueAssociations(associationsFromItemModels, associations);
	}

	private List<Association> getAssociationsFromItemModels(final Collection<IntegrationObjectItemModel> itemModels)
	{
		return getGenerators() != null
				? aggregateAssociationsProducedByNestedGenerators(itemModels)
				: new ArrayList<>();
	}

	private List<Association> aggregateAssociationsProducedByNestedGenerators(final Collection<IntegrationObjectItemModel> itemModels)
	{
		return getGenerators().stream()
				.map(generator -> generator.generate(itemModels))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private Collection<TypeDescriptor> toDescriptors(final Collection<IntegrationObjectItemModel> itemModels)
	{
		return itemModels.stream()
				.map(descriptorFactory::createItemTypeDescriptor)
				.collect(Collectors.toList());
	}

	/**
	 * Generates associations that should be present in the schema by delegating to the registered generators and then combining
	 * their results.
	 * @param descriptors integration item descriptors, for which EDMX associations should be generated
	 * @return combined list of all associations generated by the nested generators.
	 */
	public List<Association> generateFor(final Collection<TypeDescriptor> descriptors)
	{
		return CollectionUtils.isNotEmpty(descriptors)
				? combineAssociationsProducedByNestedGenerators(descriptors)
				: Collections.emptyList();
	}

	private List<Association> combineAssociationsProducedByNestedGenerators(final Collection<TypeDescriptor> descriptors)
	{
		return associationGenerators.stream()
				.map(g -> g.generate(descriptors))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private List<Association> combineUniqueAssociations(final List<Association> associationsFromItemModels, final List<Association> associations)
	{
		final Set<String> existingAssociations = associationsFromItemModels.stream()
				.map(Association::getName)
				.collect(Collectors.toCollection(HashSet::new));
		final List<Association> additionalAssociations = associations.stream()
				.filter(a -> !existingAssociations.contains(a.getName()))
				.collect(Collectors.toList());
		return ListUtils.union(associationsFromItemModels, additionalAssociations);
	}

	/**
	 * @deprecated use {@link #setAssociationGenerators(List)} instead. There is no need to get generators, if this class should not be extended.
	 * @see AssociationListGeneratorRegistry
	 */
	@Deprecated(since = "1905.10-CEP", forRemoval = true)
	protected List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> getGenerators()
	{
		return associationListGenerators;
	}

	/**
	 * @deprecated use {@link #setAssociationGenerators(List)} instead
	 */
	@Deprecated(since = "1905.10-CEP", forRemoval = true)
	public void setAssociationListGenerators(final List<SchemaElementGenerator<List<Association>, Collection<IntegrationObjectItemModel>>> generators)
	{
		associationListGenerators = generators;
	}

	public void setAssociationGenerators(final List<SchemaElementGenerator<List<Association>, Collection<TypeDescriptor>>> generators)
	{
		associationGenerators = generators != null
				? generators
				: Collections.emptyList();
	}

	/**
	 * @deprecated temporary used by {@link #generate(Collection)}. Once that method is removed, there will be no need for this factory.
	 */
	@Required
	@Deprecated(since = "1905.10-CEP", forRemoval = true)
	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}
}

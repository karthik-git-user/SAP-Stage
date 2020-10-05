/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;

import java.util.Set;

import org.apache.commons.lang.BooleanUtils;

public final class DefaultIntegrationObjectImpexGenerator implements IntegrationObjectImpexGenerator
{
	private static final String ENDL = " \r\n";
	private static final String GENERIC_INTEGRATION_OBJECT_HEADER =
			"INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)";
	private static final String GENERIC_INTEGRATION_OBJECT_ITEM_HEADER =
			"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code); root[default = false]; " +
					"itemTypeMatch(code)";
	private static final String GENERIC_INTEGRATION_OBJECT_ITEM_ATTRIBUTE_HEADER =
			"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; " +
					"attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); " +
					"returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]";
	private static final String GENERIC_INTEGRATION_OBJECT_ITEM_CLASSIFICATION_ATTRIBUTE_HEADER =
			"INSERT_UPDATE IntegrationObjectItemClassificationAttribute; " +
					"integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; " +
					"classAttributeAssignment(classificationClass(catalogVersion(catalog(id), version), code), classificationAttribute(systemVersion(catalog(id), version), code));" +
					"returnIntegrationObjectItem(integrationObject(code), code); autocreate[default = false]";
	private static final String HEADER_TAB_DELIMITER = "\t; ";
	private static final String HEADER_SPACE_DELIMITER = "; ";

	@Override
	public String generateImpex(final IntegrationObjectModel selectedIntegrationObject)
	{
		final Set<IntegrationObjectItemModel> items = selectedIntegrationObject.getItems();
		final StringBuilder impexBuilder = new StringBuilder();
		final int[] longestStrings = calculateLongestStrings(items);
		final int longestItem = longestStrings[0];
		final int longestTypeMatch = longestStrings[1];

		constructIntegrationObjectBlock(selectedIntegrationObject, impexBuilder);
		constructIntegrationObjectItemBlock(selectedIntegrationObject, items, impexBuilder, longestItem, longestTypeMatch);
		constructIntegrationObjectItemAttributeBlock(selectedIntegrationObject, items, impexBuilder, longestItem);

		if (selectedIntegrationObject.getClassificationAttributesPresent())
		{
			constructIntegrationObjectItemClassificationAttributeBlock(selectedIntegrationObject, items, impexBuilder,
					longestItem);
		}

		return impexBuilder.toString();
	}

	private void constructIntegrationObjectItemClassificationAttributeBlock(
			final IntegrationObjectModel selectedIntegrationObject,
			final Set<IntegrationObjectItemModel> items,
			final StringBuilder impexBuilder,
			final int longestItem)
	{
		final String integrationObject = selectedIntegrationObject.getCode();
		final int longestAttribute = calculateClassificationAttributeColumnsLength(items);
		final int longestClassAssignment = calculateClassAssignmentColumnsLength(items);
		impexBuilder.append(ENDL);
		impexBuilder.append(GENERIC_INTEGRATION_OBJECT_ITEM_CLASSIFICATION_ATTRIBUTE_HEADER);
		impexBuilder.append(ENDL);
		for (final IntegrationObjectItemModel item : items)
		{
			constructIntegrationObjectItemClassificationAttributeLine(integrationObject, longestAttribute, longestClassAssignment,
					item, impexBuilder, longestItem);
		}
	}

	private void constructIntegrationObjectItemAttributeBlock(final IntegrationObjectModel selectedIntegrationObject,
	                                                          final Set<IntegrationObjectItemModel> items,
	                                                          final StringBuilder impexBuilder, final int longestItem)
	{
		final String integrationObject = selectedIntegrationObject.getCode();
		final int[] lengths = calculateAttributeColumnsLength(items);
		impexBuilder.append(GENERIC_INTEGRATION_OBJECT_ITEM_ATTRIBUTE_HEADER);
		impexBuilder.append(ENDL);
		for (final IntegrationObjectItemModel item : items)
		{
			constructIntegrationObjectItemAttributeLine(integrationObject, lengths, item, impexBuilder, longestItem);
		}
	}

	private void constructIntegrationObjectItemBlock(final IntegrationObjectModel selectedIntegrationObject,
	                                                 final Set<IntegrationObjectItemModel> items,
	                                                 final StringBuilder impexBuilder, final int longestItem,
	                                                 final int longestTypeMatch)
	{

		final String integrationObject = selectedIntegrationObject.getCode();
		impexBuilder.append(GENERIC_INTEGRATION_OBJECT_ITEM_HEADER);
		impexBuilder.append(ENDL);
		for (final IntegrationObjectItemModel item : items)
		{
			constructIntegrationObjectItemLine(item, integrationObject, impexBuilder, longestItem, longestTypeMatch);
		}
		impexBuilder.append(ENDL);
	}

	private void constructIntegrationObjectBlock(final IntegrationObjectModel selectedIntegrationObject,
	                                             final StringBuilder impexBuilder)
	{
		impexBuilder.append(GENERIC_INTEGRATION_OBJECT_HEADER);
		impexBuilder.append(ENDL);
		impexBuilder.append(HEADER_SPACE_DELIMITER);
		impexBuilder.append(selectedIntegrationObject.getCode());
		impexBuilder.append(HEADER_SPACE_DELIMITER);
		final IntegrationType integrationType = selectedIntegrationObject.getIntegrationType();
		impexBuilder.append(integrationType != null ? integrationType.getCode() : "");
		impexBuilder.append(ENDL).append(ENDL);
	}

	private void constructIntegrationObjectItemLine(final IntegrationObjectItemModel item, final String integrationObject,
	                                                final StringBuilder impexBuilder, final int longestItem,
	                                                final int longestTypeMatch)
	{
		final String currentItemName = item.getType().getCode();
		final String itemTypeMatch = item.getItemTypeMatch() != null ? item.getItemTypeMatch().getCode() : "";
		impexBuilder.append(HEADER_SPACE_DELIMITER);
		impexBuilder.append(integrationObject);
		impexBuilder.append(HEADER_TAB_DELIMITER);
		impexBuilder.append(currentItemName);
		addWhitespace(longestItem, currentItemName.length(), impexBuilder);
		impexBuilder.append(HEADER_TAB_DELIMITER);
		impexBuilder.append(currentItemName);
		addWhitespace(longestItem, currentItemName.length(), impexBuilder);
		impexBuilder.append(HEADER_TAB_DELIMITER);
		impexBuilder.append(!item.getRoot() ? "" : item.getRoot().toString());
		impexBuilder.append(HEADER_TAB_DELIMITER);
		impexBuilder.append(itemTypeMatch);
		addWhitespace(longestTypeMatch, itemTypeMatch.length(), impexBuilder);
		impexBuilder.append(HEADER_TAB_DELIMITER);
		impexBuilder.append(ENDL);
	}

	private void constructIntegrationObjectItemAttributeLine(final String integrationObject, final int[] lengths,
	                                                         final IntegrationObjectItemModel item,
	                                                         final StringBuilder impexBuilder, final int longestItem)
	{
		final int longestAttributeIndex = 0;
		final int longestDescriptorIndex = 1;
		final int longestReturnIOIndex = 2;

		final String currentItemName = item.getCode();
		for (final IntegrationObjectItemAttributeModel attribute : item.getAttributes())
		{
			final String integrationObjectItem = integrationObject + ":" + currentItemName;
			final String attributeName = attribute.getAttributeName();
			final String attributeDescriptor = currentItemName + ":" + attribute.getAttributeDescriptor().getQualifier();
			final String returnIntegrationObject = (attribute.getReturnIntegrationObjectItem() != null) ?
					(integrationObject + ":" + attribute.getReturnIntegrationObjectItem().getCode()) : "";
			final String isUnique = (BooleanUtils.isTrue(attribute.getUnique())) ? attribute.getUnique().toString() : "";
			final String autocreate = (BooleanUtils.isTrue(attribute.getAutoCreate())) ?
					attribute.getAutoCreate().toString() : "";
			impexBuilder.append("; ");
			impexBuilder.append(integrationObjectItem);
			addWhitespace(longestItem, currentItemName.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(attributeName);
			addWhitespace(lengths[longestAttributeIndex], attributeName.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(attributeDescriptor);
			addWhitespace(lengths[longestDescriptorIndex], attributeDescriptor.length() - 1, impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(returnIntegrationObject);
			addWhitespace(lengths[longestReturnIOIndex], (attribute.getReturnIntegrationObjectItem() != null) ?
					attribute.getReturnIntegrationObjectItem().getCode().length() :
					-(integrationObject.length() + 1), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(isUnique);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(autocreate);
			impexBuilder.append(ENDL);
		}
	}

	private void constructIntegrationObjectItemClassificationAttributeLine(final String integrationObject,
	                                                                       final int longestAttribute,
	                                                                       final int longestClassAssignment,
	                                                                       final IntegrationObjectItemModel item,
	                                                                       final StringBuilder impexBuilder,
	                                                                       final int longestItem)
	{
		final String currentItemName = item.getCode();
		for (final IntegrationObjectItemClassificationAttributeModel classificationAttribute : item.getClassificationAttributes())
		{
			final ClassAttributeAssignmentModel classAttributeAssignmentModel = classificationAttribute.getClassAttributeAssignment();
			final String integrationObjectItem = integrationObject + ":" + currentItemName;
			final String attributeName = classificationAttribute.getAttributeName();
			final String classAttributeAssignment = determineClassAssignment(classAttributeAssignmentModel);
			final String referenceType = (classAttributeAssignmentModel.getReferenceType() != null) ?
					(integrationObject + ":" + classAttributeAssignmentModel.getReferenceType().getCode()) : "";
			final String autocreate = (BooleanUtils.isTrue(classificationAttribute.getAutoCreate())) ?
					classificationAttribute.getAutoCreate().toString() : "";
			impexBuilder.append("; ");
			impexBuilder.append(integrationObjectItem);
			addWhitespace(longestItem, currentItemName.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(attributeName);
			addWhitespace(longestAttribute, attributeName.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(classAttributeAssignment);
			addWhitespace(longestClassAssignment, classAttributeAssignment.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(referenceType);
			addWhitespace(longestItem, referenceType.length(), impexBuilder);
			impexBuilder.append(HEADER_TAB_DELIMITER);
			impexBuilder.append(autocreate);
			impexBuilder.append(ENDL);
		}
	}

	private void addWhitespace(final int longestStringLength, final int length, final StringBuilder impexBuilder)
	{
		int whitespaceNeeded = longestStringLength - length;
		while (whitespaceNeeded > 0)
		{
			impexBuilder.append(" ");
			whitespaceNeeded--;
		}
	}

	private int calculateClassAssignmentColumnsLength(final Set<IntegrationObjectItemModel> items)
	{
		int longestClassAssignment = 0;
		for (final IntegrationObjectItemModel item : items)
		{
			for (final IntegrationObjectItemClassificationAttributeModel attr : item.getClassificationAttributes())
			{
				final ClassAttributeAssignmentModel classAttributeAssignmentModel = attr.getClassAttributeAssignment();
				final String classAttributeAssignment = determineClassAssignment(classAttributeAssignmentModel);
				if (classAttributeAssignment.length() > longestClassAssignment)
				{
					longestClassAssignment = classAttributeAssignment.length();
				}
			}
		}
		return longestClassAssignment;
	}

	private String determineClassAssignment(final ClassAttributeAssignmentModel classAttributeAssignmentModel)
	{
		final ClassificationAttributeModel classificationAttributeModel = classAttributeAssignmentModel.getClassificationAttribute();
		final ClassificationSystemVersionModel systemVersionModel = classificationAttributeModel.getSystemVersion();
		final String system = systemVersionModel.getCatalog().getId();
		final String version = systemVersionModel.getVersion();
		final String systemVersion = system + ":" + version;
		final String classCode = classAttributeAssignmentModel.getClassificationClass().getCode();
		final String attributeCode = classificationAttributeModel.getCode();
		return systemVersion + ":" + classCode + ":" + systemVersion + ":" + attributeCode;
	}

	private int calculateClassificationAttributeColumnsLength(final Set<IntegrationObjectItemModel> items)
	{
		int longestAttributeName = 0;
		for (final IntegrationObjectItemModel item : items)
		{
			for (final IntegrationObjectItemClassificationAttributeModel attr : item.getClassificationAttributes())
			{
				if (attr.getAttributeName().length() > longestAttributeName)
				{
					longestAttributeName = attr.getAttributeName().length();
				}
			}
		}
		return longestAttributeName;
	}

	private int[] calculateAttributeColumnsLength(final Set<IntegrationObjectItemModel> items)
	{
		int longestAttribute = 0;
		int longestDescriptor = 0;
		int longestReturnIO = 0;
		for (final IntegrationObjectItemModel item : items)
		{
			for (final IntegrationObjectItemAttributeModel attr : item.getAttributes())
			{
				longestAttribute = Math.max(longestAttribute, attr.getAttributeName().length());
				longestDescriptor = Math.max(longestDescriptor, (item.getCode().length() + attr.getAttributeName().length()));
				if (attr.getReturnIntegrationObjectItem() != null)
				{
					longestReturnIO = Math.max(longestReturnIO, attr.getReturnIntegrationObjectItem().getCode().length());
				}
			}
		}
		return new int[]{ longestAttribute, longestDescriptor, longestReturnIO };
	}

	private int[] calculateLongestStrings(final Set<IntegrationObjectItemModel> items)
	{
		int longestItemMatchType = 0;
		int longestItem = 0;
		for (final IntegrationObjectItemModel item : items)
		{
			longestItem = Math.max(longestItem, item.getCode().length());
			final String itemTypeMatchCode = item.getItemTypeMatch() != null ? item.getItemTypeMatch().getCode() : "";
			longestItemMatchType = Math.max(longestItemMatchType, itemTypeMatchCode.length());
		}
		return new int[]{ longestItem, longestItemMatchType };
	}
}

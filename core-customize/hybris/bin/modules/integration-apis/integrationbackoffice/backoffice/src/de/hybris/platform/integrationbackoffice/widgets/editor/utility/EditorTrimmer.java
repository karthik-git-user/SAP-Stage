/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.editor.data.TreeNodeData;

import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EditorTrimmer
{
	private static ReadService readService;

	private EditorTrimmer()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Trims the map created during the editing process so that it only contains checked/selected items so that it is
	 * leaner for the persistence process.
	 *
	 * @param rs               ReadService to access the type system
	 * @param fullMap          The map created during the editing process.
	 * @param composedTypeTree The tree representing the structure of ComposedTypes.
	 * @param considerSubtypes Flag representing whether unselected subtype changes should be considered in the trim (should be false when trimming for persistence)
	 * @return The trimmed down map.
	 */
	public static Map<ComposedTypeModel, List<AbstractListItemDTO>> trimMap(final ReadService rs,
	                                                                        final Map<ComposedTypeModel, List<AbstractListItemDTO>> fullMap,
	                                                                        final Tree composedTypeTree,
	                                                                        final boolean considerSubtypes)
	{
		readService = rs;
		Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap = new HashMap<>();

		final Treeitem root = composedTypeTree.getItems().iterator().next();
		trim(root, fullMap, trimmedMap, considerSubtypes);

		return trimmedMap;
	}

	private static void trim(final Treeitem treeItem, Map<ComposedTypeModel, List<AbstractListItemDTO>> fullMap,
	                         Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap, boolean considerSubTypes)
	{
		final ComposedTypeModel key = ((TreeNodeData) treeItem.getValue()).getComposedTypeModel();
		final List<AbstractListItemDTO> dtoList = fullMap.get(key);
		final List<AbstractListItemDTO> trimmedList = new ArrayList<>();

		for (final AbstractListItemDTO dto : dtoList)
		{
			if (dto instanceof ListItemAttributeDTO)
			{
				addAttributeDTO(trimmedList, (ListItemAttributeDTO) dto, considerSubTypes);
			}
			else if (dto.isSelected())
			{
				trimmedList.add(dto);
			}
		}

		if (treeItem.getTreechildren() != null)
		{
			final Collection<Treeitem> children = treeItem.getTreechildren().getChildren();
			for (final AbstractListItemDTO dto : trimmedList)
			{
				trimChild(children, dto, fullMap, trimmedMap, considerSubTypes);
			}
		}

		trimmedMap.put(key, trimmedList);
	}

	private static void trimChild(final Collection<Treeitem> children, final AbstractListItemDTO dto,
	                              Map<ComposedTypeModel, List<AbstractListItemDTO>> fullMap,
	                              Map<ComposedTypeModel, List<AbstractListItemDTO>> trimmedMap, boolean considerSubTypes)
	{
		final boolean isStructuredType;
		final boolean isComplexType;
		final String qualifier;

		if (dto instanceof ListItemAttributeDTO)
		{
			final ListItemAttributeDTO attributeDTO = (ListItemAttributeDTO) dto;
			isStructuredType = attributeDTO.getStructureType() != ListItemStructureType.NONE;
			isComplexType = readService.isComplexType(attributeDTO.getType());
			qualifier = attributeDTO.getAttributeDescriptor().getQualifier();
		}
		else
		{
			final ListItemClassificationAttributeDTO classificationAttributeDTO = (ListItemClassificationAttributeDTO) dto;
			isStructuredType = false;
			isComplexType = (classificationAttributeDTO.getClassAttributeAssignmentModel().getReferenceType() != null);
			qualifier = classificationAttributeDTO.getClassificationAttributeCode();
		}

		if (isComplexType || isStructuredType)
		{
			for (final Treeitem child : children)
			{
				final TreeNodeData treeNodeData = child.getValue();
				if (treeNodeData.getQualifier().equals(qualifier))
				{
					trim(child, fullMap, trimmedMap, considerSubTypes);
					break;
				}
			}
		}
	}

	private static void addAttributeDTO(final List<AbstractListItemDTO> trimmedList, ListItemAttributeDTO dto,
	                                    boolean considerSubtypes)
	{
		if (considerSubtypes && (!dto.getBaseType().equals(dto.getType()) || dto.isSelected()))
		{
			trimmedList.add(dto);
		}
		else if (dto.isSelected())
		{
			trimmedList.add(dto);
		}
	}
}
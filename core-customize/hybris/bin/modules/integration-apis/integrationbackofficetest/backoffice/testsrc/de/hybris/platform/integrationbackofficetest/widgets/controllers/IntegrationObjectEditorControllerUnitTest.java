/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.controllers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemStructureType;
import de.hybris.platform.integrationbackoffice.widgets.editor.controllers.IntegrationObjectEditorController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Checkbox;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationObjectEditorControllerUnitTest
{
	@InjectMocks
	private IntegrationObjectEditorController controller = new IntegrationObjectEditorController();

	@Mock
	private Tree tree;
	@Mock
	private Treeitem treeitem;
	@Mock
	private Listitem listitem;
	@Mock
	private Component uniqueCheckboxComponent;
	@Mock
	private Component autocreateCheckboxComponent;
	@Mock
	private Listcell labelListcell;
	@Mock
	private Checkbox uniqueCheckbox;
	@Mock
	private Checkbox autocreateCheckbox;


	@Test
	public void testUpdateAttribute()
	{
		final AttributeDescriptorModel attributeDescriptorModel = new AttributeDescriptorModel();
		attributeDescriptorModel.setQualifier("StockLevel");
		attributeDescriptorModel.setAttributeType(new TypeModel());

		final ComposedTypeModel key = new ComposedTypeModel();
		final ListItemAttributeDTO dto = new ListItemAttributeDTO(false, false, false, attributeDescriptorModel,
				ListItemStructureType.NONE, "", null);

		final Map<ComposedTypeModel, List<AbstractListItemDTO>> attributesMap = new HashMap<>();
		attributesMap.put(key, Collections.singletonList(dto));

		controller.setCurrentAttributesMap(attributesMap);
		controller.setComposedTypeTree(tree);

		final List<Component> children = new ArrayList<>();
		children.add(labelListcell);
		children.add(null);
		children.add(null);
		children.add(uniqueCheckboxComponent);
		children.add(autocreateCheckboxComponent);

		when(tree.getSelectedItem()).thenReturn(treeitem);
		when(treeitem.getValue()).thenReturn(key);

		when(listitem.getValue()).thenReturn(dto);
		when(listitem.getLabel()).thenReturn("StockLevel");

		when(listitem.getChildren()).thenReturn(children);
		when(uniqueCheckboxComponent.getFirstChild()).thenReturn(uniqueCheckbox);
		when(autocreateCheckboxComponent.getFirstChild()).thenReturn(autocreateCheckbox);

		assertFalse(dto.isSelected());
		assertFalse(dto.isCustomUnique());
		assertFalse(dto.isAutocreate());
		assertNotEquals("NewStock", dto.getAlias());

		//Update to test again
		when(listitem.isSelected()).thenReturn(true);
		when(uniqueCheckbox.isChecked()).thenReturn(true);
		when(autocreateCheckbox.isChecked()).thenReturn(true);
		when(labelListcell.getLabel()).thenReturn("NewStock");

		controller.updateAttribute(listitem);

		final ListItemAttributeDTO actual = (ListItemAttributeDTO) attributesMap.get(key).get(0);
		assertTrue(actual.isSelected());
		assertTrue(actual.isCustomUnique());
		assertTrue(actual.isAutocreate());
		assertEquals("NewStock", actual.getAlias());
	}
}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.dto;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ListItemClassificationAttributeDTOUnitTest
{
	private String qualifier = "weight";
	private String category = "dimensions";

	@Test
	public void testListItemClassificationAttributeDTOConstructor()
	{
		ListItemClassificationAttributeDTO actual = TestUtils.createClassificationAttributeDTO("", qualifier,
				ClassificationAttributeTypeEnum.STRING, category);

		assertEquals(qualifier, actual.getAlias());
		assertEquals(category, actual.getCategoryCode());
	}

	@Test
	public void testListItemClassificationAttributeDTOSetAlias()
	{
		ListItemClassificationAttributeDTO actual = TestUtils.createClassificationAttributeDTO("", qualifier,
				ClassificationAttributeTypeEnum.STRING, category);

		actual.setAlias("");
		assertEquals(qualifier, actual.getAlias());

		String alias = "aliasOfWeight";
		actual.setAlias(alias);
		assertEquals(alias, actual.getAlias());
	}

	@Test
	public void testCreateDescriptionNonReference()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(null);
		when(assignmentModel.getMultiValued()).thenReturn(false);
		when(assignmentModel.getAttributeType()).thenReturn(type);

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [number]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionNonReferenceList()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(null);
		when(assignmentModel.getMultiValued()).thenReturn(true);
		when(assignmentModel.getAttributeType()).thenReturn(type);

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [Collection [number]]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionReferenceList()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ComposedTypeModel ctm = mock(ComposedTypeModel.class);
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(ctm);
		when(assignmentModel.getMultiValued()).thenReturn(true);
		when(ctm.getCode()).thenReturn("Product");

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [Collection [Product]]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionReference()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ComposedTypeModel ctm = mock(ComposedTypeModel.class);
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(ctm);
		when(assignmentModel.getMultiValued()).thenReturn(false);
		when(ctm.getCode()).thenReturn("Product");

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [Product]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionEnum()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.ENUM;
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(null);
		when(assignmentModel.getMultiValued()).thenReturn(false);
		when(assignmentModel.getAttributeType()).thenReturn(type);

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [ValueList]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateDescriptionLocalized()
	{
		ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		ClassificationAttributeTypeEnum type = ClassificationAttributeTypeEnum.NUMBER;
		ClassificationAttributeModel attr = mock(ClassificationAttributeModel.class);
		ClassificationClassModel classModel = mock(ClassificationClassModel.class);
		when(assignmentModel.getClassificationClass()).thenReturn(classModel);
		when(classModel.getCode()).thenReturn("class");
		when(assignmentModel.getClassificationAttribute()).thenReturn(attr);
		when(attr.getCode()).thenReturn("code");
		when(assignmentModel.getReferenceType()).thenReturn(null);
		when(assignmentModel.getMultiValued()).thenReturn(false);
		when(assignmentModel.getAttributeType()).thenReturn(type);
		when(assignmentModel.getLocalized()).thenReturn(true);

		ListItemClassificationAttributeDTO dto = new ListItemClassificationAttributeDTO(true, false, false, assignmentModel,
				"name");

		String expected = "Classification [localized:number]";
		String actual = dto.getDescription();

		assertEquals(expected, actual);
	}
}

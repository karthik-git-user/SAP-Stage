/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.dto;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.integrationbackoffice.utility.QualifierNameUtils;

import org.apache.commons.lang3.BooleanUtils;


public class ListItemClassificationAttributeDTO extends AbstractListItemDTO
{
	private ClassAttributeAssignmentModel classAttributeAssignmentModel;
	private String classificationAttributeCode;
	private String categoryCode;

	public ListItemClassificationAttributeDTO(final boolean selected, final boolean customUnique, final boolean autocreate,
	                                          final ClassAttributeAssignmentModel classAttributeAssignmentModel,
	                                          final String alias)
	{
		super(selected, customUnique, autocreate);
		this.classAttributeAssignmentModel = classAttributeAssignmentModel;
		this.classificationAttributeCode = createClassificationAttributeCode(classAttributeAssignmentModel);
		this.categoryCode = classAttributeAssignmentModel.getClassificationClass().getCode();
		this.alias = createAlias(alias);
		this.description = createDescription();
	}

	@Override
	public void setAlias(final String alias)
	{
		this.alias = createAlias(alias);
	}

	public ClassAttributeAssignmentModel getClassAttributeAssignmentModel()
	{
		return classAttributeAssignmentModel;
	}

	public String getClassificationAttributeCode()
	{
		return classificationAttributeCode;
	}

	public String getCategoryCode()
	{
		return categoryCode;
	}

	private String createClassificationAttributeCode(final ClassAttributeAssignmentModel classAttributeAssignmentModel)
	{
		final String attributeCode = classAttributeAssignmentModel.getClassificationAttribute().getCode();
		return QualifierNameUtils.removeNonAlphaNumericCharacters(attributeCode);
	}

	private String createAlias(final String alias)
	{
		return "".equals(alias) ? classificationAttributeCode : alias;
	}

	private String createDescription()
	{

		final boolean isLocalized = BooleanUtils.isTrue(classAttributeAssignmentModel.getLocalized());

		String classificationType = "";

		if (isLocalized)
		{
			classificationType = "localized:";
		}
		if (classAttributeAssignmentModel.getReferenceType() == null)
		{
			if (classAttributeAssignmentModel.getAttributeType() == ClassificationAttributeTypeEnum.ENUM)
			{
				classificationType += "ValueList";
			}
			else
			{
				classificationType += classAttributeAssignmentModel.getAttributeType().getCode();
			}
		}
		else
		{
			classificationType += classAttributeAssignmentModel.getReferenceType().getCode();
		}

		final String innerPortion;
		if (classAttributeAssignmentModel.getMultiValued())
		{
			innerPortion = String.format("Collection [%s]", classificationType);
		}
		else
		{
			innerPortion = classificationType;
		}

		return String.format("Classification [%s]", innerPortion);
	}
}

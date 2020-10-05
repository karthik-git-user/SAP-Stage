/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;

public class TestUtils
{
	public static ListItemClassificationAttributeDTO createClassificationAttributeDTO(String attributeName, String qualifier,
	                                                                                  ClassificationAttributeTypeEnum type,
	                                                                                  String category)
	{
		ClassificationAttributeModel classificationAttributeModel = new ClassificationAttributeModel();
		classificationAttributeModel.setCode(qualifier);

		ClassificationClassModel classificationClassModel = new ClassificationClassModel();
		classificationClassModel.setCode(category);

		ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setClassificationAttribute(classificationAttributeModel);
		classAttributeAssignmentModel.setAttributeType(type);
		classAttributeAssignmentModel.setClassificationClass(classificationClassModel);
		classAttributeAssignmentModel.setMultiValued(false);

		IntegrationObjectItemClassificationAttributeModel integrationObjectItemClassificationAttribute = new IntegrationObjectItemClassificationAttributeModel();
		integrationObjectItemClassificationAttribute.setAttributeName(attributeName);
		integrationObjectItemClassificationAttribute.setClassAttributeAssignment(classAttributeAssignmentModel);

		return new ListItemClassificationAttributeDTO(true, false, false, classAttributeAssignmentModel, attributeName);
	}
}

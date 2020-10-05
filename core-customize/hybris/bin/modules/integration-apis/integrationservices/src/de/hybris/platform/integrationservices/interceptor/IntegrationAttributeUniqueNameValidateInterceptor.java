/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * An interceptor that validates the {@link IntegrationObjectItemAttributeModel}'s attribute name does not appear in the
 * {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel}s of the
 * {@link IntegrationObjectItemModel} associated to the attribute.
 */
public class IntegrationAttributeUniqueNameValidateInterceptor implements ValidateInterceptor<IntegrationObjectItemAttributeModel>
{
	private static final String ERROR_MSG = "The attribute [%s] is used in an integration object item classification attribute. Please provide a different name for one of the [%s] attributes.";

	@Override
	public void onValidate(final IntegrationObjectItemAttributeModel attributeModel, final InterceptorContext interceptorContext) throws InterceptorException
	{
		if (isDuplicateOfClassificationAttributeName(attributeModel))
		{
			final String attributeName = attributeModel.getAttributeName();
			throw new InterceptorException(String.format(ERROR_MSG, attributeName, attributeName), this);
		}

	}

	private boolean isDuplicateOfClassificationAttributeName(final IntegrationObjectItemAttributeModel attributeModel)
	{
		final IntegrationObjectItemModel item = attributeModel.getIntegrationObjectItem();
		return item != null && item.getClassificationAttributes() != null &&
				item.getClassificationAttributes().stream()
						.anyMatch(attr -> attr.getAttributeName().equals(attributeModel.getAttributeName()));
	}
}

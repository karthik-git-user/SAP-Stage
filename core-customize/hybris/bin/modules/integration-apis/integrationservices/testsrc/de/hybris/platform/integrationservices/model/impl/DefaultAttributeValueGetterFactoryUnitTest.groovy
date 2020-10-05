/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.classification.ClassificationService
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultAttributeValueGetterFactoryUnitTest extends Specification {

    def factory = new DefaultAttributeValueGetterFactory(
            modelService: Stub(ModelService),
            classificationService: Stub(ClassificationService)
    )

    @Test
    @Unroll
    def "create with #descriptor type attribute descriptor returns #valueGetterType value getter"() {
        when:
        def getter = factory.create descriptor

        then:
        valueGetterType.isInstance getter

        where:
        descriptor                                  | valueGetterType
        Stub(DefaultTypeAttributeDescriptor)        | StandardAttributeValueGetter
        Stub(ClassificationTypeAttributeDescriptor) | ClassificationAttributeValueGetter
        null                                        | NullAttributeValueGetter
    }
}

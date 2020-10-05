/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class StandardAttributeValueSetterUnitTest extends Specification {
    def attributeDescriptor = Stub TypeAttributeDescriptor
    def modelService = Mock ModelService
    def attributeValueSetter = new StandardAttributeValueSetter(attributeDescriptor, modelService)

    @Test
    @Unroll
    def "throws exception when instantiated with null #paramName"() {
        when:
        new StandardAttributeValueSetter(descriptor, service)

        then:
        def e = thrown IllegalArgumentException
        e.message.toLowerCase().contains paramName

        where:
        paramName              | descriptor                    | service
        'attribute descriptor' | null                          | Stub(ModelService)
        'model service'        | Stub(TypeAttributeDescriptor) | null
    }

    @Test
    def 'uses attribute qualifier when setting attribute value'() {
        given: 'attribute name differs from attribute qualifier'
        attributeDescriptor.getAttributeName() >> 'name'
        attributeDescriptor.getQualifier() >> 'qualifier'
        and: 'given a non-null item & value'
        def item = Stub(Object)
        def value = Stub(Object)

        when:
        attributeValueSetter.setValue(item, value)

        then:
        1 * modelService.setAttributeValue(item, 'qualifier', value)
    }
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import spock.lang.Specification

@UnitTest
class DefaultAtomicType2MapPopulatorUnitTest extends Specification {
    private static final def ATTR_NAME = "codex"
    private static final def QUALIFIER = "qualifier"
    private static final def LOCALE = Locale.getDefault()
    private static final def TEST_VALUE = "testValue"

    def converter = Stub(Converter)
    def populator = new DefaultAtomicType2MapPopulator(converter: converter);

    private Map<String, Object> targetMap = [:]
    private def attributeDescriptor = Stub(TypeAttributeDescriptor) {
        getAttributeName() >> ATTR_NAME
        getQualifier() >> QUALIFIER
    }
    private def itemModel = Stub(ItemModel)

    def cleanup() {
        Locale.setDefault(LOCALE)
    }

    @Test
    def "populates to map for primitive attribute for type #attributeType"() {
        given:
        attributeIsPrimitive()
        and:
        attributeHasValue(TEST_VALUE)
        and:
        converter.convert(TEST_VALUE) >> TEST_VALUE

        when:
        populator.populate(conversionContext(attributeDescriptor), targetMap)

        then:
        targetMap == [(ATTR_NAME): TEST_VALUE]
    }

    @Test
    def "does not populate when attribute value is null"() {
        given:
        attributeIsPrimitive()
        and:
        attributeHasValue(null)

        when:
        populator.populate(conversionContext(attributeDescriptor), targetMap)

        then:
        targetMap.isEmpty()
    }

    @Test
    def "does not populate when attribute is not primitive"() {
        given:
        attributeIsNotPrimitive()

        when:
        populator.populate(conversionContext(attributeDescriptor), targetMap)

        then:
        targetMap.isEmpty()
    }

    private ItemToMapConversionContext conversionContext(TypeAttributeDescriptor attribute) {
        Stub(ItemToMapConversionContext) {
            getItemModel() >> itemModel
            getTypeDescriptor() >>
                    Stub(TypeDescriptor) {
                        getAttributes() >> [attribute]
                    }
        }
    }

    def attributeIsPrimitive() {
        attributeDescriptor.isPrimitive() >> true
    }

    def attributeIsNotPrimitive() {
        attributeDescriptor.isPrimitive() >> false
    }

    def attributeHasValue(Object value) {
        attributeDescriptor.accessor() >> Stub(AttributeValueAccessor) {
            getValue(itemModel) >> value
        }
    }
}
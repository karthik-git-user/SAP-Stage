/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.errors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.populator.AttributePersistenceException
import de.hybris.platform.inboundservices.persistence.populator.MissingRequiredAttributeValueException
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataErrorContext
import org.junit.Test
import spock.lang.Specification

@UnitTest
class MissingRequiredAttributeValueExceptionContextPopulatorUnitTest extends Specification {
    def contextPopulator = new MissingRequiredAttributeValueExceptionContextPopulator()

    @Test
    def 'does not populates context values if context does not contain MissingRequiredAttributeValueException'() {
        given:
        def context = new ODataErrorContext(exception: new AttributePersistenceException('message [%s %s]', Stub(TypeAttributeDescriptor), Stub(PersistenceContext)))

        when:
        contextPopulator.populate context

        then:
        with(context) {
            !httpStatus
            !errorCode
            !message
            !locale
            !innerError
        }
    }

    @Test
    def "provides error context values for MissingRequiredAttributeValueException"() {
        given:
        def itemKey = 'item-key'
        def attributeName = 'attrName'
        def itemCode = 'testItemCode'
        def attributeDesc = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attributeName
            getTypeDescriptor() >> Stub(TypeDescriptor) {
                getItemCode() >> itemCode
            }
        }
        def persistenceContext = Stub(PersistenceContext) {
            getIntegrationItem() >> Stub(IntegrationItem) {
                getIntegrationKey() >> itemKey
            }
        }


        def ex = new MissingRequiredAttributeValueException(attributeDesc, persistenceContext)
        def context = new ODataErrorContext(exception: ex)

        when:
        contextPopulator.populate context

        then:
        with(context) {
            httpStatus == HttpStatusCodes.BAD_REQUEST
            locale == Locale.ENGLISH
            errorCode == 'missing_property'
            message == "Property [$attributeName] is required for EntityType [$itemCode]."
            innerError == itemKey
        }
    }
}

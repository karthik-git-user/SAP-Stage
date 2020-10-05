/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class IntegrationAttributeUniqueNameValidateInterceptorUnitTest extends Specification {

    def interceptor = new IntegrationAttributeUniqueNameValidateInterceptor()

    @Test
    @Unroll
    def 'no exception is thrown when #condition'() {
        given:
        def attrName = 'uniqueName'
        def attribute = attribute attrName, intObjItem

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        noExceptionThrown()

        where:
        condition                                     | intObjItem
        'integration object item is null'             | null
        'classification attribute collection is null' | Stub(IntegrationObjectItemModel) { getClassificationAttributes() >> null }
        'attribute names are unique'                  | integrationObjectItem(['className1', 'className2'])
    }

    @Test
    def "exception is thrown when attribute name is not unique"() {
        given:
        def attrName = 'duplicateName'
        def attribute = attribute attrName, integrationObjectItem(['className1', attrName, 'className2'])

        when:
        interceptor.onValidate attribute, Stub(InterceptorContext)

        then:
        def e = thrown InterceptorException
        e.message.contains "The attribute [$attrName] is used in an integration object item classification attribute. Please provide a different name for one of the [$attrName] attributes."
    }

    def attribute(String attrName, IntegrationObjectItemModel intObjectItem) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> attrName
            getIntegrationObjectItem() >> intObjectItem
        }
    }

    def integrationObjectItem(List classAttrNames) {
        Stub(IntegrationObjectItemModel) {
            getClassificationAttributes() >> classAttrNames.collect { attrName ->
                Stub(IntegrationObjectItemClassificationAttributeModel) { getAttributeName() >> attrName }
            }
        }
    }
}

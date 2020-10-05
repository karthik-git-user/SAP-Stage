/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import org.junit.Test
import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultConsumedDestinationNotFoundDecoratorUnitTest extends Specification {

    def decorator = new DefaultConsumedDestinationNotFoundDecorator()

    @Test
    @Unroll
    def "decorator creates http entity when destination is #destination"() {
        given:
        def execution = Mock(DecoratorExecution)
        def context = Stub(DecoratorContext) {
            getDestinationModel() >> destination
        }

        when:
        decorator.decorate(new HttpHeaders(), [:], context, execution)

        then:
        1 * execution.createHttpEntity(_ as HttpHeaders, _ as Map, context)

        where:
        destination << [null, Stub(ConsumedDestinationModel)]
    }

    @Test
    def 'decorator throws exception when the destination is not found'() {
        given:
        def id = 'non-existing-destination'
        def destination = new ConsumedDestinationNotFoundModel(id)

        and:
        def context = Stub(DecoratorContext) {
            getDestinationModel() >> destination
        }

        when:
        decorator.decorate(new HttpHeaders(), [:], context, Stub(DecoratorExecution))

        then:
        def e = thrown ModelNotFoundException
        e.message == "Provided destination '$id' was not found."
    }
}

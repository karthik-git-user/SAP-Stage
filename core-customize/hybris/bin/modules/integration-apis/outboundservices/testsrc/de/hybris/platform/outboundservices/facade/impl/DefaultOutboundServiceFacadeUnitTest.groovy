/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.facade.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration
import de.hybris.platform.outboundservices.decorator.DecoratorContext
import de.hybris.platform.outboundservices.decorator.DecoratorExecution
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultOutboundServiceFacadeUnitTest extends Specification {

    private static final String ENDPOINT_URL = "http://my.consumed.destination/some/path"
    private static final String DESTINATION = 'destination'
    private static final String ITEM_TYPE = 'MyType'
    private static final String IO_CODE = 'integrationObjectCode'
    private static final String IOI_CODE = 'integrationObjectItemCode'

    @Shared
    def itemModel = Stub(ItemModel) {
        getItemtype() >> ITEM_TYPE
    }
    def consumedDestination = Stub(ConsumedDestinationModel) {
        getUrl() >> ENDPOINT_URL
    }
    def flexibleSearchService = Stub(FlexibleSearchService) {
        getModelByExample(_ as ConsumedDestinationModel) >> consumedDestination
    }
    def restTemplate = Mock RestTemplate
    def integrationRestTemplateFactory = Stub(IntegrationRestTemplateFactory) {
        create(_ as ConsumedDestinationModel) >> restTemplate
    }
    @Shared
    def integrationObjectService = Stub(IntegrationObjectService) {
        findIntegrationObjectItemByTypeCode(IO_CODE, ITEM_TYPE) >> Stub(IntegrationObjectItemModel) {
            getCode() >> IOI_CODE
        }
    }
    def monitoringDecorator = Mock OutboundRequestDecorator
    def outboundServicesConfiguration = Stub(OutboundServicesConfiguration) {
        isMonitoringEnabled() >> true
    }
    def decorator1 = Mock OutboundRequestDecorator
    def decorator2 = Mock OutboundRequestDecorator
    def facade = new DefaultOutboundServiceFacade(
            decorators: [decorator1, decorator2],
            flexibleSearchService: flexibleSearchService,
            integrationObjectService: integrationObjectService,
            integrationRestTemplateFactory: integrationRestTemplateFactory,
            monitoringDecorator: monitoringDecorator,
            outboundServicesConfiguration: outboundServicesConfiguration,
    )

    @Test
    @Unroll
    def "send payload to destination with monitoring enabled and integration object item #condition"() {
        given: "integration object item #condition"
        facade.integrationObjectService = intObjService

        when:
        facade.send(itemModel, IO_CODE, DESTINATION).subscribe()

        then:
        1 * monitoringDecorator.decorate(_, _, _, _) >> {
            HttpHeaders httpHeaders, Map payload, DecoratorContext context, DecoratorExecution execution ->
                assert httpHeaders.isEmpty()
                assert payload == [:]
                with(context) {
                    integrationObjectCode == IO_CODE
                    integrationObjectItemCode == ioCode
                    itemModel == itemModel
                    destinationModel == consumedDestination
                }; Stub(HttpEntity)
        }
        1 * restTemplate.postForEntity(ENDPOINT_URL, _, Map.class)

        where:
        condition                  | intObjService                                                                                          | ioCode
        'model is found'           | integrationObjectService                                                                               | IOI_CODE
        'model is not found'       | findIntegrationObjectItemWithException(new ModelNotFoundException('IGNORE - testing exception'))       | null
        'has ambiguous identifier' | findIntegrationObjectItemWithException(new AmbiguousIdentifierException('IGNORE - testing exception')) | null
    }

    @Test
    def 'send payload to destination with monitoring disabled then monitoring decorator is not invoked'() {
        given:
        facade.outboundServicesConfiguration = Stub(OutboundServicesConfiguration) {
            isMonitoringEnabled() >> false
        }

        when:
        facade.send(itemModel, IO_CODE, DESTINATION).subscribe()

        then:
        0 * monitoringDecorator._
    }

    @Test
    @Unroll
    def "send throws exception when #condition"() {
        when:
        facade.send(item, ioCode, destination)

        then:
        def ex = thrown IllegalArgumentException
        ex.message == message

        where:
        condition              | item      | ioCode  | destination | message
        'item model is null'   | null      | IO_CODE | DESTINATION | 'itemModel cannot be null'
        'ioCode is empty'      | itemModel | ""      | DESTINATION | 'integrationObjectCode cannot be null or empty'
        'ioCode is null'       | itemModel | null    | DESTINATION | 'integrationObjectCode cannot be null or empty'
        'destination is empty' | itemModel | IO_CODE | ""          | 'destination cannot be null or empty'
        'destination is null'  | itemModel | IO_CODE | null        | 'destination cannot be null or empty'
    }

    @Test
    def 'send throws exception when destination is not found'() {
        given: 'destination not found'
        facade.flexibleSearchService = Stub(FlexibleSearchService) {
            getModelByExample(_ as ConsumedDestinationModel) >> {
                throw new ModelNotFoundException('IGNORE - testing exception')
            }
        }

        when:
        facade.send(itemModel, IO_CODE, DESTINATION)

        then:
        def ex = thrown ModelNotFoundException
        ex.message == 'Provided destination was not found.'

        and: 'monitoring decorator is initialized'
        1 * monitoringDecorator.decorate(_, _, _, _) >> {
            HttpHeaders httpHeaders, Map payload, DecoratorContext context, DecoratorExecution execution ->
                assert httpHeaders.isEmpty()
                assert payload == [:]
                with(context) {
                    integrationObjectCode == IO_CODE
                    integrationObjectItemCode == IOI_CODE
                    itemModel == itemModel
                    destinationModel.url == "Destination '$DESTINATION' was not found."
                }; Stub(HttpEntity)
        }
    }

    @Test
    def 'send fails when creating rest template throws an exception'() {
        given: 'creating rest template throws exception'
        facade.integrationRestTemplateFactory = Stub(IntegrationRestTemplateFactory) {
            create(_ as ConsumedDestinationModel) >> {
                throw new RuntimeException('IGNORE - testing exception')
            }
        }

        when:
        facade.send(itemModel, IO_CODE, DESTINATION)

        then:
        thrown RuntimeException
        
        and: 'monitoring decorator is initialized'
        1 * monitoringDecorator.decorate(_, _, _, _) >> {
            HttpHeaders httpHeaders, Map payload, DecoratorContext context, DecoratorExecution execution ->
                assert httpHeaders.isEmpty()
                assert payload == [:]
                with(context) {
                    integrationObjectCode == IO_CODE
                    integrationObjectItemCode == IOI_CODE
                    itemModel == itemModel
                    destinationModel == consumedDestination
                }; Stub(HttpEntity)
        }
    }

    def findIntegrationObjectItemWithException(def ex) {
        Stub(IntegrationObjectService) {
            findIntegrationObjectItemByTypeCode(IO_CODE, ITEM_TYPE) >> {
                throw ex
            }
        }
    }
}
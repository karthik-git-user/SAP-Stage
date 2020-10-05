/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.*
import de.hybris.platform.cronjob.model.TriggerModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2webservices.odata.IntegrationODataResponse
import de.hybris.platform.odata2webservices.odata.ODataFacade
import de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.outboundsync.model.*
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.junit.Test

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.findAll
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.findAny
import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class OutboundChannelConfigIntegrationTest extends ServicelayerTransactionalSpockSpecification {
    private static final String SERVICE_NAME = "OutboundChannelConfig"
    private static final String DESTINATION_TARGET_ID = "ConsumedDestinationTest"

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade

    def setup() {
        importCsv '/impex/essentialdata-outboundsync-setup.impex', 'UTF-8'
    }

    def cleanup() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll OutboundChannelConfigurationModel
        IntegrationTestUtil.removeAll OutboundSyncStreamConfigurationContainerModel
        IntegrationTestUtil.removeAll OutboundSyncStreamConfigurationModel
        IntegrationTestUtil.removeAll OutboundSyncJobModel
        IntegrationTestUtil.removeAll OutboundSyncCronJobModel
        IntegrationTestUtil.removeAll ConsumedDestinationModel
        IntegrationTestUtil.removeAll EndpointModel
        IntegrationTestUtil.removeAll DestinationTargetModel
        IntegrationTestUtil.removeAll BasicCredentialModel
        IntegrationTestUtil.removeAll ConsumedOAuthCredentialModel
        IntegrationTestUtil.removeAll TriggerModel
    }

    @Test
    def "Get objects from OutboundSyncStreamConfiguration"() {
        given: "a ConsumedDestination, Channel, Stream Container and Stream"
        def consumedDestinationId = 'Dest1'
        def destinationTargetId = 'DestTarget1'
        def streamContainerId = 'streamContainerIdB'
        def streamId = 'streamIdB'
        def endpointId = 'local-hybris'
        def endpointVersion = 'unknown'
        def basicCredentialId = 'testBasicCredential'
        def channelCode = 'channelCodeID'

        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                "                      ; $endpointId         ; $endpointVersion       ; local-hybris ; https://localhost:9002",
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                "                               ; $destinationTargetId",
                'INSERT_UPDATE BasicCredential; id[unique = true]   ; username; password',
                "                             ; $basicCredentialId  ; aaa     ; bbb",
                'INSERT_UPDATE ConsumedDestination; id[unique = true]             ; url                    ; endpoint(id, version)       ; credential(id)     ; destinationTarget(id)',
                "                                 ; $consumedDestinationId        ; https://localhost:9002 ; $endpointId:$endpointVersion; $basicCredentialId ; $destinationTargetId",
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                '                                                    ; OutboundProduct; INBOUND',
                'INSERT_UPDATE OutboundChannelConfiguration; code[unique = true] ; integrationObject(code); destination(id)',
                "                                          ; $channelCode        ; OutboundProduct        ; $consumedDestinationId",
                'INSERT_UPDATE OutboundSyncStreamConfigurationContainer; id[unique = true]',
                "                                                      ; $streamContainerId",
                'INSERT_UPDATE OutboundSyncStreamConfiguration; streamId[unique = true]; container(id)                ; itemTypeForStream(code); outboundChannelConfiguration(code); whereClause',
                "                                             ; $streamId              ; $streamContainerId           ; Product                ; $channelCode"
        )

        def context = oDataGetContext "OutboundSyncStreamConfigurations",
                ['$expand':
                         '''
                            outboundChannelConfiguration, 
                            container, 
                            outboundChannelConfiguration/destination, 
                            outboundChannelConfiguration/destination/destinationTarget, 
                            outboundChannelConfiguration/destination/endpoint, 
                            outboundChannelConfiguration/destination/credentialBasic
                        '''
                ]

        when:
        def response = facade.handleRequest context
        def getBody = extractBody(response as IntegrationODataResponse)

        then:
        getBody.getCollectionOfObjects("d.results").size() == 1
        getBody.getString('d.results[0].streamId') == streamId
        getBody.getString('d.results[0].outboundChannelConfiguration.code') == channelCode
        getBody.getString('d.results[0].outboundChannelConfiguration.destination.destinationTarget.id') == destinationTargetId
        getBody.getString('d.results[0].outboundChannelConfiguration.destination.endpoint.id') == endpointId
        getBody.getString('d.results[0].outboundChannelConfiguration.destination.endpoint.version') == endpointVersion
        getBody.getString('d.results[0].outboundChannelConfiguration.destination.id') == consumedDestinationId
        getBody.getString('d.results[0].outboundChannelConfiguration.destination.credentialBasic.id') == basicCredentialId
        getBody.getString('d.results[0].container.id') == streamContainerId
    }

    @Test
    def "Create ConsumedDestination and auto generate multiple streams with single POST to OutboundChannelConfigurations"() {
        given:
        def integrationObjectCode = 'MultiStreamIO'
        and: 'An IntegrationObject exists with 2 Items that have paths back to the root Item'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]    ; integrationType(code)',
                "                               ; $integrationObjectCode ; INBOUND",
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)    ; root',
                "                                   ; $integrationObjectCode                ; Category           ; Category      ; true",
                "                                   ; $integrationObjectCode                ; CatalogVersion     ; CatalogVersion; false",
                ' INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code)',
                "                                             ; $integrationObjectCode:CatalogVersion                              ; rootCategories              ; CatalogVersion:rootCategories                      ; $integrationObjectCode:Category",
                "                                             ; $integrationObjectCode:CatalogVersion                              ; version                     ; CatalogVersion:version                             ;",
                "                                             ; $integrationObjectCode:Category                                    ; code                        ; Category:code                                      ;")

        and: 'a channel configuration payload with autogenerate = true'
        def consumedDestinationId = 'destinationIdC'
        def channelId = 'channelIdC'
        def apiOAuthCredential = 'oAuthCredential1'
        def apiBasicCredential = 'basicCredential1'
        def endpointID = 'endpointID'
        def targetID = 'targetID'
        def channelConfigurationPayload = json()
                .withField('code', channelId)
                .withField('integrationObject', json()
                        .withField('code', integrationObjectCode))
                .withField('destination', json()
                        .withId(consumedDestinationId)
                        .withField('url', "https://localhost:9002/odata2webservices/OutboundProduct/Products")
                        .withField('endpoint', json()
                                .withId(endpointID)
                                .withField('name', endpointID)
                                .withField('specUrl', 'specURL')
                                .withField('version', 'endpointVersion'))
                        .withField('destinationTarget', json()
                                .withId(targetID))
                        .withField('credentialConsumedOAuth', json()
                                .withId(apiOAuthCredential)
                                .withField('clientId', 'foo')
                                .withField('clientSecret', 'bar'))
                        .withField('credentialBasic', json()
                                .withId(apiBasicCredential)
                                .withField('username', 'foo')
                                .withField('password', 'bar'))
                        .withFieldValues('additionalProperties', mapEntry('cat', 'Tom'), mapEntry('mouse', 'Jerry')))
                .withField('autoGenerate', true)
                .build()

        when: 'the payload is posted'
        facade.handleRequest oDataPostContext("OutboundChannelConfigurations", channelConfigurationPayload)

        then: 'We expect the following models to exist'
        findAny(OutboundChannelConfigurationModel, { it.code == channelId }).present
        findAny(OutboundSyncStreamConfigurationContainerModel, { it.id.contains(channelId) }).present
        findAll(OutboundSyncStreamConfigurationModel, {it.streamId.contains(channelId) }).size() == 2
        findAny(OutboundSyncJobModel, { it.code.contains(channelId) }).present
        findAny(OutboundSyncCronJobModel, { it.code.contains(channelId) }).present
        findAny(BasicCredentialModel, { it.id == apiBasicCredential }).present
        findAny(ConsumedOAuthCredentialModel, { it.id == apiOAuthCredential }).present
        findAny(ConsumedDestinationModel, { it.id == consumedDestinationId }).present
        findAny(EndpointModel, { it.id == endpointID }).present
        findAny(DestinationTargetModel, { it.id == targetID }).present
    }

    @Test
    def 'Patch OutboundSyncStreamConfiguration and nested objects'() {
        given:
        def consumedDestinationId = 'Dest1'
        def destinationTargetId = 'DestTarget1'
        def streamContainerId = 'streamContainerIdB'
        def streamId = 'streamIdB'
        def endpointId = 'local-hybris'
        def endpointVersion = 'unknown'
        def basicCredentialId = 'testBasicCredential'
        def channelCode = 'channelCodeID'

        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                "                      ; $endpointId         ; $endpointVersion       ; local-hybris ; https://localhost:9002",
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                "                               ; $destinationTargetId",
                'INSERT_UPDATE BasicCredential; id[unique = true]   ; username; password',
                "                             ; $basicCredentialId  ; aaa     ; bbb",
                'INSERT_UPDATE ConsumedDestination; id[unique = true]             ; url                    ; endpoint(id, version)       ; credential(id)     ; destinationTarget(id)',
                "                                 ; $consumedDestinationId        ; https://localhost:9002 ; $endpointId:$endpointVersion; $basicCredentialId ; $destinationTargetId",
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                '                                                    ; OutboundProduct; INBOUND',
                'INSERT_UPDATE OutboundChannelConfiguration; code[unique = true] ; integrationObject(code); destination(id)',
                "                                          ; $channelCode        ; OutboundProduct        ; $consumedDestinationId",
                'INSERT_UPDATE OutboundSyncStreamConfigurationContainer; id[unique = true]',
                "                                                      ; $streamContainerId",
                'INSERT_UPDATE OutboundSyncStreamConfiguration; streamId[unique = true]; container(id)                ; itemTypeForStream(code); outboundChannelConfiguration(code); whereClause',
                "                                             ; $streamId              ; $streamContainerId           ; Product                ; $channelCode"
        )

        and: 'the URL and itemTypeForStream is updated with patch'
        def newUrl = 'https://differentLocalHost'
        def newItemTypeCode = 'Catalog'
        def content = json()
                .withField('itemTypeForStream', json()
                        .withCode('Catalog'))
                .withField('outboundChannelConfiguration', json()
                        .withCode(channelCode)
                        .withField('destination', json()
                                .withId(consumedDestinationId)
                                .withField('url', newUrl)
                                .withField('destinationTarget', json()
                                        .withId(destinationTargetId)
                                )
                        )
                )
        when:
        def response = facade.handleRequest patch(SERVICE_NAME,'OutboundSyncStreamConfigurations',streamId, content)

        then: 'the response is successful'
        response.status == HttpStatusCodes.OK
        and: 'the stream exists with its updated fields'
        findAny(OutboundSyncStreamConfigurationModel, { it.streamId == streamId })
                .filter({ it.outboundChannelConfiguration.destination.url == newUrl })
                .filter({ it.itemTypeForStream.code == newItemTypeCode })
                .present
    }

    @Test
    def "Get trigger through outboundsync cronjob"() {
        given:
        def integrationObjectCode = 'IntegrationObject'
        def consumedDestinationId = 'destinationId'
        def channelId = 'channelId'
        and: 'Integration object is created'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]    ; integrationType(code)',
                "                               ; $integrationObjectCode ; INBOUND")
        and: 'Consumed destination is created'
        createConsumedDestination(consumedDestinationId)
        and: 'Outbound channel configuration and other required components are created'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE OutboundChannelConfiguration; code[unique = true]; integrationObject(code); destination(id, destinationTarget(id))        ; autoGenerate',
                "                                          ; $channelId         ; $integrationObjectCode ; $consumedDestinationId:$DESTINATION_TARGET_ID ; true")
        and: 'Trigger is created'
        String cronJobCode = "${channelId}CronJob"
        String cronExpression = "0 0 0 * * ?"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Trigger; cronJob(code)[unique = true]; cronExpression;',
                "                     ; $cronJobCode                ; $cronExpression")

        when:
        def response = facade.handleRequest oDataGetContext("OutboundSyncCronJobs", ['$expand': 'triggers/cronJob'])

        then: 'Response body contains triggers via the OutboundSynCronJob'
        def getBody = extractBody(response as IntegrationODataResponse)
        getBody.getCollectionOfObjects('d.results[*].triggers.results[*].cronJob.code').containsAll(cronJobCode)
        getBody.getCollectionOfObjects('d.results[*].triggers.results[*].cronExpression').containsAll(cronExpression)
    }

    @Test
    def "Get all ConsumedDestinations returns ConsumedDestinations with both basic and oauth credentials"() {
        given: '2 ConsumedDestinations exist with different ids'
        def basicConsumedDestinationId = "BasicCredential-ConsumedDestination"
        def oAuthConsumedDestinationId = "ConsumedOAuthCredential-ConsumedDestination"
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                '                      ; local-hybris        ; unknown                ; local-hybris ; https://localhost:9002',
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                '                               ; ConsumedDestinationTest',
                'INSERT_UPDATE BasicCredential; id[unique = true]   ; username; password',
                '                             ; testBasicCredential ; aaa   ; bbb',
                'INSERT_UPDATE ConsumedOAuthCredential; id[unique = true]   ; clientId; clientSecret',
                '                                     ; testOAuthCredential ; foo   ; bar',
                'INSERT_UPDATE ConsumedDestination; id[unique = true]             ; url                   ; endpoint(id, version); credential(id)     ; destinationTarget(id)',
                "                                 ; $basicConsumedDestinationId   ; https://localhost:9002; local-hybris:unknown ; testBasicCredential; ConsumedDestinationTest",
                "                                 ; $oAuthConsumedDestinationId   ; https://localhost:9002; local-hybris:unknown ; testOAuthCredential; ConsumedDestinationTest"
        )

        def context = oDataGetContext("ConsumedDestinations")

        when:
        def response = facade.handleRequest(context)
        def getBody = extractBody(response as IntegrationODataResponse)

        then:
        getBody.getCollectionOfObjects("d.results").size() == 2
        getBody.getCollectionOfObjects('d.results[*].id').containsAll(basicConsumedDestinationId, oAuthConsumedDestinationId)
    }

    private static createConsumedDestination(final String id) {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Endpoint; id[unique = true]   ; version[unique = true] ; name         ; specUrl',
                '                      ; local-hybris        ; unknown                ; local-hybris ; https://localhost:9002',
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                "                               ; $DESTINATION_TARGET_ID",
                'INSERT_UPDATE BasicCredential; id[unique = true]   ; username; password',
                '                             ; testBasicCredential ; aaa     ; bbb',
                'INSERT_UPDATE ConsumedDestination; id[unique = true]             ; url                    ; endpoint(id, version); credential(id)     ; destinationTarget(id)',
                "                                 ; $id                           ; https://localhost:9002 ; local-hybris:unknown ; testBasicCredential; $DESTINATION_TARGET_ID"
        )
    }

    private static JsonBuilder mapEntry(String key, String value) {
        json().withField('key', key).withField('value', value)
    }

    static ODataContext patch(String serviceName, String entitySet, String key, JsonBuilder body) {
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPatchRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(serviceName)
                        .withEntitySet(entitySet)
                        .withEntityKeys(key))
                .withContentType('application/json')
                .withAccepts('application/json')
                .withBody(body)
    }

    ODataContext oDataGetContext(String entitySetName) {
        oDataGetContext(entitySetName, [:])
    }

    ODataContext oDataGetContext(String entitySetName, Map params) {
        def request = ODataRequestBuilder.oDataGetRequest()
                .withAccepts(APPLICATION_JSON_VALUE)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(entitySetName))
                .withParameters(params)
                .build()
        contextGenerator.generate request
    }

    ODataContext oDataPostContext(String entitySetName, String body) {
        def request = ODataRequestBuilder.oDataPostRequest()
                .withBody(body)
                .withAccepts(APPLICATION_JSON_VALUE)
                .withContentType(APPLICATION_JSON_VALUE)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(SERVICE_NAME)
                        .withEntitySet(entitySetName))
                .build()
        contextGenerator.generate request
    }

    JsonObject extractBody(IntegrationODataResponse response) {
        JsonObject.createFrom response.entityAsStream
    }
}
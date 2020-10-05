/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ODataIntegrationKeyValueIntegrationTest extends ServicelayerSpockSpecification {
    private static final def SERVICE_NAME = 'IntegrationService'

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade
    @Resource(name = "oDataSchemaGenerator")
    private SchemaGenerator generator

    def setup() {
        importCsv("/impex/essentialdata-integrationservices.impex", "UTF-8")
    }

    def cleanup() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    @Test
    def "GET IntegrationObjectItemAttributes include the expected integrationKeys"() {
        given: "A large page size to make sure the first page will include all attributes that we expect"
        def params = ['\$top': '1000']
        def context = oDataGetContext("IntegrationObjectItemAttributes", params)

        when:
        ODataResponse response = facade.handleRequest(context)

        then:
        def json = extractEntitiesFrom response
        json.getCollectionOfObjects("d.results").size() == 34
        json.getCollectionOfObjects('d.results[*].integrationKey').containsAll(
                'code|IntegrationObject|IntegrationService',
                'integrationType|IntegrationObject|IntegrationService',
                'items|IntegrationObject|IntegrationService',
                'code|IntegrationObjectItem|IntegrationService',
                'type|IntegrationObjectItem|IntegrationService',
                'itemTypeMatch|IntegrationObjectItem|IntegrationService',
                'integrationObject|IntegrationObjectItem|IntegrationService',
                'root|IntegrationObjectItem|IntegrationService',
                'attributes|IntegrationObjectItem|IntegrationService',
                'classificationAttributes|IntegrationObjectItem|IntegrationService',
                'attributeName|IntegrationObjectItemAttribute|IntegrationService',
                'returnIntegrationObjectItem|IntegrationObjectItemAttribute|IntegrationService',
                'attributeDescriptor|IntegrationObjectItemAttribute|IntegrationService',
                'unique|IntegrationObjectItemAttribute|IntegrationService',
                'autoCreate|IntegrationObjectItemAttribute|IntegrationService',
                'integrationObjectItem|IntegrationObjectItemAttribute|IntegrationService',
                'attributeName|IntegrationObjectItemClassificationAttribute|IntegrationService',
                'classAttributeAssignment|IntegrationObjectItemClassificationAttribute|IntegrationService',
                'integrationObjectItem|IntegrationObjectItemClassificationAttribute|IntegrationService',
                'returnIntegrationObjectItem|IntegrationObjectItemClassificationAttribute|IntegrationService',
                'id|ClassificationSystem|IntegrationService',
                'catalog|ClassificationSystemVersion|IntegrationService',
                'version|ClassificationSystemVersion|IntegrationService',
                'classificationClass|ClassAttributeAssignment|IntegrationService',
                'classificationAttribute|ClassAttributeAssignment|IntegrationService',
                'code|ClassificationClass|IntegrationService',
                'catalogVersion|ClassificationClass|IntegrationService',
                'code|ClassificationAttribute|IntegrationService',
                'systemVersion|ClassificationAttribute|IntegrationService',
                'qualifier|AttributeDescriptor|IntegrationService',
                'enclosingType|AttributeDescriptor|IntegrationService',
                'code|ComposedType|IntegrationService',
                'code|IntegrationType|IntegrationService',
                'code|ItemTypeMatchEnum|IntegrationService'
        )
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

    def extractEntitiesFrom(ODataResponse response) {
        extractBodyWithExpectedStatus(response, HttpStatusCodes.OK)
    }

    def extractBodyWithExpectedStatus(ODataResponse response, HttpStatusCodes expStatus) {
        assert response.getStatus() == expStatus
        JsonObject.createFrom response.getEntity() as InputStream
    }
}

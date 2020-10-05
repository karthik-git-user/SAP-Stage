/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.classification.*
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.featuretoggle.FeatureToggler
import de.hybris.platform.integrationservices.util.featuretoggle.IntegrationApiFeature
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.junit.Test
import spock.lang.Requires

import javax.ws.rs.client.Entity
import javax.ws.rs.core.Response

@NeedsEmbeddedServer(webExtensions = Odata2webservicesConstants.EXTENSIONNAME)
@IntegrationTest
class PatchClassificationsIntegrationTest extends ServicelayerSpockSpecification {

    private static final def USER = 'tester'
    private static final def PASSWORD = 'secret'
    private static final def SYSTEM = 'Electronics'
    private static final def VERSION = 'Test'
    private static final def SYSTEM_VERSION = "$SYSTEM:$VERSION"
    private static final def TEST_IO = 'ClassifiedIO'

    def setup() {
        importCsv("/impex/essentialdata-odata2services.impex", "UTF-8") // For the integrationadmingroup
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)           ; @password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                "                      ; $USER             ; integrationcreategroup; *:$PASSWORD")
    }

    def cleanup() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll ClassAttributeAssignmentModel
        IntegrationTestUtil.removeAll ClassificationAttributeModel
        IntegrationTestUtil.removeAll ClassificationAttributeUnitModel
        IntegrationTestUtil.removeAll ClassificationClassModel
        IntegrationTestUtil.removeAll ClassificationSystemModel
    }

    @Test
    def 'patching an integration object with classification attributes is not implemented'() {
        given:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE ClassificationSystem; id[unique = true]',
                "                                  ; $SYSTEM",
                'INSERT_UPDATE ClassificationSystemVersion; catalog(id)[unique = true]; version[unique = true]',
                "                                         ; $SYSTEM                   ; $VERSION",
                '$catalogVersionHeader = catalogVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]',
                "                                 ; dimensions         ; $SYSTEM_VERSION",
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                'INSERT_UPDATE ClassificationAttributeUnit; $systemVersionHeader[unique = true]; code[unique = true]; symbol; unitType',
                "                                         ; $SYSTEM_VERSION                    ; centimeters        ; cm    ; measurement",
                'INSERT_UPDATE ClassificationAttribute; code[unique = true]; $systemVersionHeader[unique = true]',
                "                                     ; height             ; $SYSTEM_VERSION",
                '$class = classificationClass($catalogVersionHeader, code)',
                '$attribute = classificationAttribute($systemVersionHeader, code)',
                'INSERT_UPDATE ClassAttributeAssignment; $class[unique = true]     ; $attribute[unique = true]   ; unit($systemVersionHeader, code); attributeType(code); mandatory[default = false]',
                "                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:height      ; $SYSTEM_VERSION:centimeters     ; number")

        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                "                               ; $TEST_IO           ; INBOUND",

                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                "                                   ; $TEST_IO                              ; Product            ; Product",
                "                                   ; $TEST_IO                              ; CatalogVersion     ; CatalogVersion",
                "                                   ; $TEST_IO                              ; Catalog            ; Catalog",

                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                "                                            ; $TEST_IO:Catalog                                                   ; id                          ; Catalog:id",
                "                                            ; $TEST_IO:Product                                                   ; code                        ; Product:code",
                "                                            ; $TEST_IO:Product                                                   ; catalogVersion              ; Product:catalogVersion                             ; $TEST_IO:CatalogVersion",
                "                                            ; $TEST_IO:CatalogVersion                                            ; version                     ; CatalogVersion:version",
                "                                            ; $TEST_IO:CatalogVersion                                            ; catalog                     ; CatalogVersion:catalog                             ; $TEST_IO:Catalog",

                '$item = integrationObjectItem(integrationObject(code), code)',
                '$systemVersionHeader = systemVersion(catalog(id), version)',
                '$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
                '$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
                '$classificationAssignment = classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
                'INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment',
                "                                                          ; $TEST_IO:Product    ; height                      ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:height")
        
        when:
        def response = basicAuthRequest(TEST_IO)
                .path("Products('some|integration|key')")
                .build()
                .patch Entity.json()

        then:
        response.status == 501
        def json = asJson(response)
        with(json) {
            getString('\$.error.code') == 'operation_not_supported'
            getString('\$.error.message.value').contains 'PATCH'
        }
    }

    BasicAuthRequestBuilder basicAuthRequest(final String path) {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .credentials(USER, PASSWORD)
                .path(path)
                .accept("application/json")
    }
    
    JsonObject asJson(final Response response) {
        JsonObject.createFrom((InputStream) response.getEntity())
    }
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.inboundservices.util.InboundMonitoringRule
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.XmlObject
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.junit.Rule
import org.junit.Test
import spock.lang.Unroll

import javax.ws.rs.core.Response

@NeedsEmbeddedServer(webExtensions = Odata2webservicesConstants.EXTENSIONNAME)
@IntegrationTest
class GetMetadataIntegrationTest extends ServicelayerSpockSpecification {
    private static final String USER = 'tester'
    private static final String PWD = 'secret'
    private static final String IO = 'EDMX-Test'

    @Rule
    InboundMonitoringRule monitoring = InboundMonitoringRule.disabled()

    def setupSpec() {
        setupTestUser()
        setupTestIntegrationObject()
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.findAny(EmployeeModel, { it.uid == USER }).ifPresent { IntegrationTestUtil.remove it }
    }

    private static void setupTestUser() {
        importCsv("/impex/essentialdata-odata2services.impex", "UTF-8")
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)         ; @password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                "                      ; $USER             ; integrationviewgroup; *:$PWD")
    }

    private static void setupTestIntegrationObject() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                "                               ; $IO                ; INBOUND",
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                "                                   ; $IO                                   ; Catalog            ; Catalog",
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier)',
                "                                            ; $IO:Catalog                                                        ; id                          ; Catalog:id")
    }

    @Test
    def 'retrieves EDMX for /$metadata'() {
        when:
        def response = basicAuthRequest()
                .path('$metadata')
                .build()
                .get()

        then:
        response.status == 200
        def xml = extractBody response
        xml.exists '//Schema/EntityType/Key'
        xml.exists '//Schema/EntityType/Property'
        xml.exists '//EntityContainer/EntitySet'
    }

    @Test
    @Unroll
    def 'retrieves EDMX for /$metadata?Catalog='() {
        when:
        def response = basicAuthRequest()
                .path('$metadata')
                .queryParam('Catalog', '')
                .build()
                .get()

        then:
        response.status == 200
        def xml = extractBody response
        xml.exists '//Schema/EntityType/Key'
        xml.exists '//Schema/EntityType/Property'
        xml.exists '//EntityContainer/EntitySet'
    }

    BasicAuthRequestBuilder basicAuthRequest() {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .credentials(USER, PWD)
                .accept('application/xml')
                .path(IO)
    }

    XmlObject extractBody(Response response) {
        XmlObject.createFrom response.getEntity() as InputStream
    }
}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.XmlObject
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.apache.http.HttpStatus
import org.junit.Test
import spock.lang.Unroll

import javax.ws.rs.client.Entity

@NeedsEmbeddedServer(webExtensions = [Odata2webservicesConstants.EXTENSIONNAME])
@IntegrationTest
class ErrorHandlingIntegrationTest extends ServicelayerSpockSpecification {
    private static final String USER = 'tester'
    private static final String PASSWORD = 'password'

    def setupSpec() {
        importCsv '/impex/essentialdata-odata2services.impex', 'UTF-8'
        importCsv '/impex/essentialdata-odata2webservices.impex', 'UTF-8'
        importCsv '/impex/essentialdata-inboundservices.impex', 'UTF-8'
        importCsv '/impex/essentialdata-outboundservices.impex', 'UTF-8'
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    def cleanup() {
        IntegrationTestUtil.findAny(EmployeeModel, { it.uid == USER }).ifPresent { IntegrationTestUtil.remove it }
    }

    @Test
    def 'handles 401 Unauthorized in application/json content'() {
        when:
        def response = basicAuthRequest()
                .accept('application/json')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_UNAUTHORIZED
        response.getHeaderString('Content-Type') == 'application/json;charset=UTF-8'
        def json = JsonObject.createFrom response.readEntity(String)
        json.getString('error.code') == 'unauthorized'
        json.getString('error.message.lang') == 'en'
    }

    @Test
    def 'handles 401 Unauthorized in application/xml content'() {
        when:
        def response = basicAuthRequest()
                .accept('application/xml')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_UNAUTHORIZED
        response.getHeaderString('Content-Type') == 'application/xml;charset=UTF-8'
        def xml = XmlObject.createFrom response.readEntity(String)
        xml.get('/error/code') == 'unauthorized'
        xml.get('/error/message/@lang') == 'en'
    }

    @Test
    @Unroll
    def "GET /#uri: 404 Not Found in application/json content"() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)          ;$password',
                "                      ; $USER             ; integrationadmingroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest(uri)
                .credentials(USER, PASSWORD)
                .accept('application/json')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_NOT_FOUND
        response.getHeaderString('Content-Type') == 'application/json;charset=UTF-8'
        def json = JsonObject.createFrom response.readEntity(String)
        json.getString('error.code') == 'not_found'
        json.getString('error.message.lang') == 'en'

        where:
        uri << ['IntegrationService', 'IntegrationInboundMonitoring', 'IntegrationOutboundMonitoring', 'NonExistentIO']
    }

    @Test
    @Unroll
    def "GET /#uri: 404 Not Found in application/xml content"() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)          ;$password',
                "                      ; $USER             ; integrationadmingroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest(uri)
                .credentials(USER, PASSWORD)
                .accept('application/xml')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_NOT_FOUND
        response.getHeaderString('Content-Type') == 'application/xml;charset=UTF-8'
        def xml = XmlObject.createFrom response.readEntity(String)
        xml.get('/error/code') == 'not_found'
        xml.get('/error/message/@lang') == 'en'

        where:
        uri << ['IntegrationService', 'IntegrationInboundMonitoring', 'IntegrationOutboundMonitoring', 'NonExistentIO']
    }

    @Test
    @Unroll
    def "GET /#uri: 403 Forbidden in application/json content"() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)           ;$password',
                "                      ; $USER             ; integrationcreategroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest(uri)
                .credentials(USER, PASSWORD)
                .accept('application/json')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_FORBIDDEN
        response.getHeaderString('Content-Type') == 'application/json;charset=UTF-8'
        def json = JsonObject.createFrom response.readEntity(String)
        json.getString('error.code') == 'forbidden'
        json.getString('error.message.lang') == 'en'

        where:
        uri << ['IntegrationService', 'IntegrationInboundMonitoring', 'IntegrationOutboundMonitoring', 'InboundProduct']
    }

    @Test
    @Unroll
    def "GET /#uri: 403 Forbidden in application/xml content"() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)           ;$password',
                "                      ; $USER             ; integrationcreategroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest(uri)
                .credentials(USER, PASSWORD)
                .accept('application/xml')
                .build()
                .get()

        then:
        response.status == HttpStatus.SC_FORBIDDEN
        response.getHeaderString('Content-Type') == 'application/xml;charset=UTF-8'
        def xml = XmlObject.createFrom response.readEntity(String)
        xml.get('/error/code') == 'forbidden'
        xml.get('/error/message/@lang') == 'en'

        where:
        uri << ['IntegrationService', 'IntegrationInboundMonitoring', 'IntegrationOutboundMonitoring', 'InboundProduct']
    }

    @Test
    def 'POST /InboundProduct 403 Forbidden with application/json content'() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)           ;$password',
                "                      ; $USER             ; integrationviewgroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest('InboundProduct')
                .credentials(USER, PASSWORD)
                .accept('application/json')
                .build()
                .post(Entity.json('{}'))

        then:
        response.status == HttpStatus.SC_FORBIDDEN
        response.getHeaderString('Content-Type') == 'application/json;charset=UTF-8'
        def json = JsonObject.createFrom response.readEntity(String)
        json.getString('error.code') == 'forbidden'
        json.getString('error.message.lang') == 'en'
    }

    @Test
    def 'POST /InboundProduct 403 Forbidden with application/xml content'() {
        given:
        IntegrationTestUtil.importImpEx(
                '$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid)           ;$password',
                "                      ; $USER             ; integrationviewgroup;*:$PASSWORD")

        when:
        def response = basicAuthRequest('InboundProduct')
                .credentials(USER, PASSWORD)
                .accept('application/xml')
                .build()
                .post(Entity.xml('<product />'))

        then:
        response.status == HttpStatus.SC_FORBIDDEN
        response.getHeaderString('Content-Type') == 'application/xml;charset=UTF-8'
        def xml = XmlObject.createFrom response.readEntity(String)
        xml.get('/error/code') == 'forbidden'
        xml.get('/error/message/@lang') == 'en'
    }

    def basicAuthRequest(String path='') {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .path(path)
    }
}

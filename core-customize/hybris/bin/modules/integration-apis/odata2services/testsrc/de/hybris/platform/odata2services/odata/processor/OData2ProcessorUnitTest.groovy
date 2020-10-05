/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.processor.handler.delete.DeleteHandler
import de.hybris.platform.odata2services.odata.processor.handler.delete.DeleteParam
import de.hybris.platform.odata2services.odata.processor.handler.persistence.PatchPersistenceHandler
import de.hybris.platform.odata2services.odata.processor.handler.persistence.PersistenceHandler
import de.hybris.platform.odata2services.odata.processor.handler.persistence.PersistenceParam
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.BatchParam
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.BatchPersistenceHandler
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.ChangeSetParam
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.ChangeSetPersistenceHandler
import de.hybris.platform.odata2services.odata.processor.handler.read.ReadHandler
import de.hybris.platform.odata2services.odata.processor.handler.read.ReadParam
import org.apache.commons.io.IOUtils
import org.apache.olingo.odata2.api.batch.BatchHandler
import org.apache.olingo.odata2.api.batch.BatchResponsePart
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.apache.olingo.odata2.api.uri.PathInfo
import org.apache.olingo.odata2.api.uri.UriInfo
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.Charset

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.batchBuilder
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetBuilder.changeSetBuilder
import static de.hybris.platform.odata2services.odata.content.ODataChangeSetPartBuilder.partBuilder

@UnitTest
class OData2ProcessorUnitTest extends Specification {

    def batchPersistenceHandler = Mock BatchPersistenceHandler
    def changeSetPersistenceHandler = Mock ChangeSetPersistenceHandler
    def persistenceHandler = Mock PersistenceHandler
    def patchPersistenceHandler = Mock PatchPersistenceHandler
    def deleteHandler = Mock DeleteHandler
    def readHandler = Mock ReadHandler
    def context = context()
    def processor = new OData2Processor(batchPersistenceHandler: batchPersistenceHandler,
            changeSetPersistenceHandler: changeSetPersistenceHandler,
            persistenceHandler: persistenceHandler,
            patchPersistenceHandler: patchPersistenceHandler,
            deleteHandler: deleteHandler,
            readHandler: readHandler,
            context: context)

    @Test
    def "BatchPersistenceHandler is called for executeBatch"() {
        given:
        def batchHandler = Stub BatchHandler
        def contentType = 'multipart/mixed; boundary=batch'
        def content = content()
        def response = Stub ODataResponse

        when:
        def actualResponse = processor.executeBatch batchHandler, contentType, content

        then:
        1 * batchPersistenceHandler.handle(_ as BatchParam) >> { args ->
            def param = args[0] as BatchParam
            assert param.batchHandler == batchHandler
            assert !param.batchRequestParts.isEmpty()
            assert param.batchRequestPartSize == 1; response
        }
        actualResponse == response
    }

    @Test
    def "ChangeSetPersistenceHandler is called for executeChangeSet"() {
        given:
        def batchHandler = Stub BatchHandler
        def requests = [Stub(ODataRequest)]
        def response = Stub BatchResponsePart

        when:
        def actualResponse = processor.executeChangeSet batchHandler, requests

        then:
        1 * changeSetPersistenceHandler.handle(_ as ChangeSetParam) >> { args ->
            def param = args[0] as ChangeSetParam
            assert param.batchHandler == batchHandler
            assert param.requests == requests; response
        }
        actualResponse == response
    }

    @Test
    def "PersistenceHandler is called for createEntity"() {
        given:
        def entityType = Stub EdmEntityType
        def entitySet = Stub(EdmEntitySet) {
            getEntityType() >> entityType
        }
        def uriInfo = Stub(UriInfo) {
            getStartEntitySet() >> entitySet
        }
        def content = Stub InputStream
        def requestContentType = 'application/json'
        def responseContentType = 'application/xml'
        def response = Stub ODataResponse

        when:
        def actualResponse = processor.createEntity uriInfo, content, requestContentType, responseContentType

        then:
        1 * persistenceHandler.handle(_ as PersistenceParam) >> { args ->
            def param = args[0] as PersistenceParam
            assert param.content == content
            assert param.context == context
            assert param.entityType == entityType
            assert param.entitySet == entitySet
            assert param.requestContentType == requestContentType
            assert param.responseContentType == responseContentType
            assert param.uriInfo == uriInfo; response
        }
        actualResponse == response
    }

    @Test
    def "PatchPersistenceHandler is called for updateEntity"() {
        given:
        def entityType = Stub EdmEntityType
        def entitySet = Stub(EdmEntitySet) {
            getEntityType() >> entityType
        }
        def uriInfo = Stub(UriInfo) {
            getStartEntitySet() >> entitySet
        }
        def content = Stub InputStream
        def requestContentType = 'application/json'
        def responseContentType = 'application/xml'
        def doesNotMatterMerge = true
        def response = Stub ODataResponse

        when:
        def actualResponse = processor.updateEntity uriInfo, content, requestContentType, doesNotMatterMerge, responseContentType

        then:
        1 * patchPersistenceHandler.handle(_ as PersistenceParam) >> { args ->
            def param = args[0] as PersistenceParam
            assert param.content == content
            assert param.context == context
            assert param.entityType == entityType
            assert param.entitySet == entitySet
            assert param.requestContentType == requestContentType
            assert param.responseContentType == responseContentType
            assert param.uriInfo == uriInfo; response
        }
        actualResponse == response
    }

    @Test
    def "DeleteHandler is called for deleteEntity"() {
        given:
        def uriInfo = Stub DeleteUriInfo
        def responseContentType = 'Does Not Matter'
        def response = Stub ODataResponse

        when:
        def actualResponse = processor.deleteEntity uriInfo, responseContentType

        then:
        1 * deleteHandler.handle(_ as DeleteParam) >> { args ->
            def param = args[0] as DeleteParam
            assert param.context == context
            assert param.uriInfo == uriInfo; response
        }
        actualResponse == response
    }

    @Test
    @Unroll
    def "ReadHandler is called for #method"() {
        given:
        def uriInfo = Stub UriInfo
        def responseContentType = 'application/json'
        def response = Stub ODataResponse

        when:
        def actualResponse = processor."${method}" uriInfo, responseContentType

        then:
        1 * readHandler.handle(_ as ReadParam) >> { args ->
            def param = args[0] as ReadParam
            assert param.context == context
            assert param.responseContentType == responseContentType
            assert param.uriInfo == uriInfo; response
        }
        actualResponse == response

        where:
        method << ['countEntitySet', 'readEntity', 'readEntitySet']
    }

    private ODataContext context() {
        Stub(ODataContext) {
            getPathInfo() >> Stub(PathInfo) {
                getServiceRoot() >> URI.create('http://url/to/service/root')
            }
        }
    }

    private static InputStream content() {
        def payload = batchBuilder()
                .withBoundary('batch')
                .withChangeSet(changeSetBuilder()
                        .withUri('Products')
                        .withPart(partBuilder().withBody(json().withField('property', 'value')))
                ).build()
        IOUtils.toInputStream(payload, Charset.defaultCharset())
    }
}

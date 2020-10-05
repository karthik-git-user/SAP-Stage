/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence

import de.hybris.platform.servicelayer.exceptions.ModelSavingException
import de.hybris.platform.servicelayer.exceptions.SystemException
import de.hybris.platform.servicelayer.interceptor.InterceptorException
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification

import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder

class PersistenceExceptionUnitTest extends Specification {

    private static final def INTERCEPTOR_MESSAGE = "interceptor message"
    private static final def SYSTEM_MESSAGE = "system message"
    private static final def TEST_INTEGRATION_KEY = "testIntegrationKey"
    private static final def OBJECT_CODE = "testIO"
    private static final def ENTITY_TYPE_NAME = "EntityTypeName"
    private static final def ACCEPT_LOCALE = Locale.ENGLISH

    @Shared
    private EdmEntityType entityType = Stub(EdmEntityType) {
        getName() >> ENTITY_TYPE_NAME
    }
    @Shared
    private EdmEntitySet entitySet = Stub(EdmEntitySet) {
        getEntityType() >> entityType
    }

    @Test
    def "Interceptor Exception message is included when the exception cause is of the type InterceptorException"() throws EdmException {
        given:
        def e = new ModelSavingException("testMessage", new InterceptorException(INTERCEPTOR_MESSAGE))

        def storageRequest = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withODataEntry(Stub(ODataEntry))
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(TEST_INTEGRATION_KEY)
                .build()

        when:
        def persistenceException = new PersistenceException(e, storageRequest)

        then:
        with(persistenceException) {
            getMessage().contains(INTERCEPTOR_MESSAGE)
            getMessage().contains(ENTITY_TYPE_NAME)
        }
    }

    @Test
    def "System Exception message is included when the exception is of the type SystemException"() throws EdmException {
        given:
        def e = new SystemException(SYSTEM_MESSAGE)

        def storageRequest = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withODataEntry(Stub(ODataEntry))
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(TEST_INTEGRATION_KEY)
                .build()

        when:
        def persistenceException = new PersistenceException(e, storageRequest)

        then:
        with(persistenceException) {
            getMessage().contains(SYSTEM_MESSAGE)
            getMessage().contains(ENTITY_TYPE_NAME)
        }
    }

    @Test
    def "no additional message is included when the exception is not of type SystemException & cause is not of the type InterceptorException"() throws EdmException {
        given:
        def eMessage = "testMessage"
        def eCauseMessage = "test e cause message"
        def e = new RuntimeException(eMessage, new IllegalArgumentException(eCauseMessage))

        def storageRequest = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withODataEntry(Stub(ODataEntry))
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(TEST_INTEGRATION_KEY)
                .build()

        when:
        def persistenceException = new PersistenceException(e, storageRequest)

        then:
        with(persistenceException) {
            !getMessage().contains(eMessage)
            !getMessage().contains(eCauseMessage)
            getMessage().contains(ENTITY_TYPE_NAME)
        }
    }
}

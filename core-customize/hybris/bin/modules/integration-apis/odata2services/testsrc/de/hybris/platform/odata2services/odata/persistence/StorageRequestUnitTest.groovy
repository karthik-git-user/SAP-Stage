/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import org.apache.olingo.odata2.api.edm.*
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.ep.feed.ODataFeed
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder

@UnitTest
class StorageRequestUnitTest extends Specification {
    private static final String POST_HOOK = "postHook"
    private static final String PRE_HOOK = "preHook"
    private static final String OBJECT_CODE = "IntegrationObjectCode"
    private static final Locale CONTENT_LOCALE = Locale.GERMAN
    private static final Locale ACCEPT_LOCALE = Locale.ENGLISH
    private static final String ENTITY_TYPE_NAME = "EntityTypeName"
    private static final String INTEGRATION_KEY = "testIntegrationKey"

    @Shared
    private EdmEntityType entityType = Stub(EdmEntityType) {
        getName() >> ENTITY_TYPE_NAME
    }
    @Shared
    private EdmEntitySet entitySet = Stub(EdmEntitySet) {
        getEntityType() >> entityType
    }
    private ODataEntry oDataEntry = Stub(ODataEntry)
    private IntegrationItem item = Stub(IntegrationItem)

    @Test
    @Unroll
    def "IllegalArgumentException is thrown when request is built with null #condition"() {
        given:
        entitySet.getEntityType() >> type

        when:
        storageRequestBuilder()
                .withEntitySet(null)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withODataEntry(oDataEntry)
                .withIntegrationObject(OBJECT_CODE)
                .build()

        then:
        thrown(IllegalArgumentException)

        where:
        condition                 | set       | type       | content        | accept        | objectCode
        'entity set'              | null      | entityType | CONTENT_LOCALE | ACCEPT_LOCALE | OBJECT_CODE
        'entity type'             | entitySet | null       | CONTENT_LOCALE | ACCEPT_LOCALE | OBJECT_CODE
        'integration object code' | entitySet | entityType | CONTENT_LOCALE | ACCEPT_LOCALE | null
        'Accept-Language locale'  | entitySet | entityType | CONTENT_LOCALE | null          | OBJECT_CODE
        'Content-Language locale' | entitySet | entityType | null           | ACCEPT_LOCALE | OBJECT_CODE
    }

    @Test
    def "request can be built with all attributes explicitly specified"() {
        given:
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withODataEntry(oDataEntry)
                .withPostPersistHook(POST_HOOK)
                .withPrePersistHook(PRE_HOOK)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationItem(item)
                .withIntegrationKey(INTEGRATION_KEY)
                .withReplaceAttributes(true)
                .withItemCanBeCreated(true)
                .build()

        expect:
        request != null
        with(request) {
            entitySet == entitySet
            entityType == entityType
            contentLocale == CONTENT_LOCALE
            acceptLocale == ACCEPT_LOCALE
            oDataEntry == oDataEntry
            postPersistHook == POST_HOOK
            prePersistHook == PRE_HOOK
            integrationObjectCode == OBJECT_CODE
            integrationItem == item
            integrationKey == INTEGRATION_KEY
            replaceAttributes
            itemCanBeCreated
        }
    }

    @Test
    def "request can be built with null OData entry"() {
        given:
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withODataEntry(null)
                .build()

        expect:
        request?.ODataEntry == null
    }

    @Test
    def "request can be built with null integration item"() {
        given:
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationItem(null)
                .build()

        expect:
        request?.integrationItem == null
    }

    @Test
    def "request can be built without persistence hooks specification"() {
        given:
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .build()

        expect:
        request != null
        request.postPersistHook == ""
        request.prePersistHook == ""
    }

    @Test
    def "new request instance can be built from another request"() {
        given:
        def original = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withODataEntry(oDataEntry)
                .withPostPersistHook(POST_HOOK)
                .withPrePersistHook(PRE_HOOK)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withIntegrationItem(item)
                .withReplaceAttributes(true)
                .withItemCanBeCreated(true)
                .build()
        def copy = storageRequestBuilder().from(original).build()

        expect:
        copy != null
        ! copy.is(original)
        with(copy) {
            entitySet  == entitySet
            entityType  == entityType
            contentLocale  == CONTENT_LOCALE
            acceptLocale  == ACCEPT_LOCALE
            oDataEntry  == oDataEntry
            postPersistHook  == POST_HOOK
            prePersistHook  == PRE_HOOK
            integrationObjectCode  == OBJECT_CODE
            integrationKey == INTEGRATION_KEY
            integrationItem == item
            replaceAttributes
            itemCanBeCreated
        }
    }

    @Test
    def "created request does not contain context items"() {
        expect:
        storageRequest().contextItem == Optional.empty()
    }

    @Test
    def "request returns empty context item when it's not found by the integration key"() {
        final StorageRequest request = storageRequest()

        given:
        request.putItem(Stub(ItemModel))
        request.setIntegrationKey("differentKey")

        expect:
        ! request.getContextItem().isPresent()
    }

    @Test
    def "request returns empty context item when it's not found by the entity type"() {
        final StorageRequest request = storageRequest()

        given:
        request.putItem(Stub(ItemModel))
        request.setEntityType(Stub(EdmEntityType) {
            getName() >> "differentType"
        })

        expect:
        ! request.getContextItem().isPresent()
    }

    @Test
    def "context item placed into the request can be retrieved back"() {
        final StorageRequest request = storageRequest()
        final ItemModel itemModel = Stub(ItemModel)

        given:
        request.putItem(itemModel)

        expect:
        request.getContextItem().orElse(null) == itemModel
    }

    @Test
    def "handles null item passed into the context"() {
        final StorageRequest request = storageRequest()

        given:
        request.putItem null

        expect:
        request.contextItem.empty
    }

    @Test
    def "a copy of the request contains context items()"() {
        given:
        def original = storageRequest()
        original.putItem(Stub(ItemModel))

        when:
        def copy = storageRequestBuilder().from(original).build()

        then:
        copy.getContextItem() == original.getContextItem()
    }

    @Test
    def "integration key can be derived from the context integration item"() {
        given:
        item.getIntegrationKey() >> "item|key"
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationItem(item)
                .build()

        expect:
        request.integrationKey == "item|key"
    }

    @Test
    def "integration key specified for the request overrides the key value in the integration item"() {
        given:
        item.getIntegrationKey() >> "item|key"
        def request = storageRequestBuilder()
                .withEntitySet(entitySet)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withIntegrationItem(item)
                .build()

        expect:
        request.integrationKey == INTEGRATION_KEY
    }

    @Test
    def "storage request can be converted to item lookup request"() {
        given:
        def storageRequest = storageRequest()

        when:
        def lookupRequest = storageRequest.toLookupRequest()

        then:
        with(lookupRequest) {
            integrationObjectCode == storageRequest.integrationObjectCode
            contentType == storageRequest.contentType
            acceptLocale == storageRequest.acceptLocale
            entitySet == storageRequest.entitySet
            entityType == storageRequest.entityType
            oDataEntry == storageRequest.ODataEntry
            integrationItem == storageRequest.integrationItem
            integrationKey == storageRequest.integrationKey
            serviceRoot == storageRequest.serviceRoot
            requestUri == storageRequest.requestUri
        }
    }

    @Test
    def "getReferenceContext creates a PersistenceContext for the attribute"() {
        given: 'attribute descriptor'
        def attributeName = "innerAttributeName"
        def typeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attributeName
        }
        and: 'there is a nested integration item'
        def nestedItemIntegrationKey = 'nestedKey'
        def nestedIntegrationItem = Stub(IntegrationItem) {
            getIntegrationKey() >> nestedItemIntegrationKey
        }
        item.getReferencedItem(typeAttributeDescriptor) >> nestedIntegrationItem
        and: 'there is a nested ODataEntry for the attribute defined above'
        def nestedEntry = Stub ODataEntry
        oDataEntry.getProperties() >> [innerAttributeName: nestedEntry]
        and: 'there is a nested EntitySet for the attribute defined above'
        def referenceEntitySet = Stub EdmEntitySet
        def localEntitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty(attributeName) >> Stub(EdmTyped) {
                    getType() >> Stub(EdmType) {
                        getName() >> 'NestedItem'
                    }
                }
            }
            getEntityContainer() >> Stub(EdmEntityContainer) {
                getEntitySet('NestedItems') >> referenceEntitySet
            }
        }
        and:
        def storageRequest = storageRequest(localEntitySet)

        when:
        def nestedPersistenceContext = storageRequest.getReferencedContext(typeAttributeDescriptor) as StorageRequest

        then:
        with(nestedPersistenceContext) {
            integrationItem.is nestedIntegrationItem
            integrationKey.is nestedItemIntegrationKey
            entitySet.is referenceEntitySet
            getODataEntry() == nestedEntry
            sourceContext.get().is storageRequest
            rootContext.is storageRequest
        }
    }

    @Test
    @Unroll
    def "referenced PersistenceContext has isItemCanBeCreated()=#res when the attribute has autoCreate=#create and partOf=#part"() {
        given:
        def attribute = Stub(TypeAttributeDescriptor) {
            isAutoCreate() >> create
            isPartOf() >> part
        }

        when:
        def referencedContext = storageRequest().getReferencedContext attribute

        then:
        referencedContext.itemCanBeCreated == res

        where:
        create | part  | res
        false  | false | false
        true   | false | true
        false  | true  | true
        true   | true  | true
    }

    @Test
    def "getReferencedContexts creates multiple PersistenceContexts for a collection attribute"() {
        given: 'attribute descriptor'
        def attributeName = "innerAttributeName"
        def typeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attributeName
        }
        and: 'there are two nested integration items'
        def nestedItems = [integrationItem('key1'), integrationItem('key2')]
        item.getReferencedItems(typeAttributeDescriptor) >> nestedItems
        and: 'there is a nested ODataEntry for the attribute defined above'
        def nestedEntries = [Stub(ODataEntry), Stub(ODataEntry)]
        def nestedFeed = Stub(ODataFeed) {
            getEntries() >> nestedEntries
        }
        oDataEntry.getProperties() >> [innerAttributeName: nestedFeed]
        and: 'there is a nested EntitySet for the attribute defined above'
        def referenceEntitySet = Stub EdmEntitySet
        def localEntitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty(attributeName) >> Stub(EdmTyped) {
                    getType() >> Stub(EdmType) {
                        getName() >> 'NestedItem'
                    }
                }
            }
            getEntityContainer() >> Stub(EdmEntityContainer) {
                getEntitySet('NestedItems') >> referenceEntitySet
            }
        }
        and:
        def storageRequest = storageRequest(localEntitySet)

        when:
        def nestedContexts = storageRequest.getReferencedContexts(typeAttributeDescriptor)

        then:
        nestedContexts.size() == 2
        with(nestedContexts[0]) {
            integrationItem.is nestedItems[0]
            integrationKey == 'key1'
            entitySet.is referenceEntitySet
            getODataEntry() == nestedEntries[0]
            sourceContext.get().is storageRequest
            rootContext.is storageRequest
        }
        with(nestedContexts[1]) {
            integrationItem.is nestedItems[1]
            integrationKey == 'key2'
            entitySet.is referenceEntitySet
            getODataEntry() == nestedEntries[1]
            sourceContext.get().is storageRequest
            rootContext.is storageRequest
        }
    }

    @Test
    @Unroll
    def "referenced contexts have isItemCanBeCreated()=#res when the collection attribute has autoCreate=#create and partOf=#part"() {
        given: 'attribute descriptor'
        def attributeName = 'items'
        def typeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> attributeName
            isAutoCreate() >> create
            isPartOf() >> part
        }
        and: 'there are two nested integration items'
        item.getReferencedItems(typeAttributeDescriptor) >> [integrationItem('one'), integrationItem('two')]
        and: 'there is a nested ODataEntry for the attribute defined above'
        def nestedFeed = Stub(ODataFeed) {
            getEntries() >> [Stub(ODataEntry), Stub(ODataEntry)]
        }
        oDataEntry.getProperties() >> [(attributeName): nestedFeed]

        when:
        def nestedContexts = storageRequest().getReferencedContexts(typeAttributeDescriptor)

        then:
        nestedContexts.collect { it.itemCanBeCreated } == [res, res]

        where:
        create | part  | res
        false  | false | false
        true   | false | true
        false  | true  | true
        true   | true  | true
    }

    @Test
    def "source context is empty when context is not a referenced context"() {
        expect:
        storageRequest().sourceContext.empty
    }

    @Test
    def "root context is itself when current context is not a referenced context"() {
        given:
        def storageRequest = storageRequest()

        expect:
        storageRequest.rootContext.is storageRequest
    }

    @Test
    def "multiple levels of nesting returns the correct source and root contexts"() {
        given: 'lowest level attribute descriptor'
        def lowestLevelAttributeName = "lowestLevelAttrName"
        def lowestLevelTypeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> lowestLevelAttributeName
        }
        and: 'lowest level integration item'
        def lowestLevelItemIntegrationKey = 'lowestLevelKey'
        def lowestLevelIntegrationItem = Stub(IntegrationItem) {
            getIntegrationKey() >> lowestLevelItemIntegrationKey
        }
        and: 'middle level attribute descriptor'
        def middleLevelAttributeName = "middleLevelAttrName"
        def middleLevelTypeAttributeDescriptor = Stub(TypeAttributeDescriptor) {
            getAttributeName() >> middleLevelAttributeName
        }
        and: 'middle level integration item'
        def middleLevelItemIntegrationKey = 'middleLevelKey'
        def middleLevelIntegrationItem = Stub(IntegrationItem) {
            getIntegrationKey() >> middleLevelItemIntegrationKey
            getReferencedItem(lowestLevelTypeAttributeDescriptor) >> lowestLevelIntegrationItem
        }
        item.getReferencedItem(middleLevelTypeAttributeDescriptor) >> middleLevelIntegrationItem
        and: 'there are nested ODataEntries for the attributes defined above'
        def lowestLevelEntry = Stub(ODataEntry)
        def middleLevelEntry = Stub(ODataEntry) {
            getProperties() >> [lowestLevelAttrName: lowestLevelEntry]
        }
        oDataEntry.getProperties() >> [middleLevelAttrName: middleLevelEntry]
        and: 'there is a nested EntitySet for the attribute defined above'
        def lowestReferencedEntitySet = Stub(EdmEntitySet)
        def lowestLevelEntitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty(lowestLevelAttributeName) >> Stub(EdmTyped) {
                    getType() >> Stub(EdmType) {
                        getName() >> 'LowestNestedItem'
                    }
                }
            }
            getEntityContainer() >> Stub(EdmEntityContainer) {
                getEntitySet('LowestNestedItems') >> lowestReferencedEntitySet
            }
        }
        def middleLevelEntitySet = Stub(EdmEntitySet) {
            getEntityType() >> Stub(EdmEntityType) {
                getProperty(middleLevelAttributeName) >> Stub(EdmTyped) {
                    getType() >> Stub(EdmType) {
                        getName() >> 'MiddleNestedItem'
                    }
                }
            }
            getEntityContainer() >> Stub(EdmEntityContainer) {
                getEntitySet('MiddleNestedItems') >> lowestLevelEntitySet
            }
        }
        and:
        def storageRequest = storageRequest(middleLevelEntitySet)

        when:
        def middlePersistenceContext = storageRequest.getReferencedContext(middleLevelTypeAttributeDescriptor) as StorageRequest
        def lowestPersistenceContext = middlePersistenceContext.getReferencedContext(lowestLevelTypeAttributeDescriptor) as StorageRequest

        then:
        with(lowestPersistenceContext) {
            integrationItem.is lowestLevelIntegrationItem
            integrationKey.is lowestLevelItemIntegrationKey
            entitySet.is lowestReferencedEntitySet
            getODataEntry() == lowestLevelEntry
            sourceContext.get().is middlePersistenceContext
            rootContext.is storageRequest
        }
    }

    private StorageRequest storageRequest(EdmEntitySet set = entitySet) {
        return storageRequestBuilder()
                .withEntitySet(set)
                .withContentLocale(CONTENT_LOCALE)
                .withAcceptLocale(ACCEPT_LOCALE)
                .withODataEntry(oDataEntry)
                .withPostPersistHook(POST_HOOK)
                .withPrePersistHook(PRE_HOOK)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey(INTEGRATION_KEY)
                .withIntegrationItem(item)
                .build()
    }

    private IntegrationItem integrationItem(String key) {
        Stub(IntegrationItem) {
            getIntegrationKey() >> key
        }
    }
}

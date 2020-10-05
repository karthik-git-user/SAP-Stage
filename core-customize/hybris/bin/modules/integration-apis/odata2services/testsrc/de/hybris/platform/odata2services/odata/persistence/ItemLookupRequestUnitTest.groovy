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
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.PaginationParameters
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest.itemLookupRequestBuilder

@UnitTest
class ItemLookupRequestUnitTest extends Specification {
    private static final String OBJECT_CODE = "IntegrationObjectCode"
    private static final String INTEGRATION_KEY = "item|key"

    def entitySet = Stub(EdmEntitySet) {
        getEntityType() >> Stub(EdmEntityType)
    }
    def entry = Stub ODataEntry
    def itemType = Stub TypeDescriptor
    def item = Stub(IntegrationItem) {
        getIntegrationKey() >> INTEGRATION_KEY
        getItemType() >> itemType
    }

    @Test
    def "builds item lookup request successfully"() throws EdmException {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
                .withIntegrationObject(OBJECT_CODE)
                .build()

        expect:
        request != null
        request.entitySet == entitySet
        request.entityType == entitySet.entityType
        request.acceptLocale == Locale.ENGLISH
        request.ODataEntry == entry
        request.integrationObjectCode == OBJECT_CODE
    }

    @Test
    def "integration key can be derived from the context integration item"() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationItem(item)
                .build()

        expect:
        request.integrationKey == INTEGRATION_KEY
        request.requestedItem == Optional.of(item)
        request.typeDescriptor == itemType
    }

    @Test
    def "integration key specified for the request overrides the key value in the integration item"() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey('request|integration|key')
                .withIntegrationItem(item)
                .build()

        expect:
        request.integrationKey == 'request|integration|key'
    }

    @Test
    def 'can be presented as an ItemConversionRequest'() {
        given:
        def lookup = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withIntegrationObject(OBJECT_CODE)
                .withIntegrationKey('request|integration|key')
                .withIntegrationItem(item)
                .build()
        def conversionOptions = Stub ConversionOptions
        def item = Stub ItemModel

        when:
        def conversion = lookup.toConversionRequest item, conversionOptions

        then:
        conversion.entitySet == entitySet
        conversion.acceptLocale == lookup.acceptLocale
        conversion.integrationObjectCode == lookup.integrationObjectCode
        conversion.options == conversionOptions
        conversion.value == item
        conversion.conversionLevel == 0
    }

    @Test
    @Unroll
    def "page parameters can be specified with top=#top and skip=#skip"() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
                .withTop(top)
                .withSkip(skip)
                .build()

        expect:
        request.paginationParameters == expected

        where:
        skip | top  | expected
        null | null | Optional.empty()
        null | 100  | Optional.of(PaginationParameters.create(0, 100))
        100  | null | Optional.of(PaginationParameters.create(100, 0))
        100  | 50   | Optional.of(PaginationParameters.create(100, 50))
    }

    @Test
    def 'type descriptor can be specified'() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
                .withTypeDescriptor(itemType)
                .build()

        expect:
        request.typeDescriptor.is itemType
    }

    @Test
    def 'can specify to include total count in the result'() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
                .withCount(true)
                .build()

        expect:
        request.count
        request.includeTotalCount()
    }

    @Test
    def 'count only request can be specified'() {
        given:
        def request = itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
                .withCountOnly(true)
                .build()

        expect:
        request.countOnly
    }

    ItemLookupRequest.ItemLookupRequestBuilder minimumRequiredBuilder() {
        itemLookupRequestBuilder()
                .withEntitySet(entitySet)
                .withAcceptLocale(Locale.ENGLISH)
                .withODataEntry(entry)
    }
}

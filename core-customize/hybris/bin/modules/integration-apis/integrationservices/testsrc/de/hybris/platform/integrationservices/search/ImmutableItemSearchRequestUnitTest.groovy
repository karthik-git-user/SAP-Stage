/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeDescriptor
import org.junit.Test
import spock.lang.Specification

@UnitTest
class ImmutableItemSearchRequestUnitTest extends Specification {
    @Test
    def 'builds a request for search within a type without additional conditions'() {
        given:
        def type = Stub TypeDescriptor

        when:
        def request = new ImmutableItemSearchRequest.Builder()
                .withItemType(type)
                .build()

        then:
        with(request) {
            typeDescriptor == type
            paginationParameters.empty
            requestedItem.empty
            !filter
            !includeTotalCount()
            !countOnly
        }
    }

    @Test
    def 'builds a request for search by key attributes'() {
        given:
        def type = Stub TypeDescriptor
        def item = integrationItem(type)

        when:
        def request = new ImmutableItemSearchRequest.Builder()
                .withIntegrationItem(item)
                .build()

        then:
        with(request) {
            typeDescriptor == type
            requestedItem == Optional.of(item)
            paginationParameters.empty
            !filter
            !includeTotalCount()
            !countOnly
        }
    }

    @Test
    def 'builds a request for search with page parameters'() {
        given:
        def pageParams = PaginationParameters.create(10, 5)

        when:
        def request = new ImmutableItemSearchRequest.Builder()
                .withItemType(Stub(TypeDescriptor))
                .withPageParameters(pageParams)
                .build()

        then:
        request.paginationParameters == Optional.of(pageParams)
    }

    @Test
    def 'builds a request for search with filter conditions'() {
        given:
        def filter = new WhereClauseConditions()

        when:
        def request = new ImmutableItemSearchRequest.Builder()
                .withItemType(Stub(TypeDescriptor))
                .withFilter(filter)
                .build()

        then:
        request.filter.is filter
    }

    @Test
    def 'builds a request for search with total number of matching items'() {
        given:
        def request = new ImmutableItemSearchRequest.Builder()
                .withItemType(Stub(TypeDescriptor))
                .withTotalCount()
                .build()

        expect:
        with(request) {
            includeTotalCount()
            !countOnly
        }
    }

    @Test
    def 'builds a request for search of matching items total number only'() {
        given:
        def request = new ImmutableItemSearchRequest.Builder()
                .withItemType(Stub(TypeDescriptor))
                .withCountOnly()
                .build()

        expect:
        with(request) {
            includeTotalCount()
            countOnly
        }
    }

    @Test
    def 'changes to the builder spec do no mutate previously created requests'() {
        given: 'a builder'
        def builder = new ImmutableItemSearchRequest.Builder()
                .withItemType(Stub(TypeDescriptor))
        and: 'first request is created using the builder'
        def request1 = builder.build()
        and: 'the builder sets more specification before creating second request'
        def request2 = builder
                .withCountOnly()
                .withFilter(new WhereClauseConditions())
                .withPageParameters(PaginationParameters.create(1, 2))
                .withIntegrationItem(integrationItem())
                .build()

        expect: 'first created request did not pick up specifications applied to the second request'
        !request1.is(request2)
        with(request1) {
            typeDescriptor != request2.typeDescriptor
            requestedItem != request2.requestedItem
            paginationParameters != request2.paginationParameters
            filter != request2.filter
            includeTotalCount() != request2.includeTotalCount()
            countOnly != request2.countOnly
        }
    }

    @Test
    def 'fails to build a request without item type specified'() {
        when:
        new ImmutableItemSearchRequest.Builder()
                .withCountOnly()
                .withFilter(new WhereClauseConditions())
                .withPageParameters(PaginationParameters.create(1, 2))
                .build()

        then:
        def e = thrown IllegalArgumentException
        e.message == 'TypeDescriptor must be specified for a ImmutableItemSearchRequest'
    }

    private def integrationItem(TypeDescriptor type=null) {
        Stub(IntegrationItem) {
            getItemType() >> (type ?: Stub(TypeDescriptor))
        }

    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class OrderByClauseBuilderUnitTest extends Specification {

    def service = Stub(IntegrationObjectService)
    def builder = new FlexibleSearchQueryBuilder(service)

    @Test
    def "order by localized and non-localized attributes"() {
        given:
        def itemMetadata = integrationObjectItemModelWithOrderBy("name", "price")

        def orderExpressions = new ArrayList()

        orderExpressions.add(new OrderExpression("name", OrderBySorting.DESC))
        orderExpressions.add(new OrderExpression("price", OrderBySorting.ASC))

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderExpressions)
                .withLocale(Locale.ENGLISH)
                .build().query

        then:
        searchQuery == "SELECT DISTINCT {product:pk}, {product:name[en]}, {product:price} FROM {Product* AS product} ORDER BY {product:name[en]} DESC, {product:price} ASC"
    }

    @Test
    def "combine order by and filter by on localized and non-localized attributes"() {
        given:
        def itemMetadata = integrationObjectItemModelWithOrderBy("name", "price")

        def orderExpressions = new ArrayList()

        orderExpressions.add(new OrderExpression("name", OrderBySorting.DESC))
        orderExpressions.add(new OrderExpression("price", OrderBySorting.ASC))

        def condition = new WhereClauseCondition('{name} = some_name')
        def filter = new WhereClauseConditions(condition)

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withFilter(filter)
                .withOrderBy(orderExpressions)
                .withLocale(Locale.ENGLISH)
                .build().query

        then:
        searchQuery == "SELECT DISTINCT {product:pk}, {product:name[en]}, {product:price} FROM {Product* AS product} WHERE {product:name[en]} = some_name ORDER BY {product:name[en]} DESC, {product:price} ASC"
    }

    @Test
    @Unroll
    def "order by PK only when the order by clause is not provided [#orderBy]"() {

        given:
        def itemMetadata = item('OutboundProduct', "Product")

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .orderedByPK()
                .withOrderBy(orderBy)
                .withLocale(Locale.ENGLISH)
                .build().query

        then:
        searchQuery == expectedResult

        where:
        orderBy         | expectedResult
        null            | "SELECT DISTINCT {product:pk} FROM {Product* AS product} ORDER BY {product:pk}"
        new ArrayList() | "SELECT DISTINCT {product:pk} FROM {Product* AS product} ORDER BY {product:pk}"

    }

    @Test
    @Unroll
    def "order by clause is not added to select statement when PK ordering is not specified and orderBy #orderBy"() {

        given:
        def itemMetadata = item('OutboundProduct', "Product")

        when:
        def searchQuery = builder
                .withIntegrationObjectItem(itemMetadata)
                .withOrderBy(orderBy)
                .build().query

        then:
        searchQuery == expectedResult

        where:
        orderBy         | expectedResult
        null            | "SELECT DISTINCT {product:pk} FROM {Product* AS product}"
        new ArrayList() | "SELECT DISTINCT {product:pk} FROM {Product* AS product}"

    }

    private IntegrationObjectItemModel integrationObjectItemModelWithOrderBy(final String localizedAttrName, final String attrName) {

        def item = item('OutboundProduct', "Product")
        item.getAttributes() >> [attribute([qualifier: localizedAttrName, localized: true] as Map<String, Boolean>),
                                 attribute([qualifier: attrName, localized: false] as Map<String, Boolean>)]
        item
    }

    def item(final String integrationCode, final String platformCode) {
        Stub(IntegrationObjectItemModel) {
            getCode() >> integrationCode
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> platformCode
                getAbstract() >> false
            }
            getItemTypeMatch() >> null
        }
    }

    IntegrationObjectItemAttributeModel attribute(final Map<String, Boolean> params) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> params['qualifier']
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getQualifier() >> params['qualifier']
                getLocalized() >> params['localized']
            }
        }
    }

}

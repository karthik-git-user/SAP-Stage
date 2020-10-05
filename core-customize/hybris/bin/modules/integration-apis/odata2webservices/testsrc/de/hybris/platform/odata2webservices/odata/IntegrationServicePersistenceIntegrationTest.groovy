/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.handleRequest
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.postRequestBuilder
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class IntegrationServicePersistenceIntegrationTest extends ServicelayerSpockSpecification {
    private static final SERVICE_NAME = "IntegrationService"
    private static final ITEMS_ENTITY_SET = "IntegrationObjectItems"
    private static final OBJECT_CODE = "MyIntegrationObjectTest"

    @Resource(name = 'oDataContextGenerator')
    private ODataContextGenerator contextGenerator
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade

    def setup() {
        // for IntegrationApi
        importData("/impex/essentialdata-integrationservices.impex", "UTF-8")
    }

    def cleanup() {
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    @Test
    def "Successfully create item as root when no root item exists on the same IO"() {
        given:
        final IntegrationObjectModel objectModel = integrationObject()
                .withCode(OBJECT_CODE)
                .build()

        and:
        IntegrationObjectItemModelBuilder
                .integrationObjectItem()
                .withCode("Product")
                .withIntegrationObject(objectModel)
                .build()

        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem())

        when:
        final ODataResponse response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)
        final IntegrationObjectItemModel categoryItem = IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("Category", objectModel)
        assert categoryItem.getRoot()
    }

    @Test
    def "Fails to create item as root when a root item already exists on the same IO"() {
        given:
        final IntegrationObjectModel objectModel = integrationObject()
                .withCode(OBJECT_CODE)
                .build()

        and:
        IntegrationObjectItemModelBuilder
                .integrationObjectItem()
                .withCode("Product")
                .asRoot()
                .withIntegrationObject(objectModel)
                .build()

        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem())

        when:
        final ODataResponse response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.INTERNAL_SERVER_ERROR)

        assert IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("Category", objectModel) == null
    }

    @Test
    def "Change an existing root item to not root"() {
        given:
        final String itemCode = "Category"

        and:
        final IntegrationObjectModel objectModel = integrationObject()
                .withCode(OBJECT_CODE)
                .build()
        and:
        IntegrationObjectItemModelBuilder
                .integrationObjectItem()
                .withCode(itemCode)
                .asRoot()
                .withIntegrationObject(objectModel)
                .build()

        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, item())

        when:
        def response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)

        final IntegrationObjectItemModel updatedCategoryItem = IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, objectModel)
        assert !updatedCategoryItem.getRoot()
    }

    @Test
    def "Successfully send same root item twice"() {
        given:
        final String itemCode = "Category"

        and:
        final IntegrationObjectModel objectModel = integrationObject()
                .withCode(OBJECT_CODE)
                .build()
        and:
        IntegrationObjectItemModelBuilder
                .integrationObjectItem()
                .withCode(itemCode)
                .asRoot()
                .withIntegrationObject(objectModel)
                .build()

        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem())

        when:
        def response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)

        final IntegrationObjectItemModel updatedCategoryItem = IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, objectModel)
        assert updatedCategoryItem.getRoot()
    }

    @Test
    def "Change existing item to root when no root item exists on the same IO"() {
        given:
        final String itemCode = "Category"

        and:
        final IntegrationObjectModel objectModel = integrationObject()
                .withCode(OBJECT_CODE)
                .build()
        and:
        IntegrationObjectItemModelBuilder
                .integrationObjectItem()
                .withCode(itemCode)
                .withIntegrationObject(objectModel)
                .build()

        and:
        final ODataRequest request = postRequest(ITEMS_ENTITY_SET, rootItem())

        when:
        def response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)

        final IntegrationObjectItemModel updatedCategoryItem = IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject(itemCode, objectModel)
        assert updatedCategoryItem.getRoot()
    }

    @Test
    def "Create IntegrationObject with multiple items where only 1 item as root"() {
        given:
        final ODataRequest request = postRequest("IntegrationObjects", json()
                .withCode(OBJECT_CODE)
                .withFieldValues("items",
                        rootItem("Product"),
                        item("Catalog")))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.CREATED)

        final IntegrationObjectModel objectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(OBJECT_CODE)
        assert objectModel.items.size() == 2
    }

    @Test
    def "Create IntegrationObject with multiple items where there are multiple roots"() {
        given:
        final ODataRequest request = postRequest("IntegrationObjects", json()
                .withCode(OBJECT_CODE)
                .withFieldValues("items",
                        rootItem("Product"),
                        rootItem("Catalog")))

        when:
        final ODataResponse response = handleRequest(facade, request)

        then:
        ODataAssertions.assertThat(response)
                .hasStatus(HttpStatusCodes.INTERNAL_SERVER_ERROR)

        assert IntegrationObjectTestUtil.findIntegrationObjectByCode(OBJECT_CODE) == null
    }

    private static ODataRequest postRequest(final String entitySet, final JsonBuilder requestBody) {
        postRequestBuilder(SERVICE_NAME, entitySet, APPLICATION_JSON_VALUE)
                .withAcceptLanguage(Locale.ENGLISH)
                .withBody(requestBody)
                .build()
    }

    private static JsonBuilder item(final String itemCode = "Category") {
        json()
                .withCode(itemCode)
                .withField("type", json().withCode(itemCode))
                .withField("root", false)
                .withField("integrationObject", json().withCode(OBJECT_CODE))
    }

    private static JsonBuilder rootItem(final String itemCode = "Category") {
        json()
                .withCode(itemCode)
                .withField("type", json().withCode(itemCode))
                .withField("root", true)
                .withField("integrationObject", json().withCode(OBJECT_CODE))
    }
}

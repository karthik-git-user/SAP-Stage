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

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel
import de.hybris.platform.catalog.model.classification.ClassificationClassModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.ODataContextGenerator
import de.hybris.platform.odata2services.odata.ODataSchema
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class IntegrationServiceGetIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	private static final def PHONE_IO = 'PhoneIO'
	private static final def SERVICE_NAME = 'IntegrationService'
	private static final def CLASS_SYSTEM = 'Electronics'
	private static final def CLASS_VERSION = 'Phones'
	private static final def CLASSIFICATION_CLASS = 'phoneSpecs'
	private static final def CLASS_SYSTEM_VERSION = "$CLASS_SYSTEM:$CLASS_VERSION"
	private static final def PRODUCT_WITH_CLASS_ATTRIBUTES_IO = "ProductWithClassAttributesIO"

	@Resource(name = 'oDataContextGenerator')
	private ODataContextGenerator contextGenerator
	@Resource(name = "defaultODataFacade")
	private ODataFacade facade
	@Resource(name = "oDataSchemaGenerator")
	private SchemaGenerator generator

	def setup() {
		importCsv("/impex/essentialdata-integrationservices.impex", "UTF-8")

		IntegrationTestUtil.importImpEx(
				'$catalogVersionHeader = catalogVersion(catalog(id), version)',
				'$systemVersionHeader = systemVersion(catalog(id), version)',
				'$class = classificationClass($catalogVersionHeader, code)',
				'$attribute = classificationAttribute($systemVersionHeader, code)',

				'$item = integrationObjectItem(integrationObject(code), code)',
				'$systemVersionHeader = systemVersion(catalog(id), version)',
				'$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
				'$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
				'$classificationAssignment = classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',

				'INSERT_UPDATE ClassificationSystem; id[unique = true]',
				"                                  ; $CLASS_SYSTEM",
				'INSERT_UPDATE ClassificationSystemVersion; catalog(id)[unique = true]; version[unique = true]',
				"                                         ; $CLASS_SYSTEM             ; $CLASS_VERSION",
				'INSERT_UPDATE ClassificationClass; code[unique = true]  ; $catalogVersionHeader[unique = true]',
				"                                 ; $CLASSIFICATION_CLASS; $CLASS_SYSTEM_VERSION",
				'INSERT_UPDATE ClassificationAttribute; code[unique = true]; $systemVersionHeader[unique = true]',
				"                                     ; brand              ; $CLASS_SYSTEM_VERSION",
				"                                     ; model              ; $CLASS_SYSTEM_VERSION",
				'INSERT_UPDATE ClassAttributeAssignment; $class[unique = true]                      ; $attribute[unique = true]    ; unit($systemVersionHeader, code); attributeType(code); mandatory[default = false]',
				"                                      ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS; $CLASS_SYSTEM_VERSION:brand  ;                                 ; number             ; true",
				"                                      ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS; $CLASS_SYSTEM_VERSION:model  ;                                 ; number             ; true",

				'INSERT_UPDATE IntegrationObject; code[unique = true]',
				"                               ; $PHONE_IO",
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				"                                   ; $PHONE_IO                             ; Product            ; Product",
				"                                   ; $PHONE_IO                             ; CatalogVersion     ; CatalogVersion",
				"                                   ; $PHONE_IO                             ; Catalog            ; Catalog",

				'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]     ; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				"                                            ; $PHONE_IO:Catalog        ; id                          ; Catalog:id",
				"                                            ; $PHONE_IO:Product        ; code                        ; Product:code",
				"                                            ; $PHONE_IO:Product        ; catalogVersion              ; Product:catalogVersion                             ; $PHONE_IO:CatalogVersion",
				"                                            ; $PHONE_IO:CatalogVersion ; version                     ; CatalogVersion:version",
				"                                            ; $PHONE_IO:CatalogVersion ; catalog                     ; CatalogVersion:catalog                             ; $PHONE_IO:Catalog"
		)
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
	def "GET IntegrationObjects returns all integration objects"() {
		given:
		def context = oDataGetContext("IntegrationObjects")

		when:
		ODataResponse response = facade.handleRequest(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 2
		json.getCollectionOfObjects('d.results[*].code').containsAll('IntegrationService', PHONE_IO)
	}

	@Test
	def "GET IntegrationObjectItems returns all integration object items"() {
		given:
		def params = ['\$top': '1000']
		def context = oDataGetContext("IntegrationObjectItems", params)

		when:
		ODataResponse response = facade.handleRequest(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 16
		json.getCollectionOfObjects('d.results[*].code').containsAll('Product', 'CatalogVersion', 'Catalog',
                'IntegrationObject', 'IntegrationType', 'ItemTypeMatchEnum', 'IntegrationObjectItem',
                'IntegrationObjectItemAttribute', 'IntegrationObjectItemClassificationAttribute',
                'ClassificationSystem', 'ClassificationSystemVersion', 'ClassAttributeAssignment',
                'ClassificationClass', 'ClassificationAttribute', 'AttributeDescriptor', 'ComposedType')
	}

	@Test
	def "GET IntegrationObjectItemAttributes returns all integration object attributes for Product"() {
		given: "A large page size to make sure the first page will include all attributes that we expect"
		def params = ['\$top': '1000']
		def context = oDataGetContext("IntegrationObjectItemAttributes", params)

		when:
		ODataResponse response = facade.handleRequest(context)

		then: "Verify the number of attributes and run a sanity check to verify some of the expected attributes are shown."
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects("d.results").size() == 39
		json.getCollectionOfObjects('d.results[*].attributeName').containsAll('code', 'catalogVersion', 'catalog')
	}

	@Test
	def "GET IntegrationObjectItemClassificationAttributes returns classification attributes for Product"() {
		given:
		classificationAttributesForProduct()
		and:
		def context = oDataGetContext("IntegrationObjectItemClassificationAttributes")

		when:
		ODataResponse response = facade.handleRequest(context)

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects('d.results[*].attributeName').containsAll('brand', 'model')
	}

	@Test
	def "POST IntegrationObjects, IntegrationObjectItem and IntegrationObjectItemAttribute creates new service"() {
		given:
		categoryIntegrationObjectIsCreated("CategoryOne")

		when:
		def schema = ODataSchema.from(generator.generateSchema(getIntegrationObjectItemModelDefinitions()))

		then:
		with(schema)
				{
					getEntityTypeNames().containsAll("Category", TestConstants.LOCALIZED_ENTITY_PREFIX + "Category")
					getEntityType("Category").getPropertyNames().containsAll("code", "name")

					getEntityType("Category").getAnnotatableProperty("code").getAnnotationNames() == ["Nullable", "s:IsUnique"]
					getEntityType("Category").getAnnotatableProperty("name").getAnnotationNames() == ["s:IsLanguageDependent", "Nullable"]
					getEntityType("Category").getNavigationPropertyNames() == ["localizedAttributes"]
				}
	}

    @Test
    def "POST an IO with Classification Attributes for type Product creates schema containing those Classification Attributes"() {
        given: 'product IOI'
        def jsonBuilder = productIntegrationObjectItemBody(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
        and: 'brand classification attribute'
        def body = jsonBuilder
                .withFieldValues("classificationAttributes", productClassificationAttribute(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, "brand"))
                .build()
        and: 'product is POSTed to the integration api'
        productIntegrationObjectIsCreated(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, body)

        when: 'schema is generate for MyProduct'
        def schema = ODataSchema.from(generator.generateSchema(IntegrationTestUtil.findAll(IntegrationObjectItemModel, { it.code == 'MyProduct' })))

        then: 'schema contains regular and classification attributes'
        with(schema)
                {
                    getEntityTypeNames().containsAll("MyProduct")
                    getEntityType("MyProduct").getPropertyNames().containsAll("code", "brand")
                    getEntityType("MyProduct").getNavigationPropertyNames().containsAll("catalogVersion")
                }
    }

    @Test
    def "PATCH an IO with Classification Attributes for type Product updates schema with new Classification Attributes"() {
        given: 'product IOI'
        def jsonBuilder = productIntegrationObjectItemBody(PRODUCT_WITH_CLASS_ATTRIBUTES_IO)
        and: 'brand classification attribute'
        def body = jsonBuilder
                .withFieldValues("classificationAttributes", productClassificationAttribute(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, "brand"))
                .build()
        and: 'product is POSTed to the integration api'
        productIntegrationObjectIsCreated(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, body)
        when: 'product is PATCHed to the integration api with brand and model classification attributes'
        def patchedBody = productIntegrationObjectItemBody(PRODUCT_WITH_CLASS_ATTRIBUTES_IO).withFieldValues("classificationAttributes",
                productClassificationAttribute(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, "brand"),
                productClassificationAttribute(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, "model")).build()

        productIntegrationObjectIsPatched(PRODUCT_WITH_CLASS_ATTRIBUTES_IO, patchedBody)

        and: 'schema is generated for MyProduct'
        def schema = ODataSchema.from(generator.generateSchema(IntegrationTestUtil.findAll(IntegrationObjectItemModel, { it.code == 'MyProduct' })))

        then: 'schema contains regular and PATCHed classification attributes'
        with(schema)
                {
                    getEntityTypeNames().containsAll("MyProduct")
                    getEntityType("MyProduct").getPropertyNames().containsAll("code", "brand", "model")
                }
    }

	@Test
	def "POST multiple IntegrationObjects including duplicated Attribute name/descriptor creates different Integration Objects"() {
		given:
		categoryIntegrationObjectIsCreated("CategoryOne")
		categoryIntegrationObjectIsCreated("CategoryTwo")

		when:
		ODataResponse response = facade.handleRequest(oDataGetContext("IntegrationObjects"))

		then:
		def json = extractEntitiesFrom response
		json.getCollectionOfObjects('d.results[*].code').containsAll('CategoryOne', 'CategoryTwo')

	}

	def classificationAttributesForProduct() {
		IntegrationTestUtil.importImpEx(
				'$item = integrationObjectItem(integrationObject(code), code)',
				'$systemVersionHeader = systemVersion(catalog(id), version)',
				'$classificationClassHeader = classificationClass(catalogVersion(catalog(id), version), code)',
				'$classificationAttributeHeader = classificationAttribute($systemVersionHeader, code)',
				'$classificationAssignment = classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)',
				'INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment',
				"                                                          ; $PHONE_IO:Product   ; brand                       ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS:$CLASS_SYSTEM_VERSION:brand",
				"                                                          ; $PHONE_IO:Product   ; model                       ; $CLASS_SYSTEM_VERSION:$CLASSIFICATION_CLASS:$CLASS_SYSTEM_VERSION:model"
		)
	}

	def categoryIntegrationObjectIsCreated(String integrationObjectCode) {
		def categoryOneIntegrationObjectContext = oDataPostContext("IntegrationObjects",
				categoryIntegrationObjectBody(integrationObjectCode))
		facade.handleRequest(categoryOneIntegrationObjectContext)
	}

	def productIntegrationObjectIsCreated(String integrationObjectCode, String productBody) {
		def productIntegrationObjectContext = oDataPostContext("IntegrationObjects",
				productIntegrationObjectBody(integrationObjectCode, productBody))
		facade.handleRequest(productIntegrationObjectContext)
	}

	def productIntegrationObjectIsPatched(String integrationObjectCode, String patchedBody) {
		def body = productIntegrationObjectBody(integrationObjectCode, patchedBody)

		facade.handleRequest patch(SERVICE_NAME, "IntegrationObjects", integrationObjectCode, body)
	}

	static ODataContext patch(String serviceName, String entitySet, String key, String body) {
		ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataPatchRequest()
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withEntitySet(entitySet)
						.withEntityKeys(key))
				.withContentType('application/json')
				.withAccepts('application/json')
				.withBody(body)
	}

	ODataContext oDataGetContext(String entitySetName) {
		oDataGetContext(entitySetName, [:])
	}

	ODataContext oDataGetContext(String entitySetName, Map params) {
		def request = ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(SERVICE_NAME)
						.withEntitySet(entitySetName))
				.withParameters(params)
				.build()

		contextGenerator.generate request
	}

	ODataContext oDataPostContext(String entitySetName, String content) {
		def request = ODataFacadeTestUtils
				.oDataPostRequest(SERVICE_NAME, entitySetName, content, APPLICATION_JSON_VALUE)

		contextGenerator.generate request
	}

	def categoryIntegrationObjectBody(String integrationObjectCode) {
		json()
				.withCode(integrationObjectCode)
				.withField("integrationType", json().withCode("INBOUND"))
				.withFieldValues("items", categoryIntegrationObjectItemBody(integrationObjectCode))
				.build()
	}

	def productIntegrationObjectBody(String integrationObjectCode, String productBody) {
		json()
				.withCode(integrationObjectCode)
				.withField("integrationType", json().withCode("INBOUND"))
				.withFieldValues("items",
						productBody,
						catalogVersionIntegrationObjectItemBody(integrationObjectCode),
						catalogIntegrationObjectItemBody(integrationObjectCode))
				.build()
	}

	def categoryIntegrationObjectItemBody(String integrationObjectCode) {
		json()
				.withCode("Category")
				.withField("type", json().withCode("Category"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
				.withFieldValues("attributes",
						categoryCodeAttribute(integrationObjectCode),
						categoryNameAttribute(integrationObjectCode))
				.build()
	}

	def productIntegrationObjectItemBody(String integrationObjectCode) {
		json()
				.withCode("MyProduct")
				.withField("type", json().withCode("Product"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
				.withFieldValues("attributes",
						productCodeAttribute(integrationObjectCode),
						productCatalogVersionAttribute(integrationObjectCode))

	}

	def catalogVersionIntegrationObjectItemBody(String integrationObjectCode) {
		json()
				.withCode("CatalogVersion")
				.withField("type", json().withCode("CatalogVersion"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
				.withFieldValues("attributes",
						catalogVersionCatalogAttribute(integrationObjectCode),
						catalogVersionVersionAttribute(integrationObjectCode))
				.build()
	}

	def catalogIntegrationObjectItemBody(String integrationObjectCode) {
		json()
				.withCode("Catalog")
				.withField("type", json().withCode("Catalog"))
				.withField("integrationObject", json().withCode(integrationObjectCode))
				.withFieldValues("attributes",
						catalogIdAttribute(integrationObjectCode)).build()
	}

	def categoryCodeAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "code")
				.withField("attributeDescriptor", attributeDescriptor("code", "Category"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "Category", "Category")).build()
	}

	def productCodeAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "code")
				.withField("attributeDescriptor", attributeDescriptor("code", "Product"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "MyProduct", "Product")).build()
	}


	def productClassificationAttribute(String integrationObjectCode, String attributeName) {
		json()
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "MyProduct", "Product"))
				.withField("attributeName", attributeName)
				.withField("classAttributeAssignment", classAttributeAssignment(attributeName)).build()
	}

	def catalogVersionCatalogAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "catalog")
				.withField("attributeDescriptor", attributeDescriptor("catalog", "CatalogVersion"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "CatalogVersion", "CatalogVersion"))
				.withField("returnIntegrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "Catalog", "Catalog")).build()

	}

	def catalogVersionVersionAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "version")
				.withField("attributeDescriptor", attributeDescriptor("version", "CatalogVersion"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "CatalogVersion", "CatalogVersion")).build()
	}

	def catalogIdAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "id")
				.withField("attributeDescriptor", attributeDescriptor("id", "Catalog"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "Catalog", "Catalog")).build()
	}

	def productCatalogVersionAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "catalogVersion")
				.withField("attributeDescriptor", attributeDescriptor("catalogVersion", "Product"))
				.withField("unique", true)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "MyProduct", "Product"))
				.withField("returnIntegrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "CatalogVersion", "CatalogVersion")).build()
	}

	def categoryNameAttribute(String integrationObjectCode) {
		json()
				.withField("attributeName", "name")
				.withField("attributeDescriptor", attributeDescriptor("name", "Category"))
				.withField("unique", false)
				.withField("integrationObjectItem", attributeIntegrationObjectItem(integrationObjectCode, "Category", "Category")).build()
	}

	def attributeIntegrationObjectItem(String integrationObjectCode, String code, String type) {
		json().withCode(code)
				.withField("type", json().withCode(type))
				.withField("integrationObject", json().withCode(integrationObjectCode))
	}

	def attributeDescriptor(String attributeName, String enclosingType) {
		json().withField("qualifier", attributeName)
				.withField("enclosingType", json().withCode(enclosingType)).build()
	}

	def classAttributeAssignment(String attributeName) {
		json()
				.withField("classificationClass", classificationClass())
				.withField("classificationAttribute", classificationAttribute(attributeName)).build()
	}

	def classificationClass() {
		json()
				.withField("catalogVersion", classificationSystemVersion())
				.withField("code", CLASSIFICATION_CLASS).build()
	}

	def classificationAttribute(String attributeName) {
		json()
				.withField("systemVersion", classificationSystemVersion())
				.withField("code", attributeName).build()
	}

	def classificationCatalogVersion() {
		json()
				.withField("catalog", json().withId(CLASS_SYSTEM))
				.withField("version", CLASS_VERSION).build()
	}

	def classificationSystemVersion() {
		json()
				.withField("catalog", json().withId(CLASS_SYSTEM))
				.withField("version", CLASS_VERSION).build()
	}

	def extractEntitiesFrom(ODataResponse response) {
		extractBodyWithExpectedStatus(response, HttpStatusCodes.OK)
	}

	def extractErrorFrom(ODataResponse response) {
		extractBodyWithExpectedStatus(response, HttpStatusCodes.BAD_REQUEST)
	}

	def extractBodyWithExpectedStatus(ODataResponse response, HttpStatusCodes expStatus) {
		assert response.getStatus() == expStatus
		JsonObject.createFrom response.getEntity() as InputStream
	}

	def getIntegrationObjectItemModelDefinitions() {
		IntegrationTestUtil.findAll(IntegrationObjectItemModel.class) as Collection<IntegrationObjectItemModel>
	}
}

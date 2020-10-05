/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.populator.PrimitiveCollectionElement
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.i18n.I18NService
import org.junit.Test
import spock.lang.Unroll

import javax.annotation.Resource

@IntegrationTest
class IntegrationObjectConversionServiceIntegrationTest extends ServicelayerTransactionalSpockSpecification {
    private static final String INTEGRATION_OBJECT = "ProductIntegrationObject"
    private static final String CATALOG_ID = "Default"

    @Resource(name = "integrationObjectConversionService")
    private IntegrationObjectConversionService conversionService
    @Resource
    private I18NService i18NService

    private Locale defaultLocale

    def setup() {
        IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true];',
				'; ProductIntegrationObject',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; ProductIntegrationObject ; Catalog        ; Catalog',
				'; ProductIntegrationObject ; CatalogVersion ; CatalogVersion',
				'; ProductIntegrationObject ; Category       ; Category',
				'; ProductIntegrationObject ; Product        ; Product',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; ProductIntegrationObject:Product        ; code            ; Product:code                 ;',
				'; ProductIntegrationObject:Category       ; code            ; Category:code                ;',
				'; ProductIntegrationObject:Category       ; name            ; Category:name                ;',
				'; ProductIntegrationObject:Category       ; description     ; Category:description         ;',
				'; ProductIntegrationObject:Category       ; products        ; Category:products            ; ProductIntegrationObject:Product',
				'; ProductIntegrationObject:Catalog        ; id              ; Catalog:id                   ;',
				'; ProductIntegrationObject:Catalog        ; urlPatterns     ; Catalog:urlPatterns          ;',
				'; ProductIntegrationObject:CatalogVersion ; catalog         ; CatalogVersion:catalog       ; ProductIntegrationObject:Catalog',
				'; ProductIntegrationObject:CatalogVersion ; version         ; CatalogVersion:version       ;',
				'INSERT_UPDATE IntegrationObject; code[unique = true];',
				'; ClassAttributeAssignment',
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; ClassAttributeAssignment ; ClassAttributeAssignment        ; ClassAttributeAssignment',
				'; ClassAttributeAssignment ; ClassificationAttributeTypeEnum ; ClassificationAttributeTypeEnum',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
				'; ClassAttributeAssignment:ClassAttributeAssignment        ; attributeType ; ClassAttributeAssignment:attributeType ; ClassAttributeAssignment:ClassificationAttributeTypeEnum ; true',
				'; ClassAttributeAssignment:ClassificationAttributeTypeEnum ; code          ; ClassificationAttributeTypeEnum:code   ;                                                          ; true',
				'; ClassAttributeAssignment:ClassificationAttributeTypeEnum ; codex         ; ClassificationAttributeTypeEnum:code   ;                                                          ;',
				'# For localized attribute test case',
				'INSERT_UPDATE Language; isocode[unique = true]',
				'; fr',
				'INSERT_UPDATE Catalog; id[unique = true];',
				'; Default',
				'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;',
				'; Default ; Staged ; true')
        defaultLocale = i18NService.currentLocale
    }

    def cleanup() {
        i18NService.currentLocale = defaultLocale
        IntegrationTestUtil.removeAll IntegrationObjectModel
        IntegrationTestUtil.removeAll CategoryModel
    }

    @Test
    def "converts simple item model without nested items"() {
        given:
        def catalog = new CatalogModel()
        catalog.id = CATALOG_ID
        catalog.urlPatterns = ['url1', 'url2']

        when:
        def converted = conversionService.convert(catalog, INTEGRATION_OBJECT)

        then:
        def expectedAttributes = [id: CATALOG_ID, urlPatterns: [PrimitiveCollectionElement.create('url1'), PrimitiveCollectionElement.create('url2')]]
        converted.intersect(expectedAttributes) == expectedAttributes // contains all expected attributes
    }

    @Test
    def "converted model contains generated integration key"() {
        given:
        def catalog = new CatalogModel()
        catalog.id = CATALOG_ID

        when:
        def converted = conversionService.convert(catalog, INTEGRATION_OBJECT)

        then:
        converted['integrationKey'] == CATALOG_ID
    }

    @Test
    def "converted model does not contain null attributes"() {
        given:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
                '; testEmpty ; Default:Staged')
        when:
        def converted = conversionService.convert(findCategoryByCode('testEmpty'), INTEGRATION_OBJECT)

        then:
        ! converted.keySet().containsAll(['name', 'products'])
    }

    @Test
    def "can convert properties of Enum type"() {
        given:
        final ClassAttributeAssignmentModel model = new ClassAttributeAssignmentModel()
        model.setAttributeType(ClassificationAttributeTypeEnum.STRING)

        when:
        def map = conversionService.convert(model, "ClassAttributeAssignment")

        then:
        map["attributeType"] == [code: 'string', codex: 'string']
    }

    @Test
    def "converts localized attributes into nested entities"() {
        given: 'default locale in the system is FRENCH'
        i18NService.setCurrentLocale(Locale.FRENCH)
        and: 'there is a category with multiple locales set'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]',
                '; localized-category ; Default:Staged ; english value ; french value')

        when:
        def converted = conversionService.convert(findCategoryByCode('localized-category'), INTEGRATION_OBJECT)

        then:
        //default language populated in localized properties
        converted['name'] == 'french value'
        converted['localizedAttributes'] == [[language: 'en', name: 'english value'], [language: 'fr', name: 'french value']]
    }

    @Test
    def "converted model contains empty localized attribute values but no null localized attribute values"() {
        given: 'default locale in the system is ENGLISH'
        i18NService.setCurrentLocale(Locale.ENGLISH)
        and: 'there a category with some localized attributes set'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]',
                '; localized ; Default:Staged ; english name',
                'UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); description[lang = fr]',
                '; localized ; Default:Staged ; french description')
        // Cannot set value to empty string via impex so have to do it programmatically
        def category = findCategoryByCode('localized')
        category.setName("", new Locale("fr"))

        when:
        def converted = conversionService.convert(category, INTEGRATION_OBJECT)

        then:
        converted['name'] == 'english name'
        ! converted.hasProperty('description')
        converted['localizedAttributes'] == [[language:'fr', name:'', description:'french description'], [language:'en', name:'english name']]
    }

    @Test
    def "throws exception when the specified IntegrationObject is not found"() {
        when:
        conversionService.convert(new ItemModel(), INTEGRATION_OBJECT + "notFound")

        then:
        thrown(IntegrationObjectNotFoundException)
    }

    @Test
    @Unroll
    def "throws exception when the specified IntegrationObject code is '#objCode'"() {
        when:
        conversionService.convert(new ItemModel(), objCode)

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('null')
        e.message.contains('empty')

        where:
        objCode << [null, '']
    }

    @Test
    def "throws exception when the IntegrationObject does not contain item definition for the specified item model"() {
        when:
        conversionService.convert(new EmployeeModel(), INTEGRATION_OBJECT)

        then:
        thrown IntegrationObjectAndItemMismatchException
    }

    @Test
    def "converts attribute value that is subclass of the declared integration object item attribute type"() {
        given:
        def catalog = new ClassificationSystemModel() // subtype of CatalogModel
        catalog.id = 'classifications'

        def catalogVersion = new CatalogVersionModel()
        catalogVersion.version = 'test'
        catalogVersion.catalog = catalog

        when:
        def converted = conversionService.convert(catalogVersion, INTEGRATION_OBJECT)

        then:
        def expectedClassificationAttributes = [id: 'classifications']
        converted['catalog'].intersect(expectedClassificationAttributes) == expectedClassificationAttributes
    }

    private static CategoryModel findCategoryByCode(String code) {
        IntegrationTestUtil.findAny(CategoryModel, { code == it.code }).orElse(null)
    }
}

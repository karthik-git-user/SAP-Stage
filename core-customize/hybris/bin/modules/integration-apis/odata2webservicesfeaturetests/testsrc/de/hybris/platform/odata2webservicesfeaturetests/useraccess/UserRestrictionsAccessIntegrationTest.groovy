/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservicesfeaturetests.useraccess

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.core.model.type.SearchRestrictionModel
import de.hybris.platform.core.model.user.UserGroupModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import org.springframework.http.MediaType
import spock.lang.Unroll

import javax.ws.rs.client.Entity

import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservicesfeaturetests.useraccess.UserAccessTestUtils.ADMIN_USER
import static de.hybris.platform.odata2webservicesfeaturetests.useraccess.UserAccessTestUtils.PASSWORD
import static de.hybris.platform.odata2webservicesfeaturetests.useraccess.UserAccessTestUtils.SERVICE

@NeedsEmbeddedServer(webExtensions = [Odata2webservicesConstants.EXTENSIONNAME])
@IntegrationTest
class UserRestrictionsAccessIntegrationTest extends ServicelayerSpockSpecification {
	private static final String CATALOG_USER = "testcataloguser"
	private static final String CATALOG_ID = "testRestrictedCatalog"


	def setupSpec() {
		importCsv '/impex/essentialdata-odata2services.impex', 'UTF-8'

		UserAccessTestUtils.givenCatalogIOExists()
		IntegrationTestUtil.createCatalogWithId(CATALOG_ID)

		final String catalogGroupUid = "testcataloggroup"

		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE UserGroup; UID[unique = true] ; description		; groups(uid)',
				"						; $catalogGroupUid   ; Catalog IO Group	;",

				"\$searchQuery=({code}='$SERVICE' AND EXISTS ({{ select {ug:PK} from {UserGroup as ug} where {ug:PK} IN (?session.user.groups) and {ug:uid} = '$catalogGroupUid' }})) OR ({code}!='$SERVICE')",

				'INSERT_UPDATE SearchRestriction; code[unique = true] ; name[lang = en]            ; query       ; principal(UID)       ; restrictedType(code); active; generate',
				'                               ; catalogIOVisibility ; TestCatalog IO Restriction ;$searchQuery ; integrationusergroup ; IntegrationObject   ; true  ; true',
		)
		UserAccessTestUtils.givenUserExistsWithUidAndGroups(ADMIN_USER, "integrationadmingroup")
		UserAccessTestUtils.givenUserExistsWithUidAndGroups(CATALOG_USER, "integrationadmingroup,$catalogGroupUid")
	}

	def cleanupSpec() {
		IntegrationTestUtil.removeAll IntegrationObjectModel
		IntegrationTestUtil.findAny(SearchRestrictionModel, { it.code == 'catalogIOVisibility' }).ifPresent { IntegrationTestUtil.remove it }
		IntegrationTestUtil.findAny(UserGroupModel, { it.uid == 'testcataloggroup' }).ifPresent { IntegrationTestUtil.remove it }
		IntegrationTestUtil.findAny(CatalogModel, { it.id == CATALOG_ID }).ifPresent { IntegrationTestUtil.remove it }
	}

	@Test
	@Unroll
	def "User #user gets #status for GET #path"() {
		when:
		def response = UserAccessTestUtils.basicAuthRequest(path)
				.credentials(user, PASSWORD)
				.build()
				.get()

		then:
		response.status == status.statusCode

		where:
		user         | status                    | path
		ADMIN_USER   | HttpStatusCodes.NOT_FOUND | SERVICE + '/$metadata'
		CATALOG_USER | HttpStatusCodes.OK        | SERVICE + '/$metadata'
		ADMIN_USER   | HttpStatusCodes.NOT_FOUND | "$SERVICE/Catalogs('${CATALOG_ID}')"
		CATALOG_USER | HttpStatusCodes.OK        | "$SERVICE/Catalogs('${CATALOG_ID}')"
	}

	@Test
	@Unroll
	def "User #user gets #status for POST TestCatalog/Catalogs"() {
		when:
		def response = UserAccessTestUtils.basicAuthRequest("$SERVICE/Catalogs")
				.credentials(user, PASSWORD)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.build()
				.post(Entity.json(json().withField("id", "testNewCatalog").build()))

		then:
		response.status == status.statusCode

		where:
		user         | status
		ADMIN_USER   | HttpStatusCodes.NOT_FOUND
		CATALOG_USER | HttpStatusCodes.CREATED
	}
}
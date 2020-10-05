/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservicesfeaturetests.useraccess

import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import org.springframework.http.MediaType

class UserAccessTestUtils {
	protected static final String SERVICE = "TestCatalog"
	protected static final String ADMIN_USER = "userintegrationadmin"
	protected static final String PASSWORD = "password"

	protected static void givenCatalogIOExists() {
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]',
				"                               ; $SERVICE",
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				"                                   ; $SERVICE                              ; Catalog            ; Catalog",
				'$returnItem=returnIntegrationObjectItem(integrationObject(code), code)',
				'$descriptor=attributeDescriptor(enclosingType(code), qualifier)',
				'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; $descriptor  ; $returnItem; unique[default = false]',
				"                                            ; $SERVICE:Catalog                                                   ; id                          ; Catalog:id   ;            ;",
				"                                            ; $SERVICE:Catalog                                                   ; name                        ; Catalog:name ;            ;",
		)
	}

	protected static void givenUserExistsWithUidAndGroups(final String uid, final String groups)
	{
		IntegrationTestUtil.importImpEx(
				'$password=@password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
				'INSERT_UPDATE Employee; UID[unique = true] ; groups(uid)[mode = append] ;$password',
				"                      ; $uid               ; $groups                    ;*:$PASSWORD"
		)
	}

	protected static basicAuthRequest(String path) {
		new BasicAuthRequestBuilder()
				.extensionName(Odata2webservicesConstants.EXTENSIONNAME)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.path(path)
	}
}
